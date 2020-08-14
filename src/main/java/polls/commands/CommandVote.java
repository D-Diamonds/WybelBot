package polls.commands;

import core.Command;
import polls.PollModule;

public class CommandVote extends Command {

    public CommandVote(PollModule pollModule) {
        super(pollModule, "vote", "Votes on poll", new String[]{"vote"}, new String[]{"ID", "Option #"});
    }
}
