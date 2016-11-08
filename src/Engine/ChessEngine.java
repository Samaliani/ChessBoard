package Engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ChessEngine {

	String cmd;
	Process engine;
	int timeout;
	InputStream input;
	OutputStream output;

	InputStreamReader reader;
	PrintWriter writer;

	String buffer;

	boolean uciok;
	String[] info;
	
	static String UCI = "uci";
	static String UCI_OK = "uciok";
	static String IS_READY = "isready";
	static String READY_OK = "readyok";
	static String QUIT = "quit";
	
	static String UCI_NEW_GAME = "ucinewgame";
	static String GO = "go";
	static String STOP = "stop";

	static String INFO = "info";
	static String SCORE = "score";
	static String CP = "cp";
	static String MATE = "mate";
	static String PV = "pv";
	static String BEST_MOVE = "bestmove";

	static String NEW_LINE = "\r\n";

	public ChessEngine(String cmd) {
		this.cmd = cmd;
		timeout = 50;
		uciok = false;
	}

	public static boolean isUCIEngine(String cmd) {

		// File exists
		File engineExecutable = new File(cmd);
		if (!engineExecutable.exists())
			return false;

		// Engine supports UCI
		ChessEngine ce = new ChessEngine(cmd);
		ce.start();
		ce.stop();

		return ce.uciok;
	}

	public boolean isAvailable() {

		File engineExecutable = new File(cmd);
		return engineExecutable.exists();
	}

	public boolean isUCI() {
		return uciok;
	}

	public boolean startNewGame() {
		sendCommand(UCI_NEW_GAME);
		return isReady();
	}

	private void startEngine() throws IOException {

		engine = Runtime.getRuntime().exec(cmd);

		reader = new InputStreamReader(engine.getInputStream());
		//InputStreamReader is = new InputStreamReader(engine.getInputStream());
		//reader = new BufferedReader(is);
		writer = new PrintWriter(engine.getOutputStream());

		info = readLines(true);
	}

	private void stopEngine() {

		if (engine != null) {
			quit();
			engine.destroy();
			engine = null;
		}

		reader = null;
		writer = null;
	}

	private void sendCommand(String command) {

		if (engine == null)
			return;

		writer.println(command);
		writer.flush();
	}

	private void waitForInput() {
		try {
			while (!reader.ready())
				;
		} catch (IOException e) {
		}
	}
	
	private boolean readyWithDelay() throws IOException, InterruptedException
	{
		if (reader.ready())
			return true;
		Thread.sleep(50);
		return reader.ready();
	}

	private void readData() {

		try {
			while (readyWithDelay())
				buffer += (char) reader.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String[] readLines(boolean wait) {
		if (wait)
			waitForInput();

		String result = "";
		try {
			while(readyWithDelay()) {
			//while (reader.ready()){
				result += (char) reader.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result.split(NEW_LINE);
	}

	private String parseBestMove() {

		String[] lines = readLines(true);
		for (String line : lines) {
			if (line.startsWith(BEST_MOVE))
				return line;
		}
		return "";
	}

	private void initUCI() {

		sendCommand(UCI);
		String[] lines = readLines(true);
		for (String line : lines)
			if (line.equals(UCI_OK)) {
				uciok = true;
				break;
			}
	}

	private boolean isReady() {
		sendCommand(IS_READY);
		String[] result = readLines(true);
		return (result.length > 0) && (result[0].equals(READY_OK));
	}

	private void quit() {
		sendCommand(QUIT);
	}

	public void findBestMove(String position, String moves) {

		sendCommand(String.format("position %s moves %s", position, moves));
		sendCommand("go infinite");
		buffer = "";
	}

	public String getInfoLine() {

		readData();

		String result = "";
		int idx = buffer.indexOf(NEW_LINE);
		if (idx != -1) {
			result = buffer.substring(0, idx);
			buffer = buffer.substring(idx + NEW_LINE.length());
		}
		return result;
	}

	public String getBestMove() {

		sendCommand(STOP);
		return parseBestMove();
	}

	public void cancelFind() {
		sendCommand(STOP);
		readLines(false);
	}

	public boolean start() {
		boolean result = false;
		try {
			startEngine();
			initUCI();
			result = uciok;
			if (result)
				result = isReady();
			if (!result)
				stop();
		} catch (IOException e) {
			stopEngine();
		}
		return result;
	}

	public void stop() {
		stopEngine();
	}

}
