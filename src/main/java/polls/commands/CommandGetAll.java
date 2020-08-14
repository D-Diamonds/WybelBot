package polls.commands;

import core.Command;
import polls.PollModule;

public class CommandGetAll extends Command {

    public CommandGetAll(PollModule pollModule) {
        super(pollModule, "get-all", "Gets polls of User/Tagged User", new String[]{"getIDS"}, null, new String[]{"Tagged User"});
    }
}
