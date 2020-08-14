package core;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nonnull;


public abstract class Module<Object> {

    protected final int MINIMUM_ARGS;

    public final String MODULE_NAME;
    public final String MODULE_DATA_PATH;
    public final String MODULE_COMMAND;

    public DataSaver<Object> dataSaver;

    private Object updatingObject;

    public Module(String name, String command) {
        this(name, command, 0);
    }

    public Module(String name, String command, int minArgs) {
        this.MODULE_NAME = name;
        this.MODULE_COMMAND = command;

        this.MINIMUM_ARGS = minArgs;

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

    public abstract void onMessageReceived(MessageReceivedEvent event, String... phrases);

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
    }
}
