package network.radicle.tools.migrate.commands;

import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.clients.gitlab.IGitLabClient;
import network.radicle.tools.migrate.core.gitlab.*;
import network.radicle.tools.migrate.core.radicle.actions.EmbedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

@Alternative
@Singleton
public class MockedGitLabClient implements IGitLabClient {
    private static final Logger logger = LoggerFactory.getLogger(MockedGitLabClient.class);

    @Override
    public List<GitLabIssue> getIssues(int page, Config.Filters filters) {
        logger.info("MockedGitLabClient MockedGitLabClient MockedGitLabClient");
        var issues = IssueTest.loadGitLabIssues().stream()
                .filter(i -> {
                    var matchesMilestone = filters.milestone() == null || (i.milestone != null && String.valueOf(filters.milestone()).equals(i.milestone.title));
                    var state = filters.state().name().equals(Command.State.open.name()) ? "opened" : filters.state().name();
                    var matchesState = state.equals(Command.State.all.name()) || state.equals(i.state);
                    var matchesAssignee = filters.assignee() == null || (i.assignee != null && filters.assignee().equals(i.assignee.username));
                    var matchesAssignees = filters.assignee() == null ||
                            !i.assignees.stream().filter(a -> a.username.equals(filters.assignee())).toList().isEmpty();
                    var matchesCreator = filters.creator() == null || (i.author != null && filters.creator().equals(i.author.username));

                    var filterLabels = filters.labels() == null ? List.of() :
                            Arrays.stream(filters.labels().split(",")).toList();
                    var issueLabels = i.labels;

                    var matchesLabels = filters.labels() == null ||
                            !filterLabels.stream().filter(issueLabels::contains).toList().isEmpty();
                    if (matchesLabels) {
                        logger.debug("{} matches {}", filterLabels, issueLabels);
                    }

                    return matchesMilestone && matchesState && (matchesAssignee || matchesAssignees) &&
                            matchesCreator && matchesLabels;
                }).toList();
        return issues;
    }

    @Override
    public List<GitLabComment> getComments(long issueNumber, int page) {
        var comments = CommentTest.loadGitLabComments();
        return comments;
    }

    @Override
    public List<GitLabEvent> getEvents(long issueNumber, int page, GitLabEvent.Type type) throws Exception {
        var events = EventTest.loadGitLabEvents();
        return events;
    }

    @Override
    public String getAssetOrFile(String url) {
        var embed = EmbedTest.generateEmbed();
        return embed != null ? embed.content : null;
    }
}
