package Communication.Readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Communication.Event;
import LowLevel.BoardData;

public class FileBoardReader extends BoardReader {

	String fileName;
	BufferedReader reader;

	public FileBoardReader(String fileName) {
		super("");
		this.fileName = fileName;
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
	
	public Event getEvent() {
		String line = readLine();
		if (line == null)
			return null;

		delay(100);
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
	
	public String getPortName() {
		return "FILE";
	}
}
