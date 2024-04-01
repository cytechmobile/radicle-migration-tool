package network.radicle.tools.migrate.commands;

import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.clients.IGitHubClient;
import network.radicle.tools.migrate.core.github.*;
import network.radicle.tools.migrate.core.radicle.actions.EmbedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Alternative
@Singleton
public class MockedGitHubClient implements IGitHubClient {
    private static final Logger logger = LoggerFactory.getLogger(MockedGitHubClient.class);

    @Override
    public List<Issue> getIssues(int page, Config.Filters filters) {
        var issues = IssueTest.loadGitHubIssues().stream()
                .filter(i -> {
                    var matchesMilestone = filters.milestone() == null || (i.milestone != null && filters.milestone().equals(i.milestone.number));
                    var matchesState = filters.state() == Command.State.all || filters.state().name().equals(i.state);
                    var matchesAssignee = filters.assignee() == null || (i.assignee != null && filters.assignee().equals(i.assignee.login));
                    var matchesAssignees = filters.assignee() == null ||
                            !i.assignees.stream().filter(a -> a.login.equals(filters.assignee())).toList().isEmpty();
                    var matchesCreator = filters.creator() == null || (i.user != null && filters.creator().equals(i.user.login));

                    var filterLabels = filters.labels() == null ? List.of() :
                            Arrays.stream(filters.labels().split(",")).toList();
                    var issueLabels = i.labels.stream().map(l -> l.name).toList();

                    var matchesLabels = filters.labels() == null ||
                            !filterLabels.stream().filter(issueLabels::contains).toList().isEmpty();
                    if (matchesLabels) {
                        logger.debug("{} matches {}", filterLabels, issueLabels);
                    }

                    return matchesMilestone && matchesState && (matchesAssignee || matchesAssignees) &&
                            matchesCreator && matchesLabels;
                }).toList();
        logger.info("Returning {} issues for page {}", issues.size(), page);
        return issues;
    }

    @Override
    public List<Comment> getComments(long issueNumber, int page) {
        var comments = CommentTest.loadGitHubComments();
        logger.info("Returning {} comments for page {}", comments.size(), page);
        return comments;
    }

    @Override
    public List<Event> getEvents(long issueNumber, int page, boolean timeline) throws Exception {
        var events = EventTest.loadGitHubEvents();
        logger.info("Returning {} events for page {}", events.size(), page);
        return events;
    }

    @Override
    public Commit getCommit(String commitId) throws Exception {
        return CommitTest.generateGitHubCommit();
    }

    @Override
    public String getAssetOrFile(String url) {
        var embed = EmbedTest.generateEmbed();
        return embed != null ? embed.content : null;
    }

    @Override
    public String execSubtreeCmd(String owner, String repo, String token, String path) {
        logger.info("executing git subtree command for owner: {}, repo: {}, token: {} into path: {}",
                owner, repo, token, path);
        return "success";
    }
}