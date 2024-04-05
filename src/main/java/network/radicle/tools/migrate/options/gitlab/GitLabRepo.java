package network.radicle.tools.migrate.options.gitlab;

import picocli.CommandLine;

public class GitLabRepo {
    @CommandLine.Option(
            names = {"-gp", "--gitlab-project"},
            order = 30,
            required = true,
            defaultValue = "${GL_PROJECT}",
            description = "The source GitLab project.")
    public String gProject;

    @CommandLine.Option(
            names = {"-gn", "--gitlab-namespace"},
            order = 40,
            required = true,
            defaultValue = "${GL_NAMESPACE}",
            description = "The namespace of the source GitLab project.")
    public String gNamespace;

    @CommandLine.Option(
            names = {"-gt", "--gitlab-token"},
            order = 50,
            required = true,
            interactive = true,
            defaultValue = "${GL_TOKEN}",
            description = "Your GitLab personal access token.")
    public String gToken;
}
