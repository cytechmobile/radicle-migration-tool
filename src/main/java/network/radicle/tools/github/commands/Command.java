package network.radicle.tools.github.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.services.MigrationService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import picocli.CommandLine;

@Dependent
@CommandLine.Command
public class Command implements Runnable {
    @CommandLine.Option(
            names = {"-v", "--version"},
            defaultValue = "github.api.version",
            description = "The version of the GitHub REST API (e.g. 2022-11-28)")
    String version;

    @CommandLine.Option(
            names = {"-u", "--url"},
            defaultValue = "github.api.url",
            description = "The base url of the GitHub REST API")
    String url;

    @CommandLine.Option(
            names = {"-r", "--repo"},
            required = true,
            defaultValue = "${GITHUB_REPO}",
            description = "The target GitHub repo")
    String repo;

    @CommandLine.Option(
            names = {"-o", "--owner"},
            required = true,
            defaultValue = "${GITHUB_OWNER}",
            description = "The owner of the target GitHub repo")
    String owner;

    @CommandLine.Option(
            names = {"-t", "--token"},
            required = true,
            interactive = true,
            defaultValue = "${GITHUB_TOKEN}",
            description = "The GitHub authentication token")
    String token;

    @ConfigProperty(name = "github.api.page-size")
    int pageSize;

    @Inject MigrationService service;
    @Inject Config config;

    public void run() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        config.setToken(token);
        config.setUrl(url);
        config.setRepo(repo);
        config.setVersion(version);
        config.setOwner(owner);
        config.setPageSize(pageSize);
    }

}
