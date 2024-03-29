package network.radicle.tools.migrate.commands;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config.GitHubConfig;
import network.radicle.tools.migrate.Config.RadicleConfig;
import network.radicle.tools.migrate.options.GithubApi;
import network.radicle.tools.migrate.options.RadicleApi;
import network.radicle.tools.migrate.options.RadicleRepo;
import network.radicle.tools.migrate.services.AppStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "issues",
        description = "Migrate issues from a GitHub repository to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n")
public class IssuesCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(IssuesCommand.class);

    @CommandLine.Mixin GithubApi ghApi;
    @CommandLine.Mixin RadicleApi radApi;
    @CommandLine.Mixin RadicleRepo radRepo;

    @Inject
    AppStateService appStateService;

    @Override
    public void exec() {
        if (!appStateService.isInitialized()) {
            Quarkus.asyncExit(1);
            return;
        }

        var result = service.migrateIssues();
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
        return new GitHubConfig(ghApi.gSession, ghRepo.gToken, ghApi.gUrl, ghApi.gVersion, ghRepo.gOwner,
                ghRepo.gRepo, ghApi.getFilters(), PAGE_SIZE);
    }

    @Override
    public RadicleConfig getRadicleConfig() {
        return new RadicleConfig(radApi.rUrl, radApi.rVersion, radRepo.rProject, radRepo.rPassphrase, null, dryRun);
    }

}
