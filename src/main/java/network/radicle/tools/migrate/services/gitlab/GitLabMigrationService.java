package network.radicle.tools.migrate.services.gitlab;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.clients.gitlab.IGitLabClient;
import network.radicle.tools.migrate.clients.radicle.IRadicleClient;
import network.radicle.tools.migrate.core.Timeline;
import network.radicle.tools.migrate.core.gitlab.GitLabComment;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent;
import network.radicle.tools.migrate.core.gitlab.GitLabEvent.Type;
import network.radicle.tools.migrate.core.gitlab.GitLabIssue;
import network.radicle.tools.migrate.core.radicle.Embed;
import network.radicle.tools.migrate.core.radicle.Issue;
import network.radicle.tools.migrate.core.radicle.State;
import network.radicle.tools.migrate.core.radicle.actions.CommentAction;
import network.radicle.tools.migrate.core.radicle.actions.EditAction;
import network.radicle.tools.migrate.core.radicle.actions.LabelAction;
import network.radicle.tools.migrate.core.radicle.actions.LifecycleAction;
import network.radicle.tools.migrate.services.AbstractMigrationService;
import network.radicle.tools.migrate.services.FilesService;
import network.radicle.tools.migrate.services.MarkdownService.MarkdownLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static network.radicle.tools.migrate.core.gitlab.GitLabIssue.STATE_OPEN;
import static network.radicle.tools.migrate.core.gitlab.GitLabIssue.STATE_OPENED;
import static network.radicle.tools.migrate.core.gitlab.GitLabIssue.STATE_SOLVED;

