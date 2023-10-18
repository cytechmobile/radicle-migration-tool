package network.radicle.tools.github.services;

import com.google.common.base.Strings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.clients.IGitHubClient;
import network.radicle.tools.github.clients.IRadicleClient;
import network.radicle.tools.github.core.github.Comment;
import network.radicle.tools.github.core.github.Event;
import network.radicle.tools.github.core.github.Issue;
import network.radicle.tools.github.core.github.Timeline;
import network.radicle.tools.github.core.radicle.Embed;
import network.radicle.tools.github.core.radicle.actions.CommentAction;
import network.radicle.tools.github.core.radicle.actions.LifecycleAction;
import network.radicle.tools.github.utils.Markdown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@ApplicationScoped
public class MigrationService extends AbstractMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);

    @Inject IGitHubClient github;
    @Inject IRadicleClient radicle;
    @Inject Config config;

    public boolean migrateIssues() {
        var page = 1;
        var hasMoreIssues = true;
        var processedCount = 0;
        var commentsCount = 0;
        var eventsCount = 0;

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
                List<Issue> issues = List.of();
                try {
                    issues = github.getIssues(page, filters);
                    for (var issue : issues) {
                        //ignore pull requests and issues that created before the `since` filter
                        if (issue.pullRequest != null || issue.createdAt.isBefore(filters.since())) {
                            continue;
                        }
                        processedCount++;

                        var radIssue = issue.toRadicle();
                        try {
                            radIssue.embeds = fetchEmbeds(issue.body);
                            var id = radicle.createIssue(session, radIssue);
                            // update issue's state
                            if (!Issue.STATE_OPEN.equalsIgnoreCase(radIssue.state.status)) {
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

                            for (var event : timeline) {
                                List<Embed> embeds = List.of();

                                //fetch any extra information for specific event types
                                if (Event.Type.REFERENCED.value.equalsIgnoreCase(event.getType()) ||
                                        Event.Type.CLOSED.value.equalsIgnoreCase(event.getType())) {
                                    var e = ((Event) event);
                                    if (e.commitUrl != null) {
                                        e.commit = github.getCommit(e.commitId);
                                    }
                                } else if (Event.Type.COMMENT.value.equalsIgnoreCase(event.getType())) {
                                    embeds = fetchEmbeds(event.getBody());
                                }
                                radicle.updateIssue(session, id, new CommentAction(event.getBodyWithMetadata(), embeds, id));
                            }
                        } catch (Exception ex) {
                            partiallyOrNonMigratedIssues.add(issue.number);
                            logger.warn("Failed to migrate issue: {}. Error: {}", issue.number, ex.getMessage());
                        }
                    }
                    logger.info("Processed issues: {}, comments: {}, events: {} ...", processedCount, commentsCount, eventsCount);

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
            logger.info("Totally processed issues: {}, comments: {}, events: {}", processedCount, commentsCount, eventsCount);
            return true;
        } catch (Exception ex) {
            logger.error("Migration failed", ex);
            return false;
        }
    }

    private List<Comment> getCommentsFor(Issue issue) throws Exception {
        var page = 1;
        var hasMore = true;
        var commentsList = new ArrayList<Comment>();
        while (hasMore) {
            var comments = github.getComments(issue.number, page);
            commentsList.addAll(comments);
            hasMore = config.getGithub().pageSize() == comments.size();
            page++;
        }
        return commentsList;
    }

    private List<Event> getEventsFor(Issue issue, boolean timeline) throws Exception {
        var page = 1;
        var hasMore = true;
        var eventsList = new ArrayList<Event>();
        while (hasMore) {
            var events = github.getEvents(issue.number, page, timeline);
            eventsList.addAll(events.stream().filter(e -> Event.Type.isValid(e.event)).toList());
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

    private List<Embed> fetchEmbeds(String body) {
        var embeds = new ArrayList<Embed>();
        var links = Markdown.extractUrls(body);
        for (var link: links) {
            var base64 = github.getAssetOrFile(link.url());
            if (!Strings.isNullOrEmpty(base64)) {
                embeds.add(new Embed(link.text(), base64));
            }
        }
        return embeds;
    }

}
