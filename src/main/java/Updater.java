import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public abstract class Updater<Object> {

	public DataSaver<Object> dataSaver;

	private Object updatingObject;


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

	public abstract void onMessageReceived(MessageReceivedEvent event);
}
