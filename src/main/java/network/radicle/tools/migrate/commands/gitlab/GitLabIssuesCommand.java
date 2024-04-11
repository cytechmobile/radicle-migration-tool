package network.radicle.tools.migrate.commands.gitlab;

import io.quarkus.runtime.Quarkus;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.Config.GitLabConfig;
import network.radicle.tools.migrate.Config.RadicleConfig;
import network.radicle.tools.migrate.commands.Command;
import network.radicle.tools.migrate.options.Filters;
import network.radicle.tools.migrate.options.Generic;
import network.radicle.tools.migrate.options.gitlab.GitLabApi;
import network.radicle.tools.migrate.options.gitlab.GitLabRepo;
import network.radicle.tools.migrate.options.radicle.RadicleApi;
import network.radicle.tools.migrate.options.radicle.RadicleRepo;
import network.radicle.tools.migrate.services.gitlab.GitLabMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(
        name = "issues",
        description = "Migrate issues from a GitLab repository to a Radicle project.",
        headerHeading = "@|bold,underline Usage|@:%n",
        synopsisHeading = "",
        descriptionHeading = "%n@|bold,underline Description|@:%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n",
        resourceBundle = "network.radicle.tools.migrate.commands.gitlab.Messages")
public class GitLabIssuesCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(GitLabIssuesCommand.class);

    @CommandLine.Mixin
    GitLabApi glApi;
    @CommandLine.Mixin
    GitLabRepo glRepo;
    @CommandLine.Mixin
    RadicleApi radApi;
    @CommandLine.Mixin
    RadicleRepo radRepo;
    @CommandLine.Mixin Generic generic;
    @CommandLine.Mixin Filters filters;

    @Inject GitLabMigrationService service;

    @Override
    public void exec() {
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
        var glConfig = new GitLabConfig(glApi.gDomain, glApi.gSession, glRepo.gToken, glApi.gUrl, glApi.gVersion,
                glRepo.gNamespace, glRepo.gProject, filters.getFilters(), PAGE_SIZE);
        var radConfig = new RadicleConfig(radApi.rUrl, radApi.rVersion, radRepo.rProject, radRepo.rPassphrase, null,
                generic.dryRun);

        return new Config(radConfig, null, glConfig);
    }
}
