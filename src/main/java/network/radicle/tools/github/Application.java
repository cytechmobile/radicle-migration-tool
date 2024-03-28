package network.radicle.tools.github;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import network.radicle.tools.github.commands.IssuesCommand;
import network.radicle.tools.github.commands.QuarkusCommand;
import network.radicle.tools.github.commands.WikiCommand;
import picocli.CommandLine;

@QuarkusMain
public class Application implements QuarkusApplication {
    @Inject IssuesCommand issuesCommand;
    @Inject WikiCommand wikiCommand;

    @Override
    public int run(String... args) {
        return new CommandLine(new QuarkusCommand())
                .addSubcommand(issuesCommand)
                .addSubcommand(wikiCommand)
                .setUsageHelpAutoWidth(true)
                .execute(args);
    }
}
