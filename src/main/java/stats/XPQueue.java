package stats;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class XPQueue {

	private final HashMap<MessageReceivedEvent, ArrayList<String>> xpQueue = new HashMap<>();

	public XPQueue() {
	}

	public void addToQueue(MessageReceivedEvent event, String type) {
		if (xpQueue.get(event) != null) {
			ArrayList<String> userQueue = xpQueue.get(event);
			userQueue.add(type);
		}
		else
			xpQueue.put(event, new ArrayList<>(Collections.singletonList(type)));
	}

	public HashMap<MessageReceivedEvent, ArrayList<String>> getXpQueue() {
		return xpQueue;
	}

	public void clearQueue() {
		xpQueue.clear();
	}
}
