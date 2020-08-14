package polls.commands;

import core.Command;
import polls.PollModule;

public class CommandGet extends Command {

    public CommandGet(PollModule pollModule) {
        super(pollModule, "get", "Gets poll", new String[]{"get"}, new String[]{"ID"});
    }
}
