package network.radicle.tools.github.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.services.MigrationService;
import picocli.CommandLine;

@Dependent
@CommandLine.Command
public class Command implements Runnable {
    public static final int PAGE_SIZE = 100;

    @CommandLine.Option(
            names = {"-gv", "--github-api-version"},
            defaultValue = "${GITHUB_API_VERSION:-2022-11-28}",
            description = "The version of the GitHub REST API (default 2022-11-28)")
    String gVersion;

    @CommandLine.Option(
            names = {"-gu", "--github-api-url"},
            defaultValue = "${GITHUB_API_URL:-https://api.github.com}",
            description = "The base url of the GitHub REST API (default https://api.github.com)")
    String gUrl;

    @CommandLine.Option(
            names = {"-gr", "--github-repo"},
            required = true,
            defaultValue = "${GITHUB_REPO}",
            description = "The source GitHub repo")
    String gRepo;

    @CommandLine.Option(
            names = {"-go", "--github-repo-owner"},
            required = true,
            defaultValue = "${GITHUB_OWNER}",
            description = "The owner of the source GitHub repo")
    String gOwner;

    @CommandLine.Option(
            names = {"-gt", "--github-token"},
            required = true,
            interactive = true,
            defaultValue = "${GITHUB_TOKEN}",
            description = "Your GitHub personal access token")
    String gToken;

    @CommandLine.Option(
            names = {"-rv", "--radicle-api-version"},
            defaultValue = "${RADICLE_API_VERSION:-v1}",
            description = "The version of the Radicle HTTP API (default v1)")
    String rVersion;

    @CommandLine.Option(
            names = {"-ru", "--radicle-api-url"},
            defaultValue = "${RADICLE_API_URL:-http://localhost:8080/api}",
            description = "The base url of Radicle HTTP API (default http://localhost:8080/api)")
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
        config.setGithub(new Config.GitHubConfig(gToken, gUrl, gVersion, gOwner, gRepo, PAGE_SIZE));
        config.setRadicle(new Config.RadicleConfig(rUrl, rVersion, rProject));
    }

}
