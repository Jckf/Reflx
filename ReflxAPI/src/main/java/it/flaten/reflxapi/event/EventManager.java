package it.flaten.reflxapi.event;

import it.flaten.reflxapi.plugin.Plugin;

public interface EventManager {
    public void registerEvents(Plugin plugin, EventListener listener);

    public void triggerEvent(Event event);
}
