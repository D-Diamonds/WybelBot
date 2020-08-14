package core.commands;

import core.Module;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class CommandNotFound extends Command {

    public CommandNotFound(Module<?> module) {
        super(module, "not-found", "Command not found", new String[]{}, null);
    }

    @Override
    public void execute(MessageReceivedEvent event, List<String> arguments) {

    }
}
