package it.flaten.reflx.event;

import it.flaten.reflx.ReflxServer;
import it.flaten.reflxapi.event.Event;
import it.flaten.reflxapi.event.EventHandler;
import it.flaten.reflxapi.event.EventListener;
import it.flaten.reflxapi.event.EventManager;
import it.flaten.reflxapi.plugin.Plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflxEventManager implements EventManager {
    private final ReflxServer server;

    private final Map<Class<Event>, List<EventHandlerInfo>> handlers;

    public ReflxEventManager(ReflxServer server) {
        this.server = server;

        this.handlers = new HashMap<>();
    }

    public void registerEvents(Plugin plugin, EventListener listener) {
        for (Method method : listener.getClass().getMethods()) {
            Annotation annotation = method.getAnnotation(EventHandler.class);

            if (annotation == null)
                continue;

            Class[] params = method.getParameterTypes();

            if (params.length != 1)
                continue;

            Class eventType = params[0];

            if (!(eventType.getSuperclass().equals(Event.class)))
                continue;

            if (!this.handlers.containsKey(plugin))
                this.handlers.put(eventType, new ArrayList<EventHandlerInfo>());

            this.handlers.get(eventType).add(new EventHandlerInfo(
                plugin,
                listener,
                method
            ));
        }
    }

    public void triggerEvent(Event event) {
        if (!this.handlers.containsKey(event.getClass()))
            return;

        for (EventHandlerInfo info : this.handlers.get(event.getClass())) {
            try {
                info.getHandler().invoke(info.getListener(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
