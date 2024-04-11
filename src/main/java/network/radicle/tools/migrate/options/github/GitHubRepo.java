package network.radicle.tools.migrate.options.github;

import picocli.CommandLine;

public class GitHubRepo {
    @CommandLine.Option(
            names = {"-gr", "--github-repo"},
            order = 30,
            required = true,
            defaultValue = "${GH_REPO}",
            description = "The source GitHub repo.")
    public String gRepo;

    @CommandLine.Option(
            names = {"-go", "--github-repo-owner"},
            order = 40,
            required = true,
            defaultValue = "${GH_OWNER}",
            description = "The owner of the source GitHub repo.")
    public String gOwner;

    @CommandLine.Option(
            names = {"-gt", "--github-token"},
            order = 50,
            required = true,
            interactive = true,
            defaultValue = "${GH_TOKEN}",
            description = "Your GitHub personal access token (with `repo` scope or `read-only access` granted).")
    public String gToken;

    @CommandLine.Option(
            names = {"-gd", "--github-domain"},
            order = 59,
            defaultValue = "${GH_DOMAIN:-github.com}",
            description = "The GitHub domain. It is utilized for migrating assets and files (default: github.com).")
    public String gDomain;
}
