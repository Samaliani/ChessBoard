package Communication;

public class SerialCommunication extends BoardCommunication {

	String portName;

	public SerialCommunication(CommunicationListener listener) {
		super(listener);

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

	public void setPortName(String portName) {
		this.portName = portName;
	}

}
