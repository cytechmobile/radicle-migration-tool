package network.radicle.tools.migrate;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.commands.QuarkusCommand;
import network.radicle.tools.migrate.commands.github.GitHubCommand;
import network.radicle.tools.migrate.commands.github.GitHubIssuesCommand;
import network.radicle.tools.migrate.commands.github.GitHubWikiCommand;
import network.radicle.tools.migrate.commands.gitlab.GitLabCommand;
import network.radicle.tools.migrate.commands.gitlab.GitLabIssuesCommand;
import picocli.CommandLine;

@QuarkusMain
public class Application implements QuarkusApplication {
    @Inject GitHubCommand ghCommand;
    @Inject GitLabCommand glCommand;
    @Inject GitHubIssuesCommand ghIssuesCommand;
    @Inject GitHubWikiCommand ghWikiCommand;
    @Inject GitLabIssuesCommand glIssuesCommand;

    @Override
    public int run(String... args) {
        var commandLine = new CommandLine(new QuarkusCommand());
        commandLine.addSubcommand(new CommandLine(ghCommand)
                .addSubcommand(ghIssuesCommand)
                .addSubcommand(ghWikiCommand));
        commandLine.addSubcommand(new CommandLine(glCommand)
                .addSubcommand(glIssuesCommand));

        return commandLine.setUsageHelpAutoWidth(true).execute(args);
    }
}
