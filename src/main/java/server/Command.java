package server;

public class Command {
    private String command;
    private String description;
    private String[] parametrs;

    public Command(String command, String description) {
        this.command = command;
        this.description = description;
        this.parametrs = new String[0];
    }
    public Command(String command, String description, String[] parameters) {
        this.command = command;
        this.description = description;
        this.parametrs = parameters;
    }

    public String get() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
