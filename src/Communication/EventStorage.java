package Communication;

import java.util.ArrayList;
import java.util.List;

public class EventStorage {

	List<Event> events = new ArrayList<Event>();

	public EventStorage() {

	}

	public void addEvent(Event event) {
		events.add(event);
	}

	public int getEventCount() {
		return events.size();
	}

	public Event getEvent(int index) {
		return events.get(index);
	}

	public Event findEvent(EventType eventType) {
		for (Event event : events)
			if (event.type == eventType)
				return event;
		return null;
	}

	public void clearEvents(Event event) {
		while ((events.size() > 0) && (!events.get(0).time.after(event.time)))
			events.remove(0);

	}

}
