package core;

import core.commands.CommandHelp;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class DefaultModule extends Module<Object> {

    public DefaultModule() {
        super("default", "help", new Color(111, 131, 153));
        registerCommand(new CommandHelp(this));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event, List<String> phrases) {
        if (isCommand(phrases) && phrases.get(0).substring(0, "help".length()).equalsIgnoreCase("help")) {
            helpCmd(event);
        }
    }

    public void helpCmd(MessageReceivedEvent event) {
        MessageSender.sendMessage(event, getCommand("help").getHelpMessage());
    }
}
