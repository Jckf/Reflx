package it.flaten.reflxapi.event.player;

import it.flaten.reflxapi.entity.Player;
import it.flaten.reflxapi.event.Event;

public class PlayerLogoutEvent extends Event {
    private final Player player;

    public PlayerLogoutEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}
