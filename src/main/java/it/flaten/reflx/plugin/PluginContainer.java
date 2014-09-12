package it.flaten.reflx.plugin;

import it.flaten.reflx.api.Plugin;
import it.flaten.reflx.api.PluginDescriptor;
import it.flaten.reflx.api.Server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginContainer extends URLClassLoader {
    private final Server server;

    private Plugin plugin;

    public PluginContainer(Server server, String path) throws MalformedURLException {
        this(server, new File(path));
    }

    public PluginContainer(Server server, File file) throws MalformedURLException {
        this(server, file.toURI().toURL());
    }

    public PluginContainer(Server server, URL url) {
        super(new URL[]{ url });

        this.server = server;
    }

    public void initialize() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        PluginDescriptor descriptor = new ReflxPluginDescriptor(this);

        this.plugin = (Plugin) Class.forName(descriptor.getMain()).newInstance();
        this.plugin.initialize(this.server, this, descriptor);
    }

    public Plugin getPlugin() {
        return plugin;
    }
}
