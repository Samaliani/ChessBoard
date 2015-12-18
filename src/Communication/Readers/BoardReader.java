package Communication.Readers;

import java.io.IOException;

import Communication.Event;
import Communication.OutEvent;

public class BoardReader {

	String portName;
	
	public BoardReader(String portName) {
		this.portName = portName;
	}
	
	public Event getEvent() throws IOException  {
		return null;
	}

	public void sendEvent(OutEvent event) throws IOException {
	}

	public String getPortName() {
		return portName;
	}

	public void connect() throws IOException {
	}

	public void disconnect() throws IOException {
	}

}