@ApplicationScoped
public class GitLabMigrationService extends AbstractMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(GitLabMigrationService.class);

    @Inject IGitLabClient gitlab;
    @Inject IRadicleClient radicle;
    @Inject Config config;
    @Inject FilesService filesService;
    @Inject GitLabMarkdownService markdownService;

    public boolean migrateIssues() {
        var page = 1;
        var hasMoreIssues = true;
        var processedCount = 0;
        var commentsCount = 0;
        var eventsCount = 0;
        var assetsCount = 0;

        var partiallyOrNonMigratedIssues = new HashSet<Long>();
        try {
            var filters = config.gitlab().filters();
            if (filters.since() == null) {
                filters = filters.withSince(Instant.EPOCH);
            }
            logger.info("Migration started with filters: {}", filters);

            var session = radicle.createSession();
            if (session == null) {
                logger.error("Session could not get authenticated.");
                logger.info("Hint: To setup your radicle profile and register your key with the ssh-agent, run `rad auth`.");
                return false;
            }

            logger.debug("Radicle session created: {}", session.id);

            var issuesCache = loadIssuesCache(session);
            var issuesMapper = calculateIssuesMapping(issuesCache);

            while (hasMoreIssues) {
                List<GitLabIssue> issues = List.of();
                try {
                    issues = gitlab.getIssues(page, filters);
                    for (var issue : issues) {
                        processedCount++;

                        var radIssue = toRadicle(issue);
                        try {
                            //process inline embeds
                            var links = markdownService.extractUrls(issue.description);
                            radIssue.embeds = fetchEmbeds(links);
                            radIssue.description = addEmbedsInline(links, radIssue.description);

                            //check if the issue is already synced in the past and update it
                            var id = issuesMapper.get("issue." + issue.iid);
                            var cachedIssue = !Strings.isNullOrEmpty(id) ? issuesCache.get(id) : null;
                            if (Strings.isNullOrEmpty(id)) {
                                id = radicle.createIssue(session, radIssue);
                                if (!STATE_OPENED.equalsIgnoreCase(radIssue.state.status)) {
                                    radicle.updateIssue(session, id, new LifecycleAction(radIssue.state));
                                }
                            } else {
                                radicle.updateIssue(session, id, new EditAction(radIssue.title));
                                var state = STATE_OPENED.equalsIgnoreCase(radIssue.state.status) ?
                                        new State(STATE_OPEN, "") : radIssue.state;
                                radicle.updateIssue(session, id, new LifecycleAction(state));
                                radicle.updateIssue(session, id, new LabelAction(radIssue.labels));
                                //issue description is stored as a comment with the same as the issue id
                                //check the description has been changed to avoid unnecessary comment edits
                                updateComment(session, id, id, radIssue.description, radIssue.embeds, cachedIssue);
                            }

                            var comments = getCommentsFor(issue);
                            var milestoneEvents = getEventsFor(issue, Type.MILESTONE);
                            var labelEvents = getEventsFor(issue, Type.LABEL);
                            var stateEvents = getEventsFor(issue, Type.STATE);

                            //now put everything in chronological order
                            var milestonesAndComments = Stream.concat(comments.stream(), milestoneEvents.stream());
                            var labelsAndState = Stream.concat(labelEvents.stream(), stateEvents.stream());
                            var timeline = Stream.concat(milestonesAndComments, labelsAndState)
                                    .sorted(Comparator.comparing(Timeline::getCreatedAt))
                                    .toList();

                            commentsCount += comments.size();
                            eventsCount += milestoneEvents.size() + labelEvents.size() + stateEvents.size();
                            assetsCount += radIssue.embeds.size();

                            for (var event : timeline) {
                                List<MarkdownLink> eventLinks = List.of();
                                List<Embed> eventEmbeds = List.of();

                                var isComment = Type.COMMENT.value.equalsIgnoreCase(event.getType());

                                //fetch any extra information for specific event types
                                if (isComment) {
                                    //process inline embeds
                                    eventLinks = markdownService.extractUrls(event.getBody());
                                    eventEmbeds = fetchEmbeds(eventLinks);
                                }
                                var bodyWithMetadata = markdownService.getBodyWithMetadata(event);
                                var bodyWithEmbeds = addEmbedsInline(eventLinks, bodyWithMetadata);

                                if (isComment) {
                                    var commentId = issuesMapper.get("comment." + event.getId());
                                    if (Strings.isNullOrEmpty(commentId)) {
                                        radicle.updateIssue(session, id, new CommentAction(bodyWithEmbeds, eventEmbeds, id));
                                    } else {
                                        updateComment(session, id, commentId, bodyWithEmbeds, eventEmbeds, cachedIssue);
                                    }
                                } else {
                                    var lastEventTimestamp = getLastEventTimestamp(cachedIssue);
                                    var eventTimestamp = event.getCreatedAt();
                                    if (lastEventTimestamp == null || lastEventTimestamp.isBefore(eventTimestamp)) {
                                        radicle.updateIssue(session, id, new CommentAction(bodyWithEmbeds, eventEmbeds, id));
                                    }
                                }

                                assetsCount += eventEmbeds.size();
                            }
                        } catch (Exception ex) {
                            partiallyOrNonMigratedIssues.add(issue.iid);
                            logger.warn("Failed to migrate issue: {}. Error: {}", issue.iid, ex.getMessage());
                        }
                    }
                    logger.info("Processed issues: {}, comments: {}, events: {}, assets: {} ...",
                            processedCount, commentsCount, eventsCount, assetsCount);

                    hasMoreIssues = config.gitlab().pageSize() == issues.size();
                    page++;
                } catch (InternalServerErrorException | BadRequestException ex) {
                    var ids = issues.stream().map(i -> i.iid).toList();
                    partiallyOrNonMigratedIssues.addAll(ids);
                    logger.warn("Failed to migrate issues: {}. Error: {}", ids, ex.getMessage());
                    page++;
                }
            }

            if (!partiallyOrNonMigratedIssues.isEmpty()) {
                logger.warn("Partially or non migrated issues: {}", partiallyOrNonMigratedIssues);
            }
            logger.info("Totally processed issues: {}, comments: {}, events: {}, assets: {}",
                    processedCount, commentsCount, eventsCount, assetsCount);
            return true;
        } catch (Exception ex) {
            logger.error("Migration failed", ex);
            return false;
        }
    }

    private List<GitLabComment> getCommentsFor(GitLabIssue issue) throws Exception {
        var page = 1;
        var hasMore = true;
        var commentsList = new ArrayList<GitLabComment>();
        while (hasMore) {
            var comments = gitlab.getComments(issue.iid, page);
            commentsList.addAll(comments);
            hasMore = config.gitlab().pageSize() == comments.size();
            page++;
        }
        return commentsList;
    }

    private List<GitLabEvent> getEventsFor(GitLabIssue issue, Type type) throws Exception {
        var page = 1;
        var hasMore = true;
        var eventsList = new ArrayList<GitLabEvent>();
        while (hasMore) {
            var events = gitlab.getEvents(issue.iid, page, type);
            eventsList.addAll(events.stream().filter(e -> GitLabEvent.Type.isValid(e.getType())).toList());
            hasMore = config.gitlab().pageSize() == events.size();
            page++;
        }
        return eventsList;
    }

    private List<Embed> fetchEmbeds(List<MarkdownLink> links) {
        var embeds = new ArrayList<Embed>();
        for (var link: links) {
            var base64 = gitlab.getAssetOrFile(link.url);
            if (!Strings.isNullOrEmpty(base64)) {
                var oid = filesService.calculateGitObjectId(base64);
                if (!Strings.isNullOrEmpty(oid)) {
                    link.oid = oid;
                }
                embeds.add(new Embed(oid, link.text, base64));
            }
        }
        return embeds;
    }

    private Map<String, String> calculateIssuesMapping(Map<String, Issue> issuesCache) {
        var mapping = new HashMap<String, String>();
        var issues = issuesCache.values().stream().sorted(Comparator.comparing(Issue::getCreatedAt)).toList();
        for (var issue : issues) {
            var discussion = issue.discussion;
            for (var idx = 0; idx < discussion.size(); idx++) {
                var disc = discussion.get(idx);
                var links = markdownService.extractUrls(disc.body);
                if (!links.isEmpty()) {
                    var separator = idx == 0 ? "/" : "_";
                    var prefix = idx == 0 ? "issue" : "comment";
                    var path = idx == 0 ? "/issues" : "#note";
                    for (var link : links) {
                        if (link.url.contains(path)) {
                            var segments = link.url.split(separator);
                            var id = segments[segments.length - 1];
                            mapping.put(prefix + "." + id, disc.id);
                            break;
                        }
                    }
                }
            }
        }
        return mapping;
    }

    public network.radicle.tools.migrate.core.radicle.Issue toRadicle(GitLabIssue issue) {
        var radIssue = new network.radicle.tools.migrate.core.radicle.Issue();

        radIssue.title = issue.title;
        var meta = markdownService.getMetadata(issue);
        radIssue.description = Strings.isNullOrEmpty(meta) ?
                Strings.nullToEmpty(issue.description) :
                meta + "<br/>" + "\n\n" +  Strings.nullToEmpty(issue.description);
        radIssue.labels = issue.labels != null ? issue.labels : List.of();

        if (issue.milestone != null) {
            var rLabels = new ArrayList<>(radIssue.labels);
            if (!"issue".equalsIgnoreCase(issue.type)) {
                rLabels.add(issue.type.toLowerCase());
            }
            rLabels.add(issue.milestone.title);
            radIssue.labels = rLabels;
        }

        var reason = "";
        if (STATE_OPENED.equalsIgnoreCase(issue.state)) {
            reason = null;
        } else {
            reason = STATE_SOLVED;
        }

        radIssue.state = new network.radicle.tools.migrate.core.radicle.State(issue.state, reason);
        radIssue.assignees = List.of();
        radIssue.embeds = List.of();

        return radIssue;
    }
}
