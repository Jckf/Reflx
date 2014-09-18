package it.flaten.reflxapi;

import it.flaten.reflxapi.command.CommandHandler;
import it.flaten.reflxapi.event.EventManager;
import it.flaten.reflxapi.plugin.PluginLoader;

public interface Server extends Runnable {
    public CommandHandler getCommandHandler();

    public EventManager getEventManager();

    public PluginLoader getPluginLoader();
}
