package network.radicle.tools.migrate.services.github;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.clients.github.IGitHubClient;
import network.radicle.tools.migrate.clients.radicle.IRadicleClient;
import network.radicle.tools.migrate.core.Timeline;
import network.radicle.tools.migrate.core.github.GitHubComment;
import network.radicle.tools.migrate.core.github.GitHubEvent;
import network.radicle.tools.migrate.core.github.GitHubIssue;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static network.radicle.tools.migrate.core.github.GitHubIssue.STATE_COMPLETED;
import static network.radicle.tools.migrate.core.github.GitHubIssue.STATE_OPEN;
import static network.radicle.tools.migrate.core.github.GitHubIssue.STATE_OTHER;
import static network.radicle.tools.migrate.core.github.GitHubIssue.STATE_SOLVED;

@ApplicationScoped
public class GitHubMigrationService extends AbstractMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(GitHubMigrationService.class);

    @Inject IGitHubClient github;
    @Inject IRadicleClient radicle;
    @Inject Config config;
    @Inject
    FilesService filesService;
    @Inject GitHubMarkdownService markdownService;

    public boolean migrateIssues() {
        var page = 1;
        var hasMoreIssues = true;
        var processedCount = 0;
        var commentsCount = 0;
        var eventsCount = 0;
        var assetsCount = 0;

        var partiallyOrNonMigratedIssues = new HashSet<Long>();
        try {
            var filters = config.getGithub().filters();
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
                List<GitHubIssue> issues = List.of();
                try {
                    issues = github.getIssues(page, filters);
                    for (var issue : issues) {
                        //ignore pull requests and issues that created before the `since` filter
                        if (issue.pullRequest != null || issue.createdAt.isBefore(filters.since())) {
                            continue;
                        }
                        processedCount++;

                        var radIssue = toRadicle(issue);
                        try {
                            //process inline embeds
                            var links = markdownService.extractUrls(issue.body);
                            radIssue.embeds = fetchEmbeds(links);
                            radIssue.description = addEmbedsInline(links, radIssue.description);

                            var id = radicle.createIssue(session, radIssue);
                            // update issue's state
                            if (!STATE_OPEN.equalsIgnoreCase(radIssue.state.status)) {
                                radicle.updateIssue(session, id, new LifecycleAction(radIssue.state));
                            }

                            var comments = getCommentsFor(issue);
                            var events = getEventsFor(issue, true);

                            //now put everything in chronological order
                            var timeline = Stream.concat(comments.stream(), events.stream())
                                    .sorted(Comparator.comparing(Timeline::getCreatedAt))
                                    .toList();

                            commentsCount += comments.size();
                            eventsCount += events.size();
                            assetsCount += radIssue.embeds.size();

                            for (var event : timeline) {
                                List<MarkdownLink> eventLinks = List.of();
                                List<Embed> eventEmbeds = List.of();

                                //fetch any extra information for specific event types
                                if (GitHubEvent.Type.REFERENCED.value.equalsIgnoreCase(event.getType()) ||
                                        GitHubEvent.Type.CLOSED.value.equalsIgnoreCase(event.getType())) {
                                    var e = ((GitHubEvent) event);
                                    if (e.commitUrl != null) {
                                        e.commit = github.getCommit(e.commitId);
                                    }
                                } else if (GitHubEvent.Type.COMMENT.value.equalsIgnoreCase(event.getType())) {
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
                            partiallyOrNonMigratedIssues.add(issue.number);
                            logger.warn("Failed to migrate issue: {}. Error: {}", issue.number, ex.getMessage());
                        }
                    }
                    logger.info("Processed issues: {}, comments: {}, events: {}, assets: {} ...",
                            processedCount, commentsCount, eventsCount, assetsCount);

                    hasMoreIssues = config.getGithub().pageSize() == issues.size();
                    page++;
                } catch (InternalServerErrorException | BadRequestException ex) {
                    var ids = issues.stream().map(i -> i.number).toList();
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

    public boolean migrateWiki() {
        var owner = config.getGithub().owner();
        var repo = config.getGithub().repo();
        var token = config.getGithub().token();
        var path = config.getRadicle().path();

        var resp = github.execSubtreeCmd(owner, repo, token, path);

        return !Strings.isNullOrEmpty(resp);
    }

    private List<GitHubComment> getCommentsFor(GitHubIssue issue) throws Exception {
        var page = 1;
        var hasMore = true;
        var commentsList = new ArrayList<GitHubComment>();
        while (hasMore) {
            var comments = github.getComments(issue.number, page);
            commentsList.addAll(comments);
            hasMore = config.getGithub().pageSize() == comments.size();
            page++;
        }
        return commentsList;
    }

    private List<GitHubEvent> getEventsFor(GitHubIssue issue, boolean timeline) throws Exception {
        var page = 1;
        var hasMore = true;
        var eventsList = new ArrayList<GitHubEvent>();
        while (hasMore) {
            var events = github.getEvents(issue.number, page, timeline);
            eventsList.addAll(events.stream().filter(e -> GitHubEvent.Type.isValid(e.event)).toList());
            hasMore = config.getGithub().pageSize() == events.size();
            page++;
        }
        return eventsList;
    }

    public String getLastRunPropertyName() {
        var radProject = config.getRadicle().project().replace("rad:", "");
        return "github." + config.getGithub().owner() + "." + config.getGithub().repo() + ".radicle." + radProject +
                ".lastRunInMillis";
    }

    private List<Embed> fetchEmbeds(List<MarkdownLink> links) {
        var embeds = new ArrayList<Embed>();
        for (var link: links) {
            var base64 = github.getAssetOrFile(link.url);
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

    public network.radicle.tools.migrate.core.radicle.Issue toRadicle(GitHubIssue issue) {
        var radIssue = new network.radicle.tools.migrate.core.radicle.Issue();

        radIssue.title = issue.title;
        var meta = markdownService.getMetadata(issue);
        radIssue.description = Strings.isNullOrEmpty(meta) ?
                Strings.nullToEmpty(issue.body) :
                meta + "<br/>" + "\n\n" +  Strings.nullToEmpty(issue.body);
        radIssue.labels = issue.labels != null ?
                issue.labels.stream().map(l -> l.name).collect(Collectors.toList()) :
                List.of();

        if (issue.milestone != null) {
            var rLabels = new ArrayList<>(radIssue.labels);
            rLabels.add(issue.milestone.title);
            radIssue.labels = rLabels;
        }

        var reason = "";
        if (STATE_OPEN.equalsIgnoreCase(issue.state)) {
            reason = null;
        } else {
            reason = STATE_COMPLETED.equalsIgnoreCase(issue.stateReason) ? STATE_SOLVED : STATE_OTHER;
        }
        radIssue.state = new network.radicle.tools.migrate.core.radicle.State(issue.state, reason);
        radIssue.assignees = List.of();
        radIssue.embeds = List.of();

        return radIssue;
    }
}
