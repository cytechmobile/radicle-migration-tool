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
            names = {"-gv", "--github-api-version"},
            defaultValue = "github.api.version",
            description = "The version of the GitHub REST API (e.g. 2022-11-28)")
    String gVersion;

    @CommandLine.Option(
            names = {"-gu", "--github-api-url"},
            defaultValue = "github.api.url",
            description = "The base url of the GitHub REST API")
    String gUrl;

    @CommandLine.Option(
            names = {"-gr", "--github-repo"},
            required = true,
            defaultValue = "${GITHUB_REPO}",
            description = "The target GitHub repo")
    String gRepo;

    @CommandLine.Option(
            names = {"-go", "--github-repo-owner"},
            required = true,
            defaultValue = "${GITHUB_OWNER}",
            description = "The owner of the target GitHub repo")
    String gOwner;

    @CommandLine.Option(
            names = {"-gt", "--github-token"},
            required = true,
            interactive = true,
            defaultValue = "${GITHUB_TOKEN}",
            description = "The GitHub authentication token")
    String gToken;

    @ConfigProperty(name = "github.api.page-size")
    int gPageSize;

    @CommandLine.Option(
            names = {"-rv", "--radicle-api-version"},
            defaultValue = "radicle.api.version",
            description = "The version of the Radicle HTTP API (e.g. v1)")
    String rVersion;

    @CommandLine.Option(
            names = {"-ru", "--radicle-api-url"},
            defaultValue = "radicle.api.url",
            description = "The base url of Radicle HTTP API")
    String rUrl;

    @CommandLine.Option(
            names = {"-rp", "--radicle-project"},
            required = true,
            defaultValue = "${RADICLE_PROJECT}",
            description = "The target Radicle project")
    String rProject;

    @Inject MigrationService service;
    @Inject Config config;

    public void run() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        config.setGithub(new Config.GitHubConfig(gToken, gUrl, gVersion, gOwner, gRepo, gPageSize));
        config.setRadicle(new Config.RadicleConfig(rUrl, rVersion, rProject));
    }

}
