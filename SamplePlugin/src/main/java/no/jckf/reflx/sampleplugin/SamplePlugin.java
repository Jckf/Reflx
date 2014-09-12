package no.jckf.reflx.sampleplugin;

import it.flaten.reflxapi.command.CommandExecutor;
import it.flaten.reflxapi.command.CommandSender;
import it.flaten.reflxapi.plugin.Plugin;

public class SamplePlugin extends Plugin implements CommandExecutor {
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
