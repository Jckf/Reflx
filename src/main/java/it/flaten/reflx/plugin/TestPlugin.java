package it.flaten.reflx.plugin;

import it.flaten.reflx.api.CommandExecutor;
import it.flaten.reflx.api.CommandSender;
import it.flaten.reflx.api.Plugin;

public class TestPlugin extends Plugin implements CommandExecutor {
    @Override
    public void onEnable() {
        this.getServer().getCommandHandler().registerCommand("test", this);

        System.out.println(this.getName() + " loaded :)");
    }

    @Override
    public void executeCommand(CommandSender sender, String command, String[] args) {
        System.out.println(sender.getName() + " ran test command :)");
    }
}
