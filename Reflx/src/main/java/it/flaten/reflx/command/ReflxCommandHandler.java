package it.flaten.reflx.command;

import it.flaten.reflx.ReflxServer;
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

public class ReflxCommandHandler implements CommandHandler {
    private final ReflxServer server;

    private Object originalHandler;

    private final Map<String, CommandExecutor> commands;

    public ReflxCommandHandler(ReflxServer server) {
        this.server = server;

        this.commands = new HashMap<>();
    }

    public void inject() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class minecraftServer = Class.forName("net.minecraft.server.MinecraftServer");

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

        Object serverInstance = this.server.getServer();

        // Fetch the vanilla command handler.
        this.originalHandler = commandHandler.get(serverInstance);

        // Method rerouting map.
        Map<String, String> mapping = new HashMap<>();
        mapping.put("bl.a(lt, String)", "executeCommand"); // Console commands.
        mapping.put("bl.a(mw, String)", "executeCommand"); // In-game commands.

        // Set the command handler field in the MinecraftServer instance to a proxy with our interceptor.
        commandHandler.set(
            serverInstance,
            Interceptor.getProxy(
                new Class[]{ Class.forName("ab") },
                this.originalHandler,
                this,
                mapping
            )
        );
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
