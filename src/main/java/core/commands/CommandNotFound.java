package core;

public class CommandNotFound extends Command {

    public CommandNotFound() {
        super(new EmptyModule(), "not-found", "Command not found", new String[]{}, null, null);
    }

    @Override
    public void execute() {

    }
}
