package network.radicle.tools.github.commands;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.clients.IGitHubClient;
import network.radicle.tools.github.clients.IRadicleClient;
import network.radicle.tools.github.core.github.*;
import network.radicle.tools.github.core.radicle.Session;
import network.radicle.tools.github.core.radicle.actions.Action;
import network.radicle.tools.github.core.radicle.actions.EmbedTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusMainTest
@TestProfile(IssuesCommandIT.GithubTestProfile.class)
public class IssuesCommandIT {
    @Test
    public void testSuccess(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption);

        var expectedIssues = 6;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testSinceFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "2021-01-01T00:00:00+00:00";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption);

        var expectedIssues = 2;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testSinceFilterValidation(QuarkusMainLauncher launcher) {
        var sinceOption = "invalid";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption);

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getErrorOutput()).contains("Invalid value for option '--filter-since'");
    }

    @Test
    public void testMilestoneFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var milestone = 1;
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fm=" + milestone);

        var expectedIssues = 1;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testMilestoneFilterValidation(QuarkusMainLauncher launcher) {
        var milestone = "invalid";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fm=" + milestone);

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getErrorOutput()).contains("Invalid value for option '--filter-milestone'");
    }

    @Test
    public void testAssigneeFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var assignee = "testuser";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fa=" + assignee);

        var expectedIssues = 1;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testCreatorFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var creator = "testcontributor";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fc=" + creator);

        var expectedIssues = 2;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testStateFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-ft=" + Command.State.open.name());

        var expectedIssues = 1;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testStateFilterValidation(QuarkusMainLauncher launcher) {
        var state = "invalid";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-ft=" + state);

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getErrorOutput()).contains("Invalid value for option '--filter-state'");
    }

    @Test
    public void testLabelsFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var labels = "release 1.0,other";
        var result = launcher.launch("issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fl=" + labels);

        var expectedIssues = 2;

        verifyOutput(result, expectedIssues);
    }

    private static void verifyOutput(LaunchResult result, int expectedIssues) {
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.getErrorOutput()).isEmpty();

        var expectedComments = expectedIssues * CommentTest.loadGitHubComments().size();
        var expectedEvents = expectedIssues * EventTest.loadGitHubEvents().stream()
                .filter(e -> Event.Type.isValid(e.event)).toList().size();

        assertThat(result.getOutput()).contains("Totally processed issues: " + expectedIssues +
                ", comments: " + expectedComments + ", events: " + expectedEvents);

        assertThat(result.getOutput()).doesNotContain("Partially or non migrated issues");
        assertThat(result.getOutput()).contains("Migration finished successfully");
    }

    public static class GithubTestProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(MockedGitHubClient.class, MockedRadicleClient.class);
        }
    }

    @Alternative
    @Singleton
    public static class MockedGitHubClient implements IGitHubClient {
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
    }

    @Alternative
    @Singleton
    public static class MockedRadicleClient implements IRadicleClient {
        private static final Logger logger = LoggerFactory.getLogger(MockedRadicleClient.class);

        @Override
        public Session createSession() {
            var session = new Session();
            session.id = UUID.randomUUID().toString();
            logger.info("Creating session: {}", session.id);
            return session;
        }

        @Override
        public String createIssue(Session session, network.radicle.tools.github.core.radicle.Issue issue) {
            var id = UUID.randomUUID().toString();
            logger.info("Creating issue: {}", id);
            return id;
        }

        @Override
        public boolean updateIssue(Session session, String id, Action action) {
            logger.info("Updating issue: {} using action: {}", id, action);
            return true;
        }
    }

}
