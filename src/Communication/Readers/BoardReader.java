package Communication.Readers;

import java.io.IOException;

import Communication.Event;

public class BoardReader {

	String portName;
	
	public BoardReader(String portName) {
		this.portName = portName;
	}
	
	public Event getEvent() {
		return null;
	}

	public String getPortName() {
		return portName;
	}

	public void connect() throws IOException {
	}

	public void disconnect() throws IOException {
	}
}
