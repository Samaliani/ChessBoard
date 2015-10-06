package Communication;

public enum EventType {

	NoEvent, BoardChange, Move;

	public static EventType fromInt(int value) {
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
