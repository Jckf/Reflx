package it.flaten.reflxapi.command;

public interface CommandHandler {
    public void registerCommand(String command, CommandExecutor executor);

    public int executeCommand(Object sender, String command);
}
