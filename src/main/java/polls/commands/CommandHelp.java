package polls.commands;

import core.DefaultModule;
import core.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import polls.PollModule;

import java.util.List;

public class CommandHelp extends Command {

    public CommandHelp(PollModule pollModule) {
        super(pollModule, "help", "Adds a voting option", new String[]{"help"}, new String[]{});
    }

    @Override
    public void execute(MessageReceivedEvent event, List<String> arguments) {
        module.helpCmd(event);
    }
}
