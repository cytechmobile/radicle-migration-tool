package network.radicle.tools.migrate.commands;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import network.radicle.tools.migrate.core.github.CommentTest;
import network.radicle.tools.migrate.core.github.EventTest;
import network.radicle.tools.migrate.core.github.GitHubEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusMainTest
@io.quarkus.test.junit.TestProfile(MockedTestProfile.class)
public class GitHubIssuesCommandIT {
    @Test
    public void testSuccess(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption);

        var expectedIssues = 6;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testSinceFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "2021-01-01T00:00:00+00:00";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption);

        var expectedIssues = 2;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testSinceFilterValidation(QuarkusMainLauncher launcher) {
        var sinceOption = "invalid";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption);

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getErrorOutput()).contains("Invalid value for option '--filter-since'");
    }

    @Test
    public void testMilestoneFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var milestone = 1;
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fm=" + milestone);

        var expectedIssues = 1;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testMilestoneFilterValidation(QuarkusMainLauncher launcher) {
        var milestone = "invalid";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fm=" + milestone);

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getErrorOutput()).contains("Invalid value for option '--filter-milestone'");
    }

    @Test
    public void testAssigneeFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var assignee = "testuser";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fa=" + assignee);

        var expectedIssues = 1;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testCreatorFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var creator = "testcontributor";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fc=" + creator);

        var expectedIssues = 2;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testStateFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-ft=" + Command.State.open.name());

        var expectedIssues = 1;

        verifyOutput(result, expectedIssues);
    }

    @Test
    public void testStateFilterValidation(QuarkusMainLauncher launcher) {
        var state = "invalid";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-ft=" + state);

        assertThat(result.exitCode()).isNotZero();
        assertThat(result.getErrorOutput()).contains("Invalid value for option '--filter-state'");
    }

    @Test
    public void testLabelsFilter(QuarkusMainLauncher launcher) {
        var sinceOption = "1970-01-01T00:00:00+00:00";

        var labels = "release 1.0,other";
        var result = launcher.launch("github", "issues", "-go=testOwner", "-gr=testRepo", "-rp=testProject",
                "-fs=" + sinceOption, "-fl=" + labels);

        var expectedIssues = 2;

        verifyOutput(result, expectedIssues);
    }

    private static void verifyOutput(LaunchResult result, int expectedIssues) {
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.getErrorOutput()).isEmpty();

        var expectedComments = expectedIssues * CommentTest.loadGitHubComments().size();
        var expectedEvents = expectedIssues * EventTest.loadGitHubEvents().stream()
                .filter(e -> GitHubEvent.Type.isValid(e.event)).toList().size();

        assertThat(result.getOutput()).contains("Totally processed issues: " + expectedIssues +
                ", comments: " + expectedComments + ", events: " + expectedEvents);

        assertThat(result.getOutput()).doesNotContain("Partially or non migrated issues");
        assertThat(result.getOutput()).contains("Migration finished successfully");
    }
}
