package network.radicle.tools.migrate.commands.github;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.Config.GitHubConfig;
import network.radicle.tools.migrate.Config.RadicleConfig;
import network.radicle.tools.migrate.commands.Command;
import network.radicle.tools.migrate.options.github.GitHubRepo;
import network.radicle.tools.migrate.services.github.GitHubMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "wiki",
        description = "Migrate a GitHub Wiki to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "",
        descriptionHeading = "%n@|bold,underline Description|@:%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n")
public class GitHubWikiCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(GitHubWikiCommand.class);

    @CommandLine.Mixin
    GitHubRepo ghRepo;
    @CommandLine.Option(
            names = {"-rpp", "--radicle-project-path"},
            order = 170,
            required = true,
            defaultValue = "${RAD_PROJECT_PATH}",
            description = "The absolute path to the target Radicle project in your local file system.")
    String rProjectPath;

    @Inject GitHubMigrationService service;

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
    public Config getConfiguration() {
        var ghConfig = new GitHubConfig(null, null, ghRepo.gToken, null, null, ghRepo.gOwner,
                ghRepo.gRepo, null, PAGE_SIZE);
        var radConfig = new RadicleConfig(null, null, null, null, rProjectPath, false);

        return new Config(radConfig, ghConfig, null);
    }
}
