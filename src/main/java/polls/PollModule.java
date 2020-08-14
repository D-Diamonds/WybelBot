package polls;

import core.commands.Command;
import core.MessageSender;
import core.Module;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import polls.commands.CommandAddOption;
import polls.commands.CommandCreate;
import polls.commands.CommandEditOption;
import polls.commands.CommandGet;
import polls.commands.CommandGetAll;
import polls.commands.CommandRemoveOption;
import polls.commands.CommandStart;
import polls.commands.CommandVote;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class PollModule extends Module<Hashtable<String, ArrayList<Poll>>> {

    public PollModule() {
        super("Poll", "!poll", new Color(0, 95, 37));
        createDataSaver(new Hashtable<>(), MODULE_NAME, MODULE_DATA_PATH);

        registerCommand(new CommandCreate(this));
        registerCommand(new CommandGet(this));
        registerCommand(new CommandAddOption(this));
        registerCommand(new CommandRemoveOption(this));
        registerCommand(new CommandEditOption(this));
        registerCommand(new CommandGetAll(this));
        registerCommand(new CommandVote(this));
        registerCommand(new CommandStart(this));
    }

    // finds player's board from list of ongoing games
    private ArrayList<Poll> getUserPolls(User user) {
        return getUpdatingObject().get(user.getId());
    }

    private void getidsCmd(MessageReceivedEvent event) {
        List<Member> taggedMembers = event.getMessage().getMentionedMembers();
        if (taggedMembers.size() == 0) {
            sendPollIds(event.getAuthor(), event);
        } else {
            sendPollIds(taggedMembers.get(0).getUser(), event);
        }
    }

    private void sendPollIds(User user, MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        ArrayList<Poll> polls = getUserPolls(user);
        eb.setTitle(user.getName() + "'s Polls");
        eb.setColor(new Color(0, 95, 37));
        if (polls != null) {
            for (Poll poll : polls) {
                eb.addField(poll.getQuestion(), "ID: " + poll.getPollID(), false);
            }
        } else {
            MessageSender.sendMessage(event, "Polls not found.");
        }
        MessageSender.sendMessage(event, eb.build());
    }

    private Poll getPoll(ArrayList<Poll> polls, int id) {
        for (Poll poll : polls) {
            if (poll.getPollID() == id) {
                return poll;
            }
        }
        return null;
    }

    private String findText(MessageReceivedEvent event, String[] messagePhrases, int startingIndex) {
        String message = event.getMessage().getContentDisplay();
        return message.substring(message.indexOf(messagePhrases[startingIndex - 1]) + 1 + messagePhrases[startingIndex - 1].length());
    }

    private void addOptCmd(String pollID, String[] messagePhrases, ArrayList<Poll> polls, MessageReceivedEvent event) {
        try {
            Poll poll = getPoll(polls, Integer.parseInt(pollID));
            if (poll != null) {
                poll.addOption(findText(event, messagePhrases, 3));
                MessageSender.sendMessage(event, poll.toEmbed());
                dataSaver.queueSaving();
            } else {
                MessageSender.sendMessage(event, "polls.Poll not found.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    private void removeOptCmd(String pollID, String optionNum, ArrayList<Poll> polls, MessageReceivedEvent event) {
        try {
            Poll poll = getPoll(polls, Integer.parseInt(pollID));
            if (poll != null) {
                poll.removeOption(Integer.parseInt(optionNum));
                MessageSender.sendMessage(event, poll.toEmbed());
                dataSaver.queueSaving();
            } else {
                MessageSender.sendMessage(event, "polls.Poll not found.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    private void editOptCmd(String pollID, String[] messagePhrases, ArrayList<Poll> polls, MessageReceivedEvent event) {
        try {
            Poll poll = getPoll(polls, Integer.parseInt(pollID));
            if (poll != null) {
                poll.editOption(findText(event, messagePhrases, 4), Integer.parseInt(messagePhrases[3]));
                MessageSender.sendMessage(event, poll.toEmbed());
                dataSaver.queueSaving();
            } else {
                MessageSender.sendMessage(event, "polls.Poll not found.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    private void startCmd(ArrayList<Poll> polls, String pollID, MessageReceivedEvent event) {
        try {
            User user = event.getAuthor();
            int pollIDInt = Integer.parseInt(pollID);
            if (getPoll(polls, pollIDInt) != null) {
                Poll poll = getPoll(polls, pollIDInt);
                if (poll != null && poll.start(user)) {
                    MessageSender.sendMessage(event, poll.toEmbed());
                    dataSaver.queueSaving();
                } else {
                    MessageSender.sendMessage(event, "Invalid.");
                }
            } else {
                MessageSender.sendMessage(event, "polls.Poll not found.");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    private void voteCmd(ArrayList<Poll> polls, String pollID, String optionNum, MessageReceivedEvent event) {
        try {
            Poll poll = Objects.requireNonNull(getPoll(polls, Integer.parseInt(pollID)));
            if (poll.addVote(Integer.parseInt(optionNum), event.getAuthor().getId())) {
                MessageSender.sendMessage(event, poll.toEmbed());
                MessageSender.sendMessage(event, "VOTE SUCCESSFUL");
                dataSaver.queueSaving();
            } else {
                MessageSender.sendMessage(event, poll.toEmbed());
                MessageSender.sendMessage(event, "VOTE FAILED");
            }
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println(e.toString());
        }
    }

    public void createCmd(ArrayList<Poll> polls, String[] messagePhrases, MessageReceivedEvent event) {
        User user = event.getAuthor();
        Poll poll = new Poll(findText(event, messagePhrases, 2), user.getId(), user.getName());
        polls.add(poll);
        MessageSender.sendMessage(event, poll.toEmbed());
        dataSaver.queueSaving();
    }

    public void getCmd(ArrayList<Poll> polls, String pollID, MessageReceivedEvent event) {
        Poll poll = getPoll(polls, Integer.parseInt(pollID));
        if (poll != null) {
            MessageSender.sendMessage(event, poll.toEmbed());
        } else {
            MessageSender.sendMessage(event, "polls.Poll not found.");
        }
    }

    public void onMessageReceived(MessageReceivedEvent event, List<String> phrases) {
        int length = phrases.size();
        if (isModuleCommand(phrases.get(0)))
        if (length > 1) {
            Command command = getCommand(phrases.get(1));
            if (command != null && command.isValidInput(phrases)) {
                command.execute();
            }
        }

        if (length > 0) {

        }

        if (phrases.length >= MINIMUM_ARGS) {
            //MessageChannel channel = event.getChannel();
            User author = event.getAuthor();
            String[] messagePhrases = event.getMessage().getContentDisplay().toLowerCase().split(" ");

            if (messagePhrases[1].equals("help")) {
                helpCmd(event);
                return;
            }
            ArrayList<Poll> polls = new ArrayList<>();
            if (getUserPolls(author) == null) {
                getUpdatingObject().put(author.getId(), polls);
            } else {
                polls = getUserPolls(author);
            }

            if (messagePhrases[1].equals("start")) {
                startCmd(polls, messagePhrases[2], event);
            } else if (messagePhrases[1].equals("get")) {
                getCmd(polls, messagePhrases[2], event);
            } else if (messagePhrases[1].equals("getids")) {
                getidsCmd(event);
            } else if (messagePhrases.length >= 3) {
                if (messagePhrases[1].equals("create")) {
                    createCmd(polls, messagePhrases, event);
                } else if (messagePhrases.length >= 4) {
                    if (messagePhrases[1].equals("addopt")) {
                        addOptCmd(messagePhrases[2], messagePhrases, polls, event);
                    } else if (messagePhrases[1].equals("removeopt")) {
                        removeOptCmd(messagePhrases[2], messagePhrases[3], polls, event);
                    } else if (messagePhrases[1].equals("vote")) {
                        voteCmd(polls, messagePhrases[2], messagePhrases[3], event);
                    } else if (messagePhrases.length >= 5) {
                        if (messagePhrases[1].equals("editopt")) {
                            editOptCmd(messagePhrases[2], messagePhrases, polls, event);
                        }
                    }
                }

            }
        }
    }

}
