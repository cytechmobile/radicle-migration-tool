package network.radicle.tools.migrate.commands;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import network.radicle.tools.migrate.Config;
import picocli.CommandLine;

@Dependent
@CommandLine.Command(sortOptions = false, sortSynopsis = false)
public class Command implements Runnable, ICommand {
    public static final int PAGE_SIZE = 100;

    public enum State { open, closed, all }

    @Inject Config config;

    public void run() {
        var c = getConfiguration();
        config.github(c.github()).gitlab(c.gitlab()).radicle(c.radicle());
        exec();
    }

    @Override
    public void exec() {
        throw new UnsupportedOperationException("Child classes must implement the exec method.");
    }

    @Override
    public Config getConfiguration() {
        throw new UnsupportedOperationException("Child classes must implement the getConfiguration method");
    }
}
