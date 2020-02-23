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

	private int tttWins;
	private int level;
	private int xp;

	private static final Map<String, Integer> XP_VALUES;
	static {
		HashMap<String, Integer> temp = new HashMap<>();
		temp.put("message", 1);
		XP_VALUES = Collections.unmodifiableMap(temp);
	}

	public UserStats(User user) {
		playerName = user.getName();
		playerID = user.getId();
		reset();
	}

	private void reset() {
		tttWins = 0;
		level = 0;
		xp = 0;
	}

	public void addXP(String type) {
		if (XP_VALUES.get(type) != null)
			xp += XP_VALUES.get(type);
		else
			System.out.println("Invalid XP Type");
	}

	// returns formatted board for discord output
	public MessageEmbed toEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(playerName + "'s " + StatUpdater.getModuleName());
		eb.setColor(new Color(25, 255, 133));

		eb.addField("Level:", Integer.toString(level), false);
		eb.addField("XP", Integer.toString(xp), false);
		eb.addField(TicTacToeUpdater.getModuleName() + " wins", Integer.toString(tttWins), false);

		return eb.build();
	}
}
