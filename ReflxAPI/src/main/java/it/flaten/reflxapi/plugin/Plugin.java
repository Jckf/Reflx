package it.flaten.reflxapi.plugin;

import it.flaten.reflxapi.Server;

public abstract class Plugin {
    private Server server;
    private PluginContainer container;
    private PluginDescriptor descriptor;

    public void initialize(Server server, PluginContainer container, PluginDescriptor descriptor) {
        this.server = server;
        this.container = container;
        this.descriptor = descriptor;
    }

    public final String getName() {
        return this.descriptor.getName();
    }

    public final String getVersion() {
        return this.descriptor.getVersion();
    }

    protected Server getServer() {
        return this.server;
    }

    public void onEnable() {

    }

    public void onDisable() {

    }
}
