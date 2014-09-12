package it.flaten.reflx.plugin;

import it.flaten.reflx.api.Plugin;
import it.flaten.reflx.api.Server;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class PluginLoader {
    private final Server server;
    private final Map<String, PluginContainer> plugins;

    public PluginLoader(Server server) {
        this.server = server;

        this.plugins = new HashMap<>();
    }

    public void load(String path) {
        this.load(new File(path));
    }

    public void load(File dir) {
        for (File file : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        })) {
            try {
                PluginContainer container = new PluginContainer(this.server, file);
                container.initialize();

                this.plugins.put(container.getPlugin().getName(), container);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // Incorrect main class given, or main class not in jar.
                e.printStackTrace();
            } catch (InstantiationException e) {
                // ???
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // Main class is not public.
                e.printStackTrace();
            }
        }
    }

    public Plugin[] getPlugins() {
        Plugin[] array = new Plugin[this.plugins.size()];

        int i = 0;
        for (PluginContainer container : this.plugins.values()) {
            array[i++] = container.getPlugin();
        }

        return array;
    }

    public Plugin getPlugin(String name) {
        if (this.plugins.containsKey(name))
            return this.plugins.get(name).getPlugin();

        return null;
    }
}
