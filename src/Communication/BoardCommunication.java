package Communication;

import java.util.ArrayList;
import java.util.List;

public abstract class BoardCommunication implements Runnable {

	List<CommunicationListener> listeners;
	boolean isStop;

	public BoardCommunication() {
		isStop = false;
		listeners = new ArrayList<CommunicationListener>();
	}

	public void addListener(CommunicationListener listener) {
		listeners.add(listener);
	}

	private void doProcessEvent(Event event) {
		for (CommunicationListener listener : listeners)
			listener.processEvent(event);
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
				doProcessEvent(event);
			else
				delay(50);
		}
	}

	public void stop() {
		isStop = true;
	}

	public String getPortName() {
		return "";
	}

	public void setPortName(String portName) {
	}

}
