package network.radicle.tools.migrate.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import network.radicle.tools.migrate.Config.GitHubConfig;
import network.radicle.tools.migrate.Config.RadicleConfig;
import network.radicle.tools.migrate.options.GithubRepo;
import network.radicle.tools.migrate.services.MigrationService;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(sortOptions = false, sortSynopsis = false)
public class Command implements Runnable {
    public static final int PAGE_SIZE = 100;

    @CommandLine.Mixin GithubRepo ghRepo;

    public enum State { open, closed, all }

    @Inject MigrationService service;
    @Inject Config config;

    public void run() {
        config.setRadicle(getRadicleConfig());
        config.setGithub(getGithubConfig());

        exec();
    }

    public void exec() {
        throw new UnsupportedOperationException("Child classes must implement the exec method.");
    }

    public GitHubConfig getGithubConfig() {
        throw new UnsupportedOperationException("Child classes must implement the getGithubConfig method.");
    }

    public RadicleConfig getRadicleConfig() {
        throw new UnsupportedOperationException("Child classes must implement the getRadicleConfig method.");
    }
}
