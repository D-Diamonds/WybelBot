package core;

import core.commands.Command;
import core.commands.CommandNotFound;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public abstract class Module<Object> {

    private static final char COMMAND_SYMBOL = '!';

    public final String MODULE_NAME;
    public final String MODULE_DATA_PATH;
    public final String MODULE_COMMAND;

    protected final Color color;

    protected final Map<String[], Command> commands = new LinkedHashMap<>();

    public DataSaver<Object> dataSaver;

    private Object updatingObject;

    public Module(String name, String command, Color color) {
        this.color = color;
        this.MODULE_NAME = name;
        this.MODULE_COMMAND = command;

        this.MODULE_DATA_PATH = "src/" + BotRunner.getBotName() + "Data" + MODULE_NAME;
    }

    public Object getUpdatingObject() {
        return updatingObject;
    }

    public void createDataSaver(Object object, String moduleName, String moduleDataPath) {
        dataSaver = new DataSaver<>(moduleName, moduleDataPath, object);
        updatingObject = dataSaver.onStart();
    }

    public void enableSaving() {
        dataSaver.enableSaving();
    }

    public void resetUpdatingObject(Object object) {
        updatingObject = object;
    }

    protected void registerCommand(Command command) {
        commands.put(command.getAliases(), command);
    }

    public abstract void onMessageReceived(MessageReceivedEvent event, List<String> phrases);
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
    }

    public Command getCommand(String cmdName) {
        for (Map.Entry<String[], Command> entry : commands.entrySet()) {
            if (Arrays.stream(entry.getKey()).anyMatch(cmdName::equalsIgnoreCase)) {
                return entry.getValue();
            }
        }
        return new CommandNotFound();
    }

    public boolean isModuleCommand(String phrase) {
        return MODULE_NAME.equalsIgnoreCase(phrase);
    }

    public boolean isCommand(List<String> phrases) {
        return phrases.size() > 0 && phrases.get(0).startsWith(String.valueOf(COMMAND_SYMBOL));
    }

    public void helpCmd(MessageReceivedEvent event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(MODULE_NAME + " Commands:");
        eb.setColor(color);

        for (Command command : commands.values()) {
            eb.addField(command.getDescription(), command.getHelpMessage(), false);
        }

        MessageSender.sendMessage(event, eb.build());
    }
}
