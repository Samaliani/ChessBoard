package Communication;

public class OutEvent {

	public enum Type {
		NoEvent, RequestBoard, MoveSignal, ErrorSignal, FinalSignal, PositionSignal, NoErrorSignal, SadSignal;

		public static int toInt(Type value) {
			switch (value) {
			case RequestBoard:
				return 0x0F;
			case MoveSignal:
				return 0x0E;
			case ErrorSignal:
				return 0x0D;
			case FinalSignal:
				return 0x0C;
			case PositionSignal:
				return 0x0B;
			case NoErrorSignal:
				return 0x0A;
			case SadSignal:
				return 0x09;
			default:
				return 0;
			}
		}
	}

	Type type;

	public OutEvent(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

}
