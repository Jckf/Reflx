package it.flaten.reflx.entity;

import it.flaten.reflxapi.entity.Player;
import it.flaten.reflx.reflection.Composite;

public class ReflxPlayer extends Composite implements Player {
    public ReflxPlayer(Object player) {
        super(player);
    }

    @Override
    public String getName() {
        return (String) this.invoke("b_"); // ac.b_()
    }
}
