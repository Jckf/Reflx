package it.flaten.reflxapi.command;

public interface CommandExecutor {
    public void executeCommand(CommandSender sender, String command, String[] args);
}
