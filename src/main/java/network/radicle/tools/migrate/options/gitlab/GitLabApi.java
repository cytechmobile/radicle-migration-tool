package network.radicle.tools.migrate.options.gitlab;

import picocli.CommandLine;

import java.net.URL;

public class GitLabApi {
    @CommandLine.Option(
            names = {"-gv", "--gitlab-api-version"},
            order = 10,
            defaultValue = "${GL_API_VERSION:-v4}",
            description = "The version of the GitLab REST API (default: v4).")
    public String gVersion;

    @CommandLine.Option(
            names = {"-gu", "--gitlab-api-url"},
            order = 20,
            defaultValue = "${GL_API_URL:-https://gitlab.com/api}",
            description = "The base url of the GitLab REST API (default: https://gitlab.com/api).")
    public URL gUrl;

    @CommandLine.Option(
            names = {"-gd", "--gitlab-domain"},
            order = 59,
            defaultValue = "${GL_DOMAIN:-gitlab.com}",
            description = "The GitLab domain. It is utilized for migrating assets and files (default: gitlab.com).")
    public String gDomain;

    @CommandLine.Option(
            names = {"-gs", "--gitlab-session"},
            order = 60,
            defaultValue = "${GL_SESSION}",
            description = "The value of the _gitlab_session cookie. It is utilized for migrating assets and files " +
                    "from a GitLab project with the `Require authentication to view media files` enabled.")
    public String gSession;
}
