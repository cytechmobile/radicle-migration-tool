package network.radicle.tools.github.commands;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import network.radicle.tools.github.providers.ApplicationPropertiesDefaultProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(name = "issues", description = "Migrate GitHub issues",
        defaultValueProvider = ApplicationPropertiesDefaultProvider.class)
public class IssuesCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(IssuesCommand.class);

    @Override
    public void run() {
        super.run();

        var result = service.migrateIssues();
        var exitCode = 0;
        if (!result) {
            exitCode = 1;
            logger.error("Migration failed!");
        } else {
            logger.info("Migration finished successfully!");
        }
        Quarkus.asyncExit(exitCode);
    }

}
