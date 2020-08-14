package core.commands;

import core.DefaultModule;

public class CommandNotFound extends Command {

    public CommandNotFound() {
        super(new DefaultModule(), "not-found", "Command not found", new String[]{}, null);
    }

    @Override
    public void execute() {

    }
}
