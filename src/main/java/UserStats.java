import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.io.Serializable;

public class UserStats implements Serializable {

	private String playerName;
	private String playerID;

	private int tttWins;
	private int level;
	private int xp;

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

	// returns formatted board for discord output
	public MessageEmbed toEmbed() {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(playerName + "'s " + StatUpdater.getModuleName());
		eb.setColor(new Color(80, 255, 236));

		eb.addField("Level:", Integer.toString(level), false);
		eb.addField("XP", Integer.toString(level), false);
		eb.addField(TicTacToeUpdater.getModuleName() + " wins", Integer.toString(tttWins), false);

		return eb.build();
	}
}
