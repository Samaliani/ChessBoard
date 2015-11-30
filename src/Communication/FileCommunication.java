package Communication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import LowLevel.BoardData;

public class FileCommunication extends BoardCommunication {

	BufferedReader reader;

	public FileCommunication(String fileName) {
		super();

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
