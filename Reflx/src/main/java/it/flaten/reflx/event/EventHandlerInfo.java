package it.flaten.reflx.event;

import it.flaten.reflxapi.event.EventListener;
import it.flaten.reflxapi.plugin.Plugin;

import java.lang.reflect.Method;

public class EventHandlerInfo {
    private final Plugin plugin;
    private final EventListener listener;
    private final Method handler;

    public EventHandlerInfo(Plugin plugin, EventListener listener, Method handler) {
        this.plugin = plugin;
        this.listener = listener;
        this.handler = handler;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public EventListener getListener() {
        return this.listener;
    }

    public Method getHandler() {
        return this.handler;
    }
}
