import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserStats implements Serializable {
	private final static long serialVersionUID = 101L;

	private String username;

	// base leveling
	private int level;
	private int xp;

	// ttt stats
	private int tttWins;
	private int tttTies;
	private int tttLosses;

	private boolean leveledUp;

	private static final Map<String, Integer> XP_VALUES;
	static {
		HashMap<String, Integer> temp = new HashMap<>();
		temp.put("ttt_win", 10);
		temp.put("ttt_tie", 3);
		temp.put("ttt_loss", 0);
		XP_VALUES = Collections.unmodifiableMap(temp);
	}

	public static Map<String, Integer> getXpValues() {
		return XP_VALUES;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserStats(User user) {
		this.username = user.getName();
		reset();
	}

	public void forceLvlUp() {
		xp += (getLevelXP() - xp);
		if (checkLvlUp())
			levelUp();
	}

	private void reset() {
		tttWins = 0;
		tttTies = 0;
		tttLosses = 0;
		level = 1;
		xp = 0;
		leveledUp = false;
	}

	private int getLevelXP() {
		return (int) Math.ceil(.1*Math.pow(level, 2) + 5*level);
	}

	public int getLevel() {
		return level;
	}

	public void incrementStats(String type) {
		switch (type) {
			case "ttt_win":
				tttWins++;
				break;
			case "ttt_tie":
				tttTies++;
				break;
			case "ttt_loss":
				tttLosses++;
				break;
		}
		addXP(type);
	}

	public void addXP(String type) {
		if (XP_VALUES.get(type) != null) {
			xp += XP_VALUES.get(type);
			if (checkLvlUp())
				levelUp();
		}
		else
			System.out.println("Invalid XP Type");
	}

	public void disableLvlUp() {
		leveledUp = false;
	}

	public boolean isLeveledUp() {
		return leveledUp;
	}

	private void levelUp() {
		xp = xp % getLevelXP();
		level++;
		leveledUp = true;
	}

	private boolean checkLvlUp() {
		return xp >= getLevelXP();
	}

	// returns formatted board for discord output
	public MessageEmbed toEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(username + "'s " + StatUpdater.MODULE_NAME);
		eb.setColor(new Color(25, 255, 133));

		eb.addField("Level:", Integer.toString(level), false);
		eb.addField("XP:", xp + "/" + getLevelXP(), false);
		eb.addField(TicTacToeUpdater.MODULE_NAME + " W | L | T:", tttWins + " | " + tttLosses + " | " + tttTies, false);

		return eb.build();
	}
}
