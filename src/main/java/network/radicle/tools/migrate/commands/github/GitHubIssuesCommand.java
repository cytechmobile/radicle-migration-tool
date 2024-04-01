package network.radicle.tools.migrate.commands.github;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.Config.GitHubConfig;
import network.radicle.tools.migrate.Config.RadicleConfig;
import network.radicle.tools.migrate.commands.Command;
import network.radicle.tools.migrate.options.Filters;
import network.radicle.tools.migrate.options.Generic;
import network.radicle.tools.migrate.options.github.GitHubApi;
import network.radicle.tools.migrate.options.github.GitHubRepo;
import network.radicle.tools.migrate.options.radicle.RadicleApi;
import network.radicle.tools.migrate.options.radicle.RadicleRepo;
import network.radicle.tools.migrate.services.AppStateService;
import network.radicle.tools.migrate.services.github.GitHubMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "issues",
        description = "Migrate issues from a GitHub repository to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "",
        descriptionHeading = "%n@|bold,underline Description|@:%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n")
public class GitHubIssuesCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(GitHubIssuesCommand.class);

    @CommandLine.Mixin
    GitHubApi ghApi;
    @CommandLine.Mixin
    GitHubRepo ghRepo;
    @CommandLine.Mixin
    RadicleApi radApi;
    @CommandLine.Mixin
    RadicleRepo radRepo;
    @CommandLine.Mixin Generic generic;
    @CommandLine.Mixin Filters filters;

    @Inject AppStateService appStateService;
    @Inject GitHubMigrationService service;

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
    public Config getConfiguration() {
        var ghConfig = new GitHubConfig(ghApi.gDomain, ghApi.gSession, ghRepo.gToken, ghApi.gUrl, ghApi.gVersion,
                ghRepo.gOwner, ghRepo.gRepo, filters.getFilters(), PAGE_SIZE);
        var radConfig = new RadicleConfig(radApi.rUrl, radApi.rVersion, radRepo.rProject, radRepo.rPassphrase, null,
                generic.dryRun);

        return new Config(radConfig, ghConfig, null);
    }

}
