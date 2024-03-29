package network.radicle.tools.migrate.commands;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Set;

public class MockedTestProfile implements QuarkusTestProfile {
    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Set.of(MockedGitHubClient.class, MockedRadicleClient.class);
    }
}
