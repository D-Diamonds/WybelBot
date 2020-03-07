import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.List;

public class BotRunner extends ListenerAdapter {

	private static TicTacToeUpdater ticTacToeUpdater = new TicTacToeUpdater();
	private static StatUpdater statUpdater = new StatUpdater();
	private static PollUpdater pollUpdater = new PollUpdater();
	private static final String botName = "Wybel";
	private static XPQueue xpQueue = new XPQueue();


	public static void main(String[] args) throws LoginException {
		// builds discord interaction
		JDABuilder builder = new JDABuilder(args[0]);
		builder.addEventListeners(new BotRunner());
		builder.build();
		System.out.println();

	}

	public static String getBotName() {
		return botName;
	}

	public static XPQueue getXpQueue() {
		return xpQueue;
	}

	private void helpCmd(MessageReceivedEvent event) {
		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(botName + " Modules:");
		eb.setColor(new Color(0, 0, 255));
		eb.addField("**List help modules**", "!help", false);
		eb.addField("**List " + TicTacToeUpdater.moduleName + " commands**", TicTacToeUpdater.moduleCommand + " help", false);
		eb.addField("**List " + StatUpdater.moduleName + " commands**", StatUpdater.moduleCommand + " help", false);
		eb.addField("**List " + PollUpdater.moduleName + " commands**", PollUpdater.moduleCommand + " help", false);
		MessageSender.sendMessage(event, eb.build());
	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
		if (!event.getAuthor().isBot()) {
			Message message = event.getMessage();
			String[] messagePhrases = message.getContentDisplay().toLowerCase().split(" ");

			if (messagePhrases.length > 0) {
				// help command
				if (messagePhrases[0].equals("!help"))
					helpCmd(event);
					// tictactoe commands
				else if (messagePhrases.length >= 2) {
					if (messagePhrases[0].equals(TicTacToeUpdater.moduleCommand))
						ticTacToeUpdater.onMessageReceived(event);
					else if (messagePhrases[0].equals(PollUpdater.moduleCommand))
						pollUpdater.onMessageReceived(event);
				}
				// stat commands/events
				statUpdater.onMessageReceived(event);
			}
			statUpdater.setXpQueue(xpQueue.getXpQueue());
			xpQueue.clearQueue();
		}
	}

	@Override
	public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
		super.onGuildMemberJoin(event);
		Member member = event.getMember();
		Guild guild = event.getGuild();

		// adds default role to Diamond Elysium Discord server
		List<Role> roles = guild.getRolesByName("Tyro", true);
		if (guild.getName().equals("Diamond Elysium") && roles.size() > 0) {
			System.out.println("Adding role Tyro to " + member.getEffectiveName());
			guild.addRoleToMember(member, roles.get(0)).complete();
		}
		statUpdater.onGuildMemberJoin(event);
	}
}