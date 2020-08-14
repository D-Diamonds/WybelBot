package polls.commands;

import core.commands.Command;
import polls.PollModule;

public class CommandCreate extends Command {

    public CommandCreate(PollModule pollModule) {
        super(pollModule, "create", "Creates poll", new String[]{"create"}, new String[]{"Poll"});
    }
}
