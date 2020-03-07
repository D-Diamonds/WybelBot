import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public abstract class Updater<Object> {

	public DataSaver<Object> dataSaver;

	private Object updatingObject;

	private static String moduleName;
	private static String moduleCommand;
	private static String moduleDataPath;

	public Object getUpdatingObject() {
		return updatingObject;
	}

	public void createDataSaver(Object object) {
		dataSaver = new DataSaver<>(moduleName, moduleDataPath, object);
		updatingObject = dataSaver.onStart();
	}
	public void enableSaving() {
		dataSaver.enableSaving();
	}

	public void resetUpdatingObject(Object object) {
		updatingObject = object;
	}

	public abstract void onMessageReceived(MessageReceivedEvent event);
}
