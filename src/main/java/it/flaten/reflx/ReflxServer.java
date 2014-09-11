package it.flaten.reflx;

import it.flaten.reflx.handlers.CommandHandler;
import it.flaten.reflx.loaders.JarLoader;
import it.flaten.reflx.reflection.Interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReflxServer {
    public void run() {
        System.out.println("Loading server jar...");

        JarLoader.load("minecraft_server.jar");

        System.out.println("Invoking main...");

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

        System.out.println("Injecting command interceptor...");

        try {
            // Find the field that holds the command executor.
            Field commandHandler = null;
            for (Field field : minecraftServer.getDeclaredFields()) {
                if (field.getName().equals("o")) {
                    commandHandler = field;
                    break;
                }
            }

            // Make it accessible.
            commandHandler.setAccessible(true);

            // Make it non-final.
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(commandHandler, commandHandler.getModifiers() & ~Modifier.FINAL);

            // Find an object that holds a reference to the MinecraftServer instance.
            Thread serverThread = null;
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (Thread thread : threads) {
                if (thread.getName().equals("Server thread")) {
                    serverThread = thread;
                    break;
                }
            }

            // Fetch the field and make it accessible.
            Field serverField = serverThread.getClass().getDeclaredField("a");
            serverField.setAccessible(true);

            // Fetch the MinecraftServer instance.
            Object serverInstance = serverField.get(serverThread);

            // Method rerouting map.
            Map<String, String> mapping = new HashMap<>();
            mapping.put("bl.a(lt,String)", "executeCommand");

            // Set the command handler field in the MinecraftServer instance to a proxy with our interceptor.
            commandHandler.set(
                serverInstance,
                Interceptor.getProxy(
                    new Class[]{ Class.forName("ab") }, // Interfaces our proxy implementes.
                    commandHandler.get(serverInstance), // Original command handler.
                    new CommandHandler(),               // Custom command handler.
                    mapping                             // Method rerouting map.
                )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
