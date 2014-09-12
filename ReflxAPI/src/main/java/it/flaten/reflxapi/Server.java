package it.flaten.reflxapi;

import it.flaten.reflxapi.command.CommandHandler;
import it.flaten.reflxapi.plugin.PluginLoader;

public interface Server extends Runnable {
    public PluginLoader getPluginLoader();

    public CommandHandler getCommandHandler();
}
