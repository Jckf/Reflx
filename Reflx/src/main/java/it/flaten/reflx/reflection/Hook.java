package it.flaten.reflx.reflection;

public class Hook {
    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}
