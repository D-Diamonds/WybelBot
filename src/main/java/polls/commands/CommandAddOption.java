package polls.commands;

import core.MessageSender;
import core.commands.Command;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import polls.Poll;
import polls.PollModule;

import java.util.ArrayList;
import java.util.List;

public class CommandAddOption extends Command {

    public CommandAddOption(PollModule pollModule) {
        super(pollModule, "add", "Adds a voting option", new String[]{"addOpt, addOption"}, new String[]{"ID", "Option"});
    }

    @Override
    public void execute(MessageReceivedEvent event, List<String> arguments) {
        try {
            PollModule pollModule = (PollModule) module;
            Poll poll = pollModule.getPoll(pollModule.getUserPolls(event.getAuthor()), Integer.parseInt(arguments.get(0)));

            if (poll != null) {
                poll.addOption(pollModule.findText(event, messagePhrases, 3));
                MessageSender.sendMessage(event, poll.toEmbed());
                module.queueSaving();
            } else {
                MessageSender.sendMessage(event, "polls.Poll not found.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }
}
