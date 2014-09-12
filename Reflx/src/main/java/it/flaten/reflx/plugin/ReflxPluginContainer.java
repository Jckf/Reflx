package it.flaten.reflx.plugin;

import it.flaten.reflxapi.plugin.Plugin;
import it.flaten.reflxapi.plugin.PluginDescriptor;
import it.flaten.reflxapi.Server;
import it.flaten.reflxapi.plugin.PluginContainer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ReflxPluginContainer extends URLClassLoader implements PluginContainer {
    private final Server server;

    private Plugin plugin;

    public ReflxPluginContainer(Server server, String path) throws MalformedURLException {
        this(server, new File(path));
    }

    public ReflxPluginContainer(Server server, File file) throws MalformedURLException {
        this(server, file.toURI().toURL());
    }

    public ReflxPluginContainer(Server server, URL url) {
        super(new URL[]{ url });

        this.server = server;
    }

    public void initialize() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        PluginDescriptor descriptor = new ReflxPluginDescriptor(this);

        this.plugin = (Plugin) Class.forName(descriptor.getMain(), false, this).newInstance();
        this.plugin.initialize(this.server, this, descriptor);
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
