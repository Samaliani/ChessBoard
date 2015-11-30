package Communication;

public class EmptyCommunication extends BoardCommunication {

	String portName;

	public EmptyCommunication(String portName) {
		this.portName = portName;
	}

	protected Event getEvent() {
		return null;
	}

	public String getPortName() {
		return portName;
	}
}
