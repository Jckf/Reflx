package it.flaten.reflxapi.plugin;

import java.io.File;

public interface PluginLoader {
    public void load(String path);

    public void load(File file);

    public Plugin[] getPlugins();

    public Plugin getPlugin(String plugin);
}
