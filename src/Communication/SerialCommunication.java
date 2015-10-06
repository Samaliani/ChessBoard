package Communication;

public class SerialCommunication extends BoardCommunication {

	public SerialCommunication(EventStorage eventStorage) {
		super(eventStorage);

	}

	protected Event getEvent() {
		return null;
	}

	public void run() {
		// Read serial events
		// 1. Board changed
	}

	protected void readBoardData() {
		// TODO read

	}

}
