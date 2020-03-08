import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;

public class StatUpdater extends Updater<Hashtable<String, UserStats>> {

	public final static String MODULE_NAME = "Stats";
	public final static String MODULE_COMMAND = "!stats";
	public final static String MODULE_DATA_PATH = "src/" + BotRunner.getBotName() + "Data" + MODULE_NAME;
	
	public StatUpdater() {
		createDataSaver(new Hashtable<>(), MODULE_NAME, MODULE_DATA_PATH);
	}

	// finds player's board from list of ongoing games
	private UserStats getUserStats(User user) {
		return getUpdatingObject().get(user.getId());
	}

	private void createUserStat(User user) {
		getUpdatingObject().put(user.getId(), new UserStats(user));
		dataSaver.queueSaving();
	}

	public void setXpQueue(HashMap<MessageReceivedEvent, ArrayList<String>> xpQueue) {
		boolean updatingEntries = false;
		for (Map.Entry<MessageReceivedEvent, ArrayList<String>> entry : xpQueue.entrySet()) {
			updatingEntries = true;
			MessageReceivedEvent event = entry.getKey();
			User author = event.getAuthor();
			UserStats entryStats = getUserStats(event.getAuthor());
			for (String type : entry.getValue()) {
				entryStats.incrementStats(type);
				if (entryStats.isLeveledUp()) {
					MessageSender.sendMessage(event, "Congrats <@" + author.getId() + "> on level " + entryStats.getLevel() + "!");
					entryStats.disableLvlUp();
				}
				System.out.println("Adding " + type + " xp to user " + author.getName());
			}
		}
		if (updatingEntries) {
			dataSaver.queueSaving();
		}
	}

	private void helpCmd(MessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(MODULE_NAME + " Commands:");
		eb.setColor(new Color(80, 255, 236));
		eb.addField("**Get user stats**", MODULE_COMMAND + " get", false);
		MessageSender.sendMessage(event, eb.build());
	}

	// Stats message commands
	public void onMessageReceived(MessageReceivedEvent event) {
		//MessageChannel channel = event.getChannel();
		User author = event.getAuthor();
		String[] messagePhrases = event.getMessage().getContentDisplay().toLowerCase().split(" ");

		UserStats userStats;
		if (getUserStats(author) == null) {
			createUserStat(author);
			dataSaver.queueSaving();
		}
		userStats = getUserStats(author);
		if (userStats.getUsername() == null)
			userStats.setUsername(author.getName());

		if (messagePhrases[0].equals(MODULE_COMMAND) && messagePhrases.length >= 2) {
			// help
			if (messagePhrases[1].equals("help")) {
				helpCmd(event);
			}
			else if (messagePhrases[1].equals("get")) {
				MessageSender.sendMessage(event, userStats.toEmbed());
			}
			else if (author.getId().equals("221748640236961792") && messagePhrases[1].equals("lvlup")) {
				userStats.forceLvlUp();
				System.out.println("Forcing lvl up");
			}
		}

		if (userStats.isLeveledUp()) {
			MessageSender.sendMessage(event, "Congrats <@" + author.getId() + "> on level " + userStats.getLevel() + "!");
			userStats.disableLvlUp();
			dataSaver.queueSaving();
		}
	}

	// creates stat for users not in database
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		User user = event.getUser();
		if (getUserStats(user) == null) {
			createUserStat(user);
		}
	}
}
