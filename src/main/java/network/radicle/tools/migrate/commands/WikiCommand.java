package network.radicle.tools.migrate.commands;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import network.radicle.tools.migrate.Config.GitHubConfig;
import network.radicle.tools.migrate.Config.RadicleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "wiki",
        description = "Migrate a GitHub Wiki to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n")
public class WikiCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(WikiCommand.class);

    @CommandLine.Option(
            names = {"-rpp", "--radicle-project-path"},
            order = 170,
            required = true,
            defaultValue = "${RAD_PROJECT_PATH}",
            description = "The absolute path to the target Radicle project in your local file system.")
    String rProjectPath;

    @Override
    public void exec() {
        var result = service.migrateWiki();
        if (!result) {
            logger.error("Migration failed.");
            Quarkus.asyncExit(1);
        } else {
            logger.info("Migration finished successfully!");
            Quarkus.asyncExit(0);
        }
    }

    @Override
    public GitHubConfig getGithubConfig() {
        return new GitHubConfig(null, ghRepo.gToken, null, null, ghRepo.gOwner,
                ghRepo.gRepo, null, PAGE_SIZE);
    }

    @Override
    public RadicleConfig getRadicleConfig() {
        return new RadicleConfig(null, null, null, null, rProjectPath, false);
    }

}
