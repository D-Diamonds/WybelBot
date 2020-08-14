package polls.commands;

import core.MessageSender;
import core.commands.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import polls.Poll;
import polls.PollModule;

import java.util.ArrayList;
import java.util.List;

public class CommandStart extends Command {

    public CommandStart(PollModule pollModule) {
        super(pollModule, "start", "Starts poll voting", new String[]{"start"}, new String[]{"ID"});
    }

    @Override
    public void execute(MessageReceivedEvent event, List<String> arguments) {
        try {

            PollModule pollModule = (PollModule) module;

            ArrayList<Poll> polls = pollModule.getUserPolls(event.getAuthor());
            User user = event.getAuthor();
            int pollIDInt = Integer.parseInt(arguments.get(0));

            Poll poll = pollModule.getPoll(polls, pollIDInt);
            if (poll != null) {
                if (poll.start(user)) {
                    MessageSender.sendMessage(event, poll.toEmbed());
                    module.queueSaving();
                } else {
                    MessageSender.sendMessage(event, "Invalid.");
                }
            } else {
                MessageSender.sendMessage(event, "Poll not found.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }
}
