package it.flaten.reflx;

import it.flaten.reflx.plugin.ReflxPluginLoader;
import it.flaten.reflx.reflection.Mapper;
import it.flaten.reflxapi.command.CommandHandler;
import it.flaten.reflxapi.plugin.Plugin;
import it.flaten.reflxapi.Server;
import it.flaten.reflx.command.ReflxCommandHandler;
import it.flaten.reflxapi.plugin.PluginLoader;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;

public class ReflxServer implements Server {
    private Mapper mapper;
    private ReflxPluginLoader pluginLoader;
    private ReflxCommandHandler commandHandler;

    @Override
    public void run() {
        try {
            this.mapper = new Mapper("mapper.yaml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JarLoader.load("minecraft_server.jar");

        Class minecraftServer;
        try {
            minecraftServer = Class.forName("net.minecraft.server.MinecraftServer");
            Method main = minecraftServer.getDeclaredMethod("main", String[].class);
            String[] arguments = new String[]{ "nogui" };
            main.invoke(null, (Object) arguments);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        this.commandHandler = new ReflxCommandHandler(this);
        this.commandHandler.inject();

        this.pluginLoader = new ReflxPluginLoader(this);
        this.pluginLoader.load("plugins");

        for (Plugin plugin : this.pluginLoader.getPlugins()) {
            plugin.onEnable();
        }
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    @Override
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }
}
