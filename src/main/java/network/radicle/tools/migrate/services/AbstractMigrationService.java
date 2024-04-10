package network.radicle.tools.migrate.services;

import com.google.common.base.Strings;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.clients.radicle.IRadicleClient;
import network.radicle.tools.migrate.commands.Command;
import network.radicle.tools.migrate.core.radicle.Embed;
import network.radicle.tools.migrate.core.radicle.Issue;
import network.radicle.tools.migrate.core.radicle.Session;
import network.radicle.tools.migrate.core.radicle.actions.CommentEditAction;
import network.radicle.tools.migrate.services.AppStateService.Property;
import network.radicle.tools.migrate.services.AppStateService.Service;
import network.radicle.tools.migrate.services.github.GitHubMarkdownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractMigrationService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMigrationService.class);

    @Inject AppStateService appStateService;
    @Inject protected IRadicleClient radicle;
    @Inject protected Config config;
    @Inject protected GitHubMarkdownService markdownService;

    public Instant getLastRun(Service service) {
        var lastRun = appStateService.getProperty(service, Property.LAST_RUN);
        return Strings.isNullOrEmpty(lastRun) ?
            Instant.EPOCH : Instant.ofEpochMilli(Long.parseLong(lastRun));
    }

    public void setLastRun(Service service, Instant lastRun) {
        appStateService.setProperty(service, Property.LAST_RUN, String.valueOf(lastRun.toEpochMilli()));
    }

    protected String addEmbedsInline(List<MarkdownService.MarkdownLink> links, String body) {
        for (var link : links) {
            if (!Strings.isNullOrEmpty(link.oid)) {
                body = body.replace(link.url, link.oid);
            }
        }
        return body;
    }

    protected Map<String, Issue> loadIssuesCache(Session session) throws Exception {
        var openIssues = radicle.getIssues(session, Command.State.open.name());
        var closedIssues = radicle.getIssues(session, Command.State.closed.name());
        return Stream.of(openIssues, closedIssues)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Issue::getId, Function.identity(), (first, second) -> first));
    }

    protected void updateComment(Session session, String issueId, String commentId, String body,
                               List<Embed> embeds, Issue cached) throws Exception {
        if (cached != null) {
            //check the description has been changed to avoid unnecessary comment edits
            for (var comment : cached.discussion) {
                if (commentId.equals(comment.id)) {
                    if (!comment.body.equalsIgnoreCase(body)) {
                        logger.debug("Updating comment {}", commentId);
                        radicle.updateIssue(session, issueId, new CommentEditAction(
                                commentId, body, embeds));
                    }
                    break;
                }
            }
        }
    }

    protected static Instant getLastEventTimestamp(Issue cachedIssue) {
        Instant lastEventTimestamp = null;
        if (cachedIssue != null) {
            var discussion = cachedIssue.discussion;
            var lastEvent = discussion.get(discussion.size() - 1);
            lastEventTimestamp = lastEvent != null ?
                    Instant.ofEpochSecond(Long.parseLong(lastEvent.timestamp)) : null;
        }
        return lastEventTimestamp;
    }
}
