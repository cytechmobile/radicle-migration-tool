package network.radicle.tools.migrate.commands;

import io.quarkus.test.junit.main.LaunchResult;
import io.quarkus.test.junit.main.QuarkusMainLauncher;
import io.quarkus.test.junit.main.QuarkusMainTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusMainTest
@io.quarkus.test.junit.TestProfile(MockedTestProfile.class)
public class WikiCommandIT {
    @Test
    public void testSuccess(QuarkusMainLauncher launcher) {
        var result = launcher.launch("wiki", "-go=testOwner", "-gr=testRepo", "-rpp=/home/user/projects/target");

        verifyOutput(result);
    }

    private static void verifyOutput(LaunchResult result) {
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.getErrorOutput()).isEmpty();

        assertThat(result.getOutput()).contains("Migration finished successfully");
    }
}
