package it.flaten.reflx;

import it.flaten.reflx.entity.ReflxPlayer;
import it.flaten.reflx.reflection.Hook;
import it.flaten.reflx.reflection.Interceptor;
import it.flaten.reflx.reflection.MethodCache;
import it.flaten.reflx.reflection.ReflectionUtils;
import it.flaten.reflxapi.event.ServerTickEvent;
import it.flaten.reflxapi.event.player.PlayerLoginEvent;
import it.flaten.reflxapi.event.player.PlayerLogoutEvent;
import javassist.*;

import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ReflxPlayerList {
    private final ReflxServer server;

    private Object originalConfigManager;

    public ReflxPlayerList(ReflxServer server) {
        this.server = server;
    }

    public void prepare() throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();

        CtClass configManager = pool.get("oi");
        configManager.addConstructor(CtNewConstructor.make("public " + configManager.getSimpleName() + "() { }", configManager));
        configManager.toClass();

        CtClass playerList =  pool.get("ls");
        playerList.addConstructor(CtNewConstructor.make("public " + playerList.getSimpleName() + "() { }", playerList));
        playerList.toClass();
    }

    public void inject() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, CannotCompileException, InstantiationException, NotFoundException, InvocationTargetException {
        Field configManagerField = Class.forName("net.minecraft.server.MinecraftServer").getDeclaredField("u");
        configManagerField.setAccessible(true);

        Object dedicatedServer = this.server.getServer();

        while (this.originalConfigManager == null) {
            this.originalConfigManager = configManagerField.get(dedicatedServer);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<String, String> mapping = new HashMap<>();
        mapping.put("ls.e()", "serverTick");
        mapping.put("ls.a(ej, mw)", "initConnection");
        mapping.put("ls.e(mw)", "playerLogout");

        configManagerField.set(
            dedicatedServer,
            Interceptor.getProxy(
                this.originalConfigManager,
                this,
                mapping
            )
        );

        Object proxy = configManagerField.get(dedicatedServer);

        // This is an extremely ugly hack, and it might not even work properly.
        for (Field f : this.originalConfigManager.getClass().getFields()) {
            try {
                f.setAccessible(true);

                Field modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
                modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                f.set(proxy, f.get(this.originalConfigManager));
            } catch (Exception ignored) { }
        }
    }

    public void serverTick(Hook hook) {
        this.server.getEventManager().triggerEvent(new ServerTickEvent());
    }

    public void initConnection(Hook hook, Object networkManager, Object entityPlayer) {
        this.server.getEventManager().triggerEvent(new PlayerLoginEvent(new ReflxPlayer(entityPlayer)));
    }

    public void playerLogout(Hook hook, Object entityPlayer) {
        this.server.getEventManager().triggerEvent(new PlayerLogoutEvent(new ReflxPlayer(entityPlayer)));
    }
}
