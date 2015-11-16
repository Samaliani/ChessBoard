package Communication;

public abstract class BoardCommunication implements Runnable {

	CommunicationListener listener;
	boolean isStop;

	public BoardCommunication(CommunicationListener listener) {
		this.listener = listener;
		isStop = false;
	}

	protected void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	abstract protected Event getEvent();

	public void run() {
		while (!isStop) {
			Event event = getEvent();
			if (event != null)
				listener.processEvent(event);
			else
				delay(50);
		}
	}
	
	public void stop()
	{
		isStop = true;
	}
}
