package Communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import LowLevel.BoardData;

public class FileCommunication extends BoardCommunication {

	BufferedReader reader;

	public FileCommunication(CommunicationListener listener, String fileName) {
		super(listener);

		try {
			reader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void Finalize() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String readLine() {
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	protected Event getEvent() {
		String line = readLine();
		if (line == null)
			return null;

		delay(1000);
		int id = Integer.parseInt(line, 16);
		Event.Type eventType = Event.Type.fromInt(id);
		switch (eventType) {
		case BoardChange:
			String data = readLine();
			return new Event(Event.Type.BoardChange, new BoardData(data));
		case Move:
			return new Event(Event.Type.Move);
		default:
			return null;
		}

	}

}
