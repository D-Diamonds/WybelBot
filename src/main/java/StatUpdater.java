import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StatUpdater extends Updater {

	private Hashtable<String, UserStats> statsTable;

	private static final String moduleName = "Stats";
	private static final String moduleCommand = "!stats";
	private static final String moduleDataPath = "src/" + BotRunner.getBotName() + "Data" + moduleName;

	public StatUpdater() {
		createDataSaver();
	}

	public static String getModuleCommand() {
		return moduleCommand;
	}

	public static String getModuleName() {
		return moduleName;
	}

	// finds player's board from list of ongoing games
	private UserStats getUserStats(User user) {
		return statsTable.get(user.getId());
	}

	private void createUserStat(User user) {
		statsTable.put(user.getId(), new UserStats(user));
		dataSaver.queueSaving();
	}

	public void setXpQueue(HashMap<User, ArrayList<String>> xpQueue) {
		for (Map.Entry entry : xpQueue.entrySet()) {
			UserStats entryStats = getUserStats((User) entry.getKey());
			for (String type : (ArrayList<String>) entry.getValue()) {
				entryStats.incrementStats(type);
				System.out.println("Adding " + type + " xp to user " + ((User) entry.getKey()).getName());
			}
		}
	}

	// Stats message commands
	public void onMessageReceived(MessageReceivedEvent event) {
		if (!event.getAuthor().isBot()) {
			MessageChannel channel = event.getChannel();
			User author = event.getAuthor();
			String[] messagePhrases = event.getMessage().getContentDisplay().toLowerCase().split(" ");

			UserStats userStats;
			if (getUserStats(author) == null)
				createUserStat(author);
			userStats = getUserStats(author);

			userStats.addXP("message");

			if (messagePhrases[0].equals(moduleCommand)) {
				// help
				if (messagePhrases.length >= 2 && messagePhrases[1].equals("help")) {
					EmbedBuilder eb = new EmbedBuilder();
					eb.setTitle(BotRunner.getBotName() + " Instruction:");
					eb.setColor(new Color(80, 255, 236));
					eb.addField("**Get user stats**", moduleCommand + " get", false);
					channel.sendMessage(eb.build()).queue();
				}
				if (messagePhrases.length >= 2 && messagePhrases[1].equals("get")) {
					channel.sendMessage(userStats.toEmbed()).queue();
				}
			}

			if (userStats.isLeveledUp()) {
				channel.sendMessage("Congrats <@" + author.getId() + "> on level " + userStats.getLevel() + "!").queue();
				userStats.disableLvlUp();
			}
			dataSaver.queueSaving();
		}
	}

	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		User user = event.getUser();
		if (getUserStats(user) == null) {
			createUserStat(user);
		}
	}

	// creates DataSaver
	public void createDataSaver() {
		statsTable = new Hashtable<>();
		dataSaver = new DataSaver(moduleName, moduleDataPath, statsTable);
		statsTable = (Hashtable<String, UserStats>) dataSaver.onStart();
		if (statsTable == null)
			statsTable = new Hashtable<>();
		System.out.println("Loaded " + statsTable.size() + " " + moduleName + " entries");
	}

	// enables dataSaver saving
	public void enableSaving() {
		dataSaver.enableSaving();
		System.out.println("Saved " + statsTable.size() + " " + moduleName + " entries");
	}

}
