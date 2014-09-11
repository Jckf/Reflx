package it.flaten.reflx;

import it.flaten.reflx.command.TestCommand;
import it.flaten.reflx.command.CommandHandler;
import it.flaten.reflx.loaders.JarLoader;

import java.lang.reflect.Method;

public class ReflxServer {
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

        CommandHandler commandHandler = CommandHandler.inject(minecraftServer);

        if (commandHandler == null)
            System.exit(1);

        // Todo: Load plugins.
        commandHandler.registerCommand("test", new TestCommand());
    }
}
