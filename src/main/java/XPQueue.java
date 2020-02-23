import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class XPQueue {

	private HashMap<User, ArrayList<String>> xpQueue = new HashMap<>();

	public XPQueue() {
	}

	public void addToQueue(User user, String type) {
		if (xpQueue.get(user) != null) {
			ArrayList<String> userQueue = xpQueue.get(user);
			userQueue.add(type);
		}
		else
			xpQueue.put(user, new ArrayList<>(Collections.singletonList(type)));
	}

	public HashMap<User, ArrayList<String>> getXpQueue() {
		return xpQueue;
	}

	public void clearQueue() {
		xpQueue = new HashMap<>();
	}
}
