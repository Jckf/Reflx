package it.flaten.reflx.api;

import it.flaten.reflx.command.CommandHandler;
import it.flaten.reflx.plugin.PluginLoader;

public interface Server extends Runnable {
    public PluginLoader getPluginLoader();

    public CommandHandler getCommandHandler();
}
