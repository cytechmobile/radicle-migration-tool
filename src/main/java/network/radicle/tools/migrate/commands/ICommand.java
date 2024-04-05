package network.radicle.tools.migrate.commands;

import network.radicle.tools.migrate.Config;

public interface ICommand {
    Config getConfiguration();
    void exec();
}
