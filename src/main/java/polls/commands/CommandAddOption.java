package polls.commands;

import core.Command;
import polls.PollModule;

public class CommandAddOption extends Command {

    public CommandAddOption(PollModule pollModule) {
        super(pollModule, "add", "Adds a voting option", new String[]{"addOpt, addOption"}, new String[]{"ID", "Option"});
    }
}
