package polls.commands;

import core.commands.Command;
import polls.PollModule;

public class CommandRemoveOption extends Command {

    public CommandRemoveOption(PollModule pollModule) {
        super(pollModule, "remove", "Removes a voting option", new String[]{"removeOpt"}, new String[]{"ID", "Option #"});
    }
}
