package it.flaten.reflx.plugin;

import it.flaten.reflxapi.plugin.Plugin;
import it.flaten.reflxapi.Server;
import it.flaten.reflxapi.plugin.PluginLoader;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class ReflxPluginLoader implements PluginLoader {
    private final Server server;
    private final Map<String, ReflxPluginContainer> plugins;

    public ReflxPluginLoader(Server server) {
        this.server = server;

        this.plugins = new HashMap<>();
    }

    @Override
    public void load(String path) {
        this.load(new File(path));
    }

    @Override
    public void load(File dir) {
        if (!dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        })) {
            System.out.println("Loading " + file.getName());

            try {
                ReflxPluginContainer container = new ReflxPluginContainer(this.server, file);
                container.initialize();

                this.plugins.put(container.getPlugin().getName(), container);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // Incorrect main class given, or main class not in jar.
                System.out.println("Main class for " + file.getName() + " not found!");
            } catch (InstantiationException e) {
                // ???
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // Main class is not public.
                e.printStackTrace();
            }
        }
    }

    @Override
    public Plugin[] getPlugins() {
        Plugin[] array = new Plugin[this.plugins.size()];

        int i = 0;
        for (ReflxPluginContainer container : this.plugins.values()) {
            array[i++] = container.getPlugin();
        }

        return array;
    }

    @Override
    public Plugin getPlugin(String name) {
        if (this.plugins.containsKey(name))
            return this.plugins.get(name).getPlugin();

        return null;
    }
}
