package network.radicle.tools.github.commands;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Singleton;
import network.radicle.tools.github.clients.IGitHubClient;
import network.radicle.tools.github.core.Issue;
import network.radicle.tools.github.core.IssueTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusMainTest
@TestProfile(IssuesCommandIT.GithubTestProfile.class)
class IssuesCommandIT {
    @Test
    public void testManualLaunch(QuarkusMainLauncher launcher) {
        LaunchResult result = launcher.launch("issues", "-o=testOwner", "-r=testRepo");

        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.getErrorOutput()).isEmpty();
        assertThat(result.getOutput()).contains("Migration finished successfully");
    }

    public static class GithubTestProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(MockedGitHubClient.class);
        }
    }

    @Alternative
    @Singleton
    public static class MockedGitHubClient implements IGitHubClient {
        private static final Logger logger = LoggerFactory.getLogger(MockedGitHubClient.class);

        @Override
        public List<Issue> getIssues(int page) throws Exception {
            var issues = IssueTest.loadGitHubIssues();
            logger.info("Returning {} issues for page {}", issues.size(), page);
            return issues;
        }
    }

}
