package polls.commands;

import core.Command;
import polls.PollModule;

public class CommandEditOption extends Command {

    public CommandEditOption(PollModule pollModule) {
        super(pollModule, "edit", "Edits a voting option", new String[]{"editOpt", "editOption"}, new String[]{"ID", "Option #", "New Option"});
    }
}
