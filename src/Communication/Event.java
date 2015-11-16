package Communication;

import java.util.Date;

import LowLevel.BoardData;

public class Event {

	public enum Type {
		NoEvent, BoardChange, Move;

		public static Type fromInt(int value) {
			switch (value) {
			case 0xFF:
				return BoardChange;
			case 0xFE:
				return Move;
			default:
				return NoEvent;
			}
		}
	}

	Type type;
	BoardData data;
	Date time = new Date();

	public Event(Type type) {
		this.type = type;
	}

	public Event(Type type, BoardData data) {
		this.type = type;
		this.data = data;
	}

	public Type getType() {
		return type;
	}

	public Date getTime() {
		return time;
	}

	public BoardData getData() {
		return data;
	}
}
