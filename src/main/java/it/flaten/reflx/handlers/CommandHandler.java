package it.flaten.reflx.handlers;

public class CommandHandler {
    public int executeCommand(Object sender, String command) {
        System.out.println("Intercepted: " + command);

        return 0;
    }
}
