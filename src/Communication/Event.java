package Communication;

import java.util.Date;

import LowLevel.BoardData;

public class Event {

	EventType type;
	BoardData data;
	Date time = new Date();

	public Event(EventType type) {
		this.type = type;
	}

	public Event(EventType type, BoardData data) {
		this.type = type;
		this.data = data;
	}

	public EventType getType() {
		return type;
	}

	public Date getTime() {
		return time;
	}

	public BoardData getData() {
		return data;
	}

}
