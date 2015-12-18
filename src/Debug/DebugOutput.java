package Debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import Chess.Board;
import Communication.Event;

public class DebugOutput {

	String path;
	FileWriter writer;

	public DebugOutput(String path) {
		this.path = path;
	}

	public void processEvent(Event event) {

		if (writer == null)
			return;
		int eventId = Event.Type.toInt(event.getType());
		try {
			writer.write(Integer.toHexString(eventId));
			writer.write("\r\n");

			if (event.getData() != null) {
				writer.write(event.getData().toString());
				writer.write("\r\n");
			}
			writer.flush();
		} catch (IOException e) {
		}
	}

	private void openWriter(String fileName)
	{
		new File(path).mkdirs();

		try {
			writer = new FileWriter(path + "/" + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeWriter()
	{
		if (writer == null)
			return;
		
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void reset(String fen) {

		closeWriter();

		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String fileName = formatter.format(new Date());
		
		saveFen(fileName + ".fen", fen);
		openWriter(fileName);
	}
	
	private void saveFen(String fileName, String fen) {
		
		if(!fen.equals(Board.StartFEN))
		{
			try {
				writer = new FileWriter(path + "/" + fileName);
				writer.write(fen);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
