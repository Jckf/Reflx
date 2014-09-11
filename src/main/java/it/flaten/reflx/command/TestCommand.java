package it.flaten.reflx.command;

import it.flaten.reflx.api.CommandExecutor;
import it.flaten.reflx.api.CommandSender;

public class TestCommand implements CommandExecutor {
    @Override
    public void executeCommand(CommandSender sender, String command, String[] args) {
        System.out.println(sender.getName() + " ran test command!");
    }
}
