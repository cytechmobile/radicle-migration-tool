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
import network.radicle.tools.migrate.core.radicle.actions.CommentAction;
import network.radicle.tools.migrate.core.radicle.actions.LifecycleAction;
import network.radicle.tools.migrate.services.AbstractMigrationService;
import network.radicle.tools.migrate.services.FilesService;
import network.radicle.tools.migrate.services.MarkdownService.MarkdownLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static network.radicle.tools.migrate.core.gitlab.GitLabIssue.STATE_OPENED;
import static network.radicle.tools.migrate.core.gitlab.GitLabIssue.STATE_SOLVED;

@ApplicationScoped
public class GitLabMigrationService extends AbstractMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(GitLabMigrationService.class);

    @Inject IGitLabClient gitlab;
    @Inject IRadicleClient radicle;
    @Inject Config config;
    @Inject
    FilesService filesService;
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
            var filters = config.getGitlab().filters();
            if (filters.since() == null) {
                filters = filters.withSince(getLastRun());
            }
            logger.info("Migration started with filters: {}", filters);

            var session = radicle.createSession();
            if (session == null) {
                logger.error("Session could not get authenticated.");
                logger.info("Hint: To setup your radicle profile and register your key with the ssh-agent, run `rad auth`.");
                return false;
            }

            logger.debug("Radicle session created: {}", session.id);

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

                            var id = radicle.createIssue(session, radIssue);
                            // update issue's state
                            if (!STATE_OPENED.equalsIgnoreCase(radIssue.state.status)) {
                                radicle.updateIssue(session, id, new LifecycleAction(radIssue.state));
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

                                //fetch any extra information for specific event types
                                if (Type.COMMENT.value.equalsIgnoreCase(event.getType())) {
                                    //process inline embeds
                                    eventLinks = markdownService.extractUrls(event.getBody());
                                    eventEmbeds = fetchEmbeds(eventLinks);
                                }
                                var bodyWithMetadata = markdownService.getBodyWithMetadata(event);
                                var bodyWithEmbeds = addEmbedsInline(eventLinks, bodyWithMetadata);
                                radicle.updateIssue(session, id, new CommentAction(bodyWithEmbeds, eventEmbeds, id));

                                assetsCount += eventEmbeds.size();
                            }
                        } catch (Exception ex) {
                            partiallyOrNonMigratedIssues.add(issue.iid);
                            logger.warn("Failed to migrate issue: {}. Error: {}", issue.iid, ex.getMessage());
                        }
                    }
                    logger.info("Processed issues: {}, comments: {}, events: {}, assets: {} ...",
                            processedCount, commentsCount, eventsCount, assetsCount);

                    hasMoreIssues = config.getGitlab().pageSize() == issues.size();
                    page++;
                } catch (InternalServerErrorException | BadRequestException ex) {
                    var ids = issues.stream().map(i -> i.iid).toList();
                    partiallyOrNonMigratedIssues.addAll(ids);
                    logger.warn("Failed to migrate issues: {}. Error: {}", ids, ex.getMessage());
                    page++;
                }
            }

            if (!config.getRadicle().dryRun()) {
                setLastRun(Instant.now());
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
            hasMore = config.getGitlab().pageSize() == comments.size();
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
            hasMore = config.getGitlab().pageSize() == events.size();
            page++;
        }
        return eventsList;
    }

    public String getLastRunPropertyName() {
        var radProject = config.getRadicle().project().replace("rad:", "");
        return "gitlab." + config.getGitlab().namespace() + "." + config.getGitlab().project() + ".radicle." + radProject +
                ".lastRunInMillis";
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

    private String addEmbedsInline(List<MarkdownLink> links, String body) {
        for (var link : links) {
            if (!Strings.isNullOrEmpty(link.oid)) {
                body = body.replace(link.url, link.oid);
            }
        }
        return body;
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
            rLabels.add(issue.type.toLowerCase());
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
