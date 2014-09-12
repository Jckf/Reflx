package it.flaten.reflx;

import it.flaten.reflx.plugin.ReflxPluginLoader;
import it.flaten.reflxapi.command.CommandHandler;
import it.flaten.reflxapi.plugin.Plugin;
import it.flaten.reflxapi.Server;
import it.flaten.reflx.command.ReflxCommandHandler;
import it.flaten.reflxapi.plugin.PluginLoader;

import java.lang.reflect.Method;

public class ReflxServer implements Server {
    private ReflxPluginLoader pluginLoader;
    private ReflxCommandHandler commandHandler;

    @Override
    public void run() {
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

        this.pluginLoader = new ReflxPluginLoader(this);
        this.commandHandler = ReflxCommandHandler.inject(minecraftServer);

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
