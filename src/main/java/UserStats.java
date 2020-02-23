import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserStats implements Serializable {

	private String playerName;
	private String playerID;

	// base leveling
	private int level;
	private int xp;
	private int levelXP;

	// ttt stats
	private int tttWins;
	private int tttTies;
	private int tttLosses;

	private boolean leveledUp;

	private static final Map<String, Integer> XP_VALUES;
	static {
		HashMap<String, Integer> temp = new HashMap<>();
		temp.put("message", 1);
		temp.put("ttt_win", 10);
		temp.put("ttt_tie", 3);
		temp.put("ttt_loss", 0);
		XP_VALUES = Collections.unmodifiableMap(temp);
	}

	public static Map<String, Integer> getXpValues() {
		return XP_VALUES;
	}

	public UserStats(User user) {
		playerName = user.getName();
		playerID = user.getId();
		reset();
	}

	private void reset() {
		tttWins = 0;
		tttTies = 0;
		tttLosses = 0;
		level = 1;
		xp = 0;
		levelXP = getLevelXP();
		leveledUp = false;
	}

	private int getLevelXP() {
		return (int) Math.ceil((4*(Math.pow(level, 3))) / 5.0);
	}

	public int getLevel() {
		return level;
	}

	public void incrementStats(String type) {
		if (type.equals("ttt_win"))
			tttWins++;
		else if (type.equals("ttt_tie"))
			tttTies++;
		else if (type.equals("ttt_loss"))
			tttLosses++;
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
		xp = xp % levelXP;
		level++;
		levelXP = getLevelXP();
		leveledUp = true;
	}

	private boolean checkLvlUp() {
		return xp >= levelXP;
	}

	// returns formatted board for discord output
	public MessageEmbed toEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(playerName + "'s " + StatUpdater.getModuleName());
		eb.setColor(new Color(25, 255, 133));

		eb.addField("Level:", Integer.toString(level), false);
		eb.addField("XP:", Integer.toString(xp) + "/" + Integer.toString(levelXP), false);
		eb.addField(TicTacToeUpdater.getModuleName() + " W | L | T:", Integer.toString(tttWins) + " | " + Integer.toString(tttLosses) + " | " + Integer.toString(tttTies), false);

		return eb.build();
	}
}
