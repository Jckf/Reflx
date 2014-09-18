package it.flaten.reflx;

import it.flaten.reflx.event.ReflxEventManager;
import it.flaten.reflx.plugin.ReflxPluginLoader;
import it.flaten.reflx.reflection.Mapper;
import it.flaten.reflxapi.command.CommandHandler;
import it.flaten.reflxapi.event.EventManager;
import it.flaten.reflxapi.plugin.Plugin;
import it.flaten.reflxapi.Server;
import it.flaten.reflx.command.ReflxCommandHandler;
import it.flaten.reflxapi.plugin.PluginLoader;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflxServer implements Server {
    private Mapper mapper;
    private ReflxPlayerList playerList;
    private ReflxCommandHandler commandHandler;
    private ReflxEventManager eventManager;
    private ReflxPluginLoader pluginLoader;

    private Object dedicatedServer;

    @Override
    public void run() {
        try {
            this.mapper = new Mapper("mapper.yaml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        JarLoader.load("minecraft_server.jar");

        this.playerList = new ReflxPlayerList(this);
        this.commandHandler = new ReflxCommandHandler(this);
        this.eventManager = new ReflxEventManager(this);

        try {
            this.playerList.prepare();
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
            return;
        }

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

        try {
            Field serverField = minecraftServer.getDeclaredField("j");
            serverField.setAccessible(true);

            this.dedicatedServer = serverField.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.playerList.inject();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException | NoSuchMethodException | CannotCompileException | InstantiationException | NotFoundException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            this.commandHandler.inject();
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        
        this.pluginLoader = new ReflxPluginLoader(this);
        this.pluginLoader.load("plugins");

        for (Plugin plugin : this.pluginLoader.getPlugins()) {
            plugin.onEnable();
        }
    }

    @Override
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    @Override
    public EventManager getEventManager() {
        return this.eventManager;
    }

    @Override
    public PluginLoader getPluginLoader() {
        return this.pluginLoader;
    }

    public Mapper getMapper() {
        return this.mapper;
    }

    public Object getServer() {
        return this.dedicatedServer;
    }
}
