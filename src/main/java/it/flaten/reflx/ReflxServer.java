package it.flaten.reflx;

import it.flaten.reflx.api.Plugin;
import it.flaten.reflx.api.Server;
import it.flaten.reflx.command.CommandHandler;
import it.flaten.reflx.plugin.PluginLoader;

import java.lang.reflect.Method;

public class ReflxServer implements Server {
    private PluginLoader pluginLoader;
    private CommandHandler commandHandler;

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

        this.pluginLoader = new PluginLoader(this);
        this.commandHandler = CommandHandler.inject(minecraftServer);

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
