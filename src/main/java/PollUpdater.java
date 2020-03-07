import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

public class PollUpdater extends Updater<Hashtable<String, ArrayList<Poll>>> {

	public static final String moduleName = "Poll";
	public static final String moduleCommand = "!stats";
	public static final String moduleDataPath = "src/" + BotRunner.getBotName() + "Data" + moduleName;

	public PollUpdater() {
		createDataSaver(new Hashtable<>());
	}

	private void helpCmd(MessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(moduleName + " Commands:");
		eb.setColor(new Color(0, 95, 37));
		eb.addField("**Creates poll**", moduleCommand + " create", false);
		MessageSender.sendMessage(event, eb.build());
	}

	// finds player's board from list of ongoing games
	private ArrayList<Poll> getUserPolls(User user) {
		return getUpdatingObject().get(user.getId());
	}

	private boolean getidsCmdTagged(MessageReceivedEvent event) {
		List<Member> taggedMembers = event.getMessage().getMentionedMembers();
		if (taggedMembers.size() == 0)
			return false;
		else
			MessageSender.sendMessage(event, "INSERT FIRST TAGGED USER'S POLLS");
		return true;
	}

	private Poll getPoll(ArrayList<Poll> polls, int id) {
		for (Poll poll : polls) {
			if (poll.getPollID() == id)
				return poll;
		}
		return null;
	}

	private String findOptionText(MessageReceivedEvent event, String[] messagePhrases, int startingIndex) {
		String message = event.getMessage().getContentDisplay();
		return message.substring(message.indexOf(messagePhrases[2]) + 1 + messagePhrases[startingIndex - 1].length());
	}

	private void addOptCmd(String pollID, String[] messagePhrases, ArrayList<Poll> polls, MessageReceivedEvent event) {
		try {
			Poll poll = Objects.requireNonNull(getPoll(polls, Integer.parseInt(pollID)));
			poll.addOption(findOptionText(event, messagePhrases, 3));
			MessageSender.sendMessage(event, "DISPLAY NEW POLL");
		}
		catch (NumberFormatException | NullPointerException e) {
			System.out.println(e.toString());
		}
	}

	private void removeOptCmd(String pollID, String optionNum, ArrayList<Poll> polls, MessageReceivedEvent event) {
		try {
			Poll poll = Objects.requireNonNull(getPoll(polls, Integer.parseInt(pollID)));
			poll.removeOption(Integer.parseInt(optionNum));
			MessageSender.sendMessage(event, "DISPLAY NEW POLL");
		}
		catch (NumberFormatException | NullPointerException e) {
			System.out.println(e.toString());
		}
	}

	private void editOptCmd(String pollID, String[] messagePhrases, ArrayList<Poll> polls, MessageReceivedEvent event) {
		try {
			Poll poll = Objects.requireNonNull(getPoll(polls, Integer.parseInt(pollID)));
			poll.editOption(findOptionText(event, messagePhrases, 4), Integer.parseInt(messagePhrases[3]));
			MessageSender.sendMessage(event, "DISPLAY NEW POLL");
		}
		catch (NumberFormatException | NullPointerException e) {
			System.out.println(e.toString());
		}
	}

	private void startCmd(ArrayList<Poll> polls, String pollID, MessageReceivedEvent event) {
		try {
			Poll poll = Objects.requireNonNull(getPoll(polls, Integer.parseInt(pollID)));
			poll.start();
			MessageSender.sendMessage(event, "DISPLAY STARTED POLL");
		}
		catch (NumberFormatException | NullPointerException e) {
			System.out.println(e.toString());
		}
	}

	private void voteCmd(ArrayList<Poll> polls, String pollID, String optionNum, MessageReceivedEvent event) {
		try {
			Poll poll = Objects.requireNonNull(getPoll(polls, Integer.parseInt(pollID)));
			if (poll.addVote(Integer.parseInt(optionNum), event.getAuthor().getId()))
				MessageSender.sendMessage(event, "VOTE SUCCESSFUL");
			else
				MessageSender.sendMessage(event, "VOTE FAILED");
		}
		catch (NumberFormatException | NullPointerException e) {
			System.out.println(e.toString());
		}
	}

	public void onMessageReceived(MessageReceivedEvent event) {
			//MessageChannel channel = event.getChannel();
			User author = event.getAuthor();
			String[] messagePhrases = event.getMessage().getContentDisplay().toLowerCase().split(" ");
			ArrayList<Poll> polls = getUserPolls(author);

			if (messagePhrases[1].equals("help"))
				helpCmd(event);
			else if (messagePhrases.length == 3) {
				if (messagePhrases[1].equals("create"))
					polls.add(new Poll(messagePhrases[2]));
				else if (messagePhrases[1].equals("start"))
					startCmd(polls, messagePhrases[2], event);

			}
			else if (messagePhrases.length >= 4) {
				if (messagePhrases[1].equals("addopt"))
					addOptCmd(messagePhrases[2], messagePhrases, polls, event);
				else if (messagePhrases[1].equals("removeopt"))
					removeOptCmd(messagePhrases[2], messagePhrases[3], polls, event);
				else if (messagePhrases[1].equals("vote")) {
					voteCmd(polls, messagePhrases[2], messagePhrases[3], event);
				}
				else if (messagePhrases.length >= 5) {
					if (messagePhrases[1].equals("editopt"))
						editOptCmd(messagePhrases[2], messagePhrases, polls, event);
				}
			}
			else if (messagePhrases[1].equals("getids") && !getidsCmdTagged(event)) {
					MessageSender.sendMessage(event, "INSERT AUTHOR'S POLLS");
			}
	}

}
