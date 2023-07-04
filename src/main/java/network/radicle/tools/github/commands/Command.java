package network.radicle.tools.github.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.github.Config;
import network.radicle.tools.github.services.MigrationService;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(sortOptions = false, sortSynopsis = false)
public class Command implements Runnable {
    public static final int PAGE_SIZE = 100;

    @CommandLine.Option(
            names = {"-gv", "--github-api-version"},
            defaultValue = "${GITHUB_API_VERSION:-2022-11-28}",
            description = "The version of the GitHub REST API (default: 2022-11-28)")
    String gVersion;

    @CommandLine.Option(
            names = {"-gu", "--github-api-url"},
            defaultValue = "${GITHUB_API_URL:-https://api.github.com}",
            description = "The base url of the GitHub REST API (default: https://api.github.com)")
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
            description = "The version of the Radicle HTTP API (default: v1)")
    String rVersion;

    @CommandLine.Option(
            names = {"-ru", "--radicle-api-url"},
            defaultValue = "${RADICLE_API_URL:-http://localhost:8080/api}",
            description = "The base url of Radicle HTTP API (default: http://localhost:8080/api)")
    String rUrl;

    @CommandLine.Option(
            names = {"-rp", "--radicle-project"},
            required = true,
            defaultValue = "${RADICLE_PROJECT}",
            description = "The target Radicle project")
    String rProject;

    @CommandLine.Option(
            names = {"-fs", "--filter-since"},
            defaultValue = "${FILTER_SINCE}",
            description = "Migrate issues created after the given time (default: timestamp of the last run). " +
                    "This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ")
    String fSince;

    @CommandLine.Option(
            names = {"-fl", "--filter-labels"},
            defaultValue = "${FILTER_LABELS}",
            description = "Migrate issues with the given labels given in a csv format (example: bug,ui,@high)")
    String fLabels;

    @CommandLine.Option(
            names = {"-ft", "--filter-state"},
            defaultValue = "${FILTER_STATE}",
            description = "Migrate issues in this state (default: all, can be one of: open, closed, all)")
    String fState;

    @CommandLine.Option(
            names = {"-fm", "--filter-milestone"},
            defaultValue = "${FILTER_MILESTONE}",
            description = "Migrate issues belonging to this milestone number. If the string * is passed, " +
                    "issues with any milestone will be migrated.")
    String fMilestone;

    @CommandLine.Option(
            names = {"-fa", "--filter-assignee"},
            defaultValue = "${FILTER_ASSIGNEE}",
            description = "Migrate issues assigned to the given user name. " +
                    "Pass * for migrating issues assigned to any user")
    String fAssignee;

    @CommandLine.Option(
            names = {"-fc", "--filter-creator"},
            defaultValue = "${FILTER_CREATOR}",
            description = "Migrate issues created by the given user name.")
    String fCreator;

    @Inject MigrationService service;
    @Inject Config config;

    public void run() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        config.setGithub(new Config.GitHubConfig(gToken, gUrl, gVersion, gOwner, gRepo, fSince, fLabels, fState,
                fMilestone, fAssignee, fCreator, PAGE_SIZE));
        config.setRadicle(new Config.RadicleConfig(rUrl, rVersion, rProject));
    }

}
