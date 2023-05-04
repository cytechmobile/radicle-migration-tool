package network.radicle.tools.github.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.github.GitHubClient;
import network.radicle.tools.github.GitHubConfig;
import network.radicle.tools.github.providers.ApplicationPropertiesDefaultProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(name = "issues", description = "Migrate GitHub issues",
                        defaultValueProvider = ApplicationPropertiesDefaultProvider.class)
public class IssuesCommand implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(IssuesCommand.class);

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

    @Inject GitHubClient github;

    @Override
    public void run() {
        var config = new GitHubConfig(token, url, version, owner, repo);

        logger.info("Running with configuration: {}", config);
        var result = github.migrateIssues(config);
        if (result) {
            logger.info("Migration finished successfully!");
        }
    }
}
