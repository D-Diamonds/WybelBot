import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Poll implements Serializable {
	private final static long serialVersionUID = 103L;

	private String question;
	private ArrayList<PollOption> options = new ArrayList<>();
	private boolean voting = false;

	private String pollAuthorID;
	private String pollAuthorName;

	private ArrayList<String> voters = new ArrayList<>();

	private int pollID;
	private static int pollCount = 0;

	public Poll(String question, String pollAuthorID, String pollAuthorName) {
		pollID = pollCount;
		pollCount++;
		this.pollAuthorID = pollAuthorID;
		this.pollAuthorName = pollAuthorName;
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}

	public int getPollID() {
		return pollID;
	}

	public void addOption(String option) {
		options.add(new PollOption(option));
	}

	public void removeOption(int index) {
		options.remove(index);
	}

	public void editOption(String option, int optionNum) {
		options.set(optionNum, new PollOption(option));
	}

	public boolean addVote(int optionNum, String userID) {
		if (voting && !voters.contains(userID)) {
			options.get(optionNum).increment();
			voters.add(userID);
			return true;
		}
		return false;
	}

	public boolean start(User user) {
		if (user.getId().equals(pollAuthorID) && options.size() > 0)
			voting = true;
		return voting;
	}

	public MessageEmbed toEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(question);
		eb.setColor(new Color(0, 95, 37));
		int counter = 0;
		for (PollOption pollOption : options) {
			eb.addField("**" + counter + ")** " + pollOption.getOption(), "Votes: " + pollOption.getVotes(), false);
			counter++;
		}
		eb.addField("Voting: " + ((voting) ? "Enabled" : "Disabled"), "Poll created by " + pollAuthorName + " | ID: " + pollID, false);

		return eb.build();
	}
}
