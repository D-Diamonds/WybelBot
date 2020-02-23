import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Updater<Object> {

	public DataSaver<Object> dataSaver;

	public abstract void createDataSaver();
	public abstract void enableSaving();
	public abstract void onMessageReceived(MessageReceivedEvent event);
}
