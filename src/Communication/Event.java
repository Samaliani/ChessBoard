package Communication;

import java.util.Date;

import LowLevel.BoardData;

public class Event {

	public enum Type {
		NoEvent, BoardChange, ButtonWhite, ButtonBlack;

		public static Type fromInt(int value) {
			switch (value) {
			case 0xFF:
				return BoardChange;
			case 0xFE:
				return ButtonWhite;
			case 0xFD:
				return ButtonBlack;
			default:
				return NoEvent;
			}
		}

		public static int toInt(Type value) {
			switch (value) {
			case BoardChange:
				return 0xFF;
			case ButtonWhite:
				return 0xFE;
			case ButtonBlack:
				return 0xFD;
			default:
				return 0;
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
