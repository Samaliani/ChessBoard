package Communication;

public abstract class BoardCommunication extends Thread {

	EventStorage eventStorage;

	public BoardCommunication(EventStorage events) {
		eventStorage = events;
	}

	private void delay(int millis) {
		try {
			sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	abstract protected Event getEvent();

	public void run() {
		while (true) {
			Event event = getEvent();
			if (event == null)
				break;

			eventStorage.addEvent(event);
			delay(50);
		}
	}
}
