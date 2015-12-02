package Debug;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import Communication.Event;

public class DebugOutput {

	String path;
	FileWriter writer;

	public DebugOutput(String path) {
		this.path = path;
		reset();
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
	
	public void reset() {

		closeWriter();
		
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		openWriter(formatter.format(new Date()));
	}

}
