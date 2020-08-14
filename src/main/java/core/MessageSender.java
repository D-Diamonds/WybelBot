package core;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import javax.annotation.Nonnull;

public abstract class MessageSender {
	public static void sendMessage(@Nonnull MessageReceivedEvent event, Object message) {
		MessageChannel channel = event.getChannel();
		try {
			if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_WRITE))
				if (message instanceof String)
					channel.sendMessage((String) message).queue();
				else if (message instanceof MessageEmbed)
					channel.sendMessage((MessageEmbed) message).queue();
			else
				System.out.println("Cannot send message in guild " + event.getGuild().getName() + " in channel " + channel.getName());
		} catch (InsufficientPermissionException e) {
			System.out.println(e.toString());
			System.out.println("Lack of permission in (guild, channel): " + event.getGuild().getName() + ", " + channel.getName());
		}
	}
}
