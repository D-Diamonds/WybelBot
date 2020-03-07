import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Poll implements Serializable {
	private String question;
	private ArrayList<PollOption> options;
	private boolean voting;

	private ArrayList<String> voters;

	private int pollID;
	private static int pollCount = 0;

	public Poll(String question) {
		pollCount++;
		pollID = pollCount;
		this.question = question;
		this.options = new ArrayList<>();
		voting = false;
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
			return true;
		}
		return false;
	}

	public void start() {
		voting = true;
	}

	public MessageEmbed toEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(question);
		eb.setColor(new Color(0, 95, 37));
		for (PollOption pollOption : options) {
			eb.addField("", pollOption.getOption(), false);
			eb.addField("", Integer.toString(pollOption.getVotes()), true);
		}

		return eb.build();
	}
}
