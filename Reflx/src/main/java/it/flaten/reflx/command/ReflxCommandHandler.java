package it.flaten.reflx.command;

import it.flaten.reflx.reflection.MethodCache;
import it.flaten.reflxapi.command.CommandExecutor;
import it.flaten.reflxapi.command.CommandHandler;
import it.flaten.reflxapi.command.CommandSender;
import it.flaten.reflx.entity.ReflxPlayer;
import it.flaten.reflx.reflection.Interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReflxCommandHandler implements CommandHandler {
    public static ReflxCommandHandler inject(Class minecraftServer) {
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

            // Fetch the vanilla command handler.
            Object originalHandler = commandHandler.get(serverInstance);

            // Our custom handler.
            ReflxCommandHandler customHandler = new ReflxCommandHandler(originalHandler);

            // Method rerouting map.
            Map<String, String> mapping = new HashMap<>();
            mapping.put("bl.a(lt, String)", "executeCommand"); // Console commands.
            mapping.put("bl.a(mw, String)", "executeCommand"); // In-game commands.

            // Set the command handler field in the MinecraftServer instance to a proxy with our interceptor.
            commandHandler.set(
                serverInstance,
                Interceptor.getProxy(
                    new Class[]{ Class.forName("ab") },
                    originalHandler,
                    customHandler,
                    mapping
                )
            );

            return customHandler;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private final Object originalHandler;

    private final Map<String, CommandExecutor> commands;

    public ReflxCommandHandler(Object originalHandler) {
        this.originalHandler = originalHandler;

        this.commands = new HashMap<>();
    }

    @Override
    public void registerCommand(String command, CommandExecutor executor) {
        this.commands.put(command, executor);
    }

    @Override
    public int executeCommand(Object sender, String commandRaw) {
        int firstSpace = commandRaw.indexOf(" ");
        String command = firstSpace >= 0 ? commandRaw.substring(0, firstSpace) : commandRaw;
        String[] args = firstSpace >= 0 ? commandRaw.substring(firstSpace + 1).split(" ") : new String[]{ };

        if (command.indexOf("/") == 0)
            command = command.substring(1);

        if (this.commands.containsKey(command)) {
            CommandSender commandSender = new ReflxPlayer(sender); // Todo: New Player _or ConsoleSender_. Player is currently compatible though.

            this.commands.get(command).executeCommand(commandSender, command, args);

            return 0;
        }

        try {
            return (int) MethodCache.getMethod(this.originalHandler, "a", sender.getClass(), String.class).invoke(this.originalHandler, sender, commandRaw);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
