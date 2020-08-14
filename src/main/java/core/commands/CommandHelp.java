package core.commands;

import core.BotRunner;
import core.DefaultModule;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class CommandHelp extends Command {

    public CommandHelp(DefaultModule module) {
        super(module, "help", "List help modules", new String[]{"help"}, null);
    }

    public EmbedBuilder getHelpMessage(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(BotRunner.getBotName() + " Modules:");
        eb.setColor(new Color(0, 0, 255));
        eb.addField("**List help modules**", "!help", false);
        BotRunner.getModules().forEach(module -> eb.addField("**List " + module.MODULE_NAME + " commands**", module.MODULE_COMMAND + " help", false));
        return eb;
    }
}
