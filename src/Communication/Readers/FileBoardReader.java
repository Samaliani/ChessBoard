package Communication.Readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Communication.Event;
import Communication.OutEvent;
import LowLevel.BoardData;

public class FileBoardReader extends BoardReader {

	String fileName;
	BufferedReader reader;
	
	Event fakeEvent;

	public FileBoardReader(String fileName) {
		super("");
		this.fileName = fileName;
	}

	protected String readLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Event getEvent() {
		
		// To resolve request position event
		if (fakeEvent != null)
		{
			Event result = fakeEvent;
			fakeEvent = null;
			return result;
		}
		
		String line = readLine();
		if (line == null)
			return null;

		delay(1000);
		int id = Integer.parseInt(line, 16);
		Event.Type eventType = Event.Type.fromInt(id);
		switch (eventType) {
		case BoardChange:
			String data = readLine();
			return new Event(eventType, new BoardData(data));
		case ButtonWhite:
		case ButtonBlack:
			return new Event(eventType);
		default:
			return null;
		}
	}

	@Override
	public String getPortName() {
		return "FILE";
	}

	@Override
	public void sendEvent(OutEvent event) throws IOException {
		// Start FEN can be loaded
		if (event.getType() == OutEvent.Type.RequestBoard)
			fakeEvent = new Event(Event.Type.BoardChange, BoardData.initialData); 			
	}

	@Override
	public void connect() throws IOException
	{
		reader = new BufferedReader(new FileReader(fileName));
	}
	
	@Override
	public void disconnect() throws IOException
	{
		reader.close();
	}
}
