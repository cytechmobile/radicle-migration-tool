package network.radicle.tools.migrate.options.github;

import picocli.CommandLine;

import java.net.URL;

public class GitHubApi {
    @CommandLine.Option(
            names = {"-gv", "--github-api-version"},
            order = 10,
            defaultValue = "${GH_API_VERSION:-2022-11-28}",
            description = "The version of the GitHub REST API (default: 2022-11-28).")
    public String gVersion;

    @CommandLine.Option(
            names = {"-gu", "--github-api-url"},
            order = 20,
            defaultValue = "${GH_API_URL:-https://api.github.com}",
            description = "The base url of the GitHub REST API (default: https://api.github.com).")
    public URL gUrl;

    @CommandLine.Option(
            names = {"-gd", "--github-domain"},
            order = 59,
            defaultValue = "${GH_DOMAIN:-github.com}",
            description = "The GitHub domain. It is utilized for migrating assets and files (default: github.com).")
    public String gDomain;

    @CommandLine.Option(
            names = {"-gs", "--github-session"},
            order = 60,
            defaultValue = "${GH_SESSION}",
            description = "The value of the user_session cookie. It is utilized for migrating assets and files from a private GitHub repository.")
    public String gSession;
}
