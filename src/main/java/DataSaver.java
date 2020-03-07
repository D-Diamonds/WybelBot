import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataSaver<Object> {

	private String moduleName;
	private String moduleDataPath;

	private Object dataPointer;

	private ScheduledExecutorService scheduledExecutorService;
	private boolean saving;

	public DataSaver (final String moduleName, final String moduleDataPath, Object dataPointer) {
		this.moduleName = moduleName;
		this.moduleDataPath = moduleDataPath;
		this.dataPointer = dataPointer;
		enableSaving();
	}

	@SuppressWarnings("unchecked")
	public Object onStart() {
		try {
			System.out.println("\nNow loading " + BotRunner.getBotName() + " " + moduleName + " data...");
			File srcFolder = new File("src");
			if (!srcFolder.exists()) {
				srcFolder.mkdir();
			}
			File dataFile = new File(moduleDataPath);
			if (!dataFile.exists()) {
				dataFile.createNewFile();
				FileOutputStream fileOutputStream = new FileOutputStream(moduleDataPath);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(dataPointer);
				objectOutputStream.close();
				fileOutputStream.close();
			}
			FileInputStream fileInputStream = new FileInputStream(moduleDataPath);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			dataPointer = (Object) objectInputStream.readObject();
			System.out.println("Completed loading " + BotRunner.getBotName() + " " + moduleName + " data!\n");
			return dataPointer;
		}
		catch (IOException | ClassNotFoundException e) {
			System.out.println(e.toString());
		}
		return null;
	}

	// enables the serialization and saving process that runs every 30 seconds
	public void enableSaving() {
		scheduledExecutorService = Executors.newScheduledThreadPool (1);
		Runnable saveDataRunnable = () -> {
			try {
				if (saving) {
					System.out.println("\nNow saving " + BotRunner.getBotName() + " " + moduleName + " data...");
					FileOutputStream fileOutputStream = new FileOutputStream(moduleDataPath);
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
					objectOutputStream.writeObject(dataPointer);
					objectOutputStream.close();
					fileOutputStream.close();
					System.out.println("Completed saving " + BotRunner.getBotName() + " " + moduleName + " data!\n");
					unqueueSaving();
				}

			} catch (IOException e) {
				System.out.println(e.toString());
			}
		};
		scheduledExecutorService.scheduleAtFixedRate(saveDataRunnable, 10, 30, TimeUnit.SECONDS);
	}

	public void disableSaving() {
		scheduledExecutorService.shutdown();
	}

	public void queueSaving() {
		if (!saving) {
			saving = true;
			System.out.println("Saving queued for module " + moduleName);
		}
	}

	private void unqueueSaving() {
		saving = false;
		System.out.println("Saving unqueued for module " + moduleName);
	}
}
