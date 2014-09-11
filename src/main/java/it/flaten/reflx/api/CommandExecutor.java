package it.flaten.reflx.api;

public interface CommandExecutor {
    public void executeCommand(CommandSender sender, String command, String[] args);
}
