package polls.commands;

import core.commands.Command;
import polls.PollModule;

public class CommandStart extends Command {

    public CommandStart(PollModule pollModule) {
        super(pollModule, "start", "Starts poll voting", new String[]{"start"}, new String[]{"ID"});
    }
}
