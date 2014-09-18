package no.jckf.reflx.sampleplugin;

import it.flaten.reflxapi.command.CommandExecutor;
import it.flaten.reflxapi.command.CommandSender;
import it.flaten.reflxapi.event.EventHandler;
import it.flaten.reflxapi.event.EventListener;
import it.flaten.reflxapi.event.player.PlayerLoginEvent;
import it.flaten.reflxapi.plugin.Plugin;

public class SamplePlugin extends Plugin implements CommandExecutor, EventListener {
    @Override
    public void onEnable() {
        this.getServer().getCommandHandler().registerCommand("test", this);

        this.getServer().getEventManager().registerEvents(this, this);

        System.out.println(this.getName() + " loaded :)");
    }

    @Override
    public void executeCommand(CommandSender sender, String command, String[] args) {
        System.out.println(sender.getName() + " ran test command :)");
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        System.out.println(event.getPlayer().getName() + " logged in :)");
    }
}
