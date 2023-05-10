package network.radicle.tools.github.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.clients.IGitHubClient;
import network.radicle.tools.github.clients.IRadicleClient;
import network.radicle.tools.github.core.github.Issue;
import network.radicle.tools.github.core.radicle.actions.CommentAction;
import network.radicle.tools.github.core.radicle.actions.LifecycleAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class MigrationService {
    private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
    private static final String DATE_PATTERN_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Inject IGitHubClient github;
    @Inject IRadicleClient radicle;
    @Inject Config config;

    public boolean migrateIssues() {
        var page = 1;
        var hasMoreIssues = true;
        var total = 0;

        var partiallyOrNonMigratedIssues = new HashSet<Long>();
        try {
            var session = radicle.createSession();
            logger.info("Created radicle session: {}", session.id);
            while (hasMoreIssues) {
                List<Issue> issues = List.of();
                try {
                    issues = github.getIssues(page);
                    var batchSize = issues.size();
                    logger.debug("Migrating page: {} with size: {}", page, batchSize);
                    for (var issue : issues) {
                        //ignore pull requests
                        if (issue.pullRequest != null) {
                            continue;
                        }
                        total++;

                        var radIssue = issue.toRadicle();
                        try {
                            var id = radicle.createIssue(session, radIssue);
                            // update issue's state
                            if (!Issue.STATE_OPEN.equalsIgnoreCase(radIssue.state.status)) {
                                radicle.updateIssue(session, id, new LifecycleAction(radIssue.state));
                            }

                            // process issue's comments
                            var commentsPage = 1;
                            var hasMoreComments = true;
                            while (hasMoreComments) {
                                var comments = github.getComments(issue.number, commentsPage);
                                for (var comment : comments) {
                                    radicle.updateIssue(session, id, new CommentAction(comment.getBodyWithMeta()));
                                }
                                hasMoreComments = config.getGithub().pageSize() == comments.size();
                                commentsPage++;
                            }
                        } catch (Exception ex) {
                            partiallyOrNonMigratedIssues.add(issue.number);
                            logger.warn("Failed to migrate issue: {}, error: {}", issue.number, ex.getMessage());
                        }
                    }
                    logger.info("Total processed until now: {}", total);
                    hasMoreIssues = config.getGithub().pageSize() == issues.size();
                    page++;
                } catch (InternalServerErrorException | BadRequestException ex) {
                    var ids = issues.stream().map(i -> i.number).toList();
                    partiallyOrNonMigratedIssues.addAll(ids);
                    logger.warn("Failed to migrate issues: {}, error: {}", ids, ex.getMessage());
                    page++;
                }
            }
            logger.info("Total processed issues: {}", total);
            if (partiallyOrNonMigratedIssues.size() > 0) {
                logger.warn("Partially or non migrated issues: {}", partiallyOrNonMigratedIssues);
            }
            return true;
        } catch (Exception ex) {
            logger.error("Migration failed: {}", ex.getMessage());
            return false;
        }
    }

}
