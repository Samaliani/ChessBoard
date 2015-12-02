package App;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import Chess.Board;
import Chess.BoardEventListener;
import Chess.PGN.PGN;
import Core.Component;
import Core.Manager;
import Core.SettingSubscriber;

public class GameArchive extends Component implements SettingSubscriber, GameEventListener {

	static final String GameArchiveId = "archive";

	Board board;
	String fileName;

	boolean archive;
	String path;

	public GameArchive(Manager manager, Board board) {
		super(manager);
		this.board = board;
	}

	@Override
	public String getId() {
		return GameArchiveId;
	}

	@Override
	public void appStart() {

		gameReset();
		// TODO save archive
		if (archive)
			board.addBoardEventListener(new BoardEventListener() {
				public void boardMove() {
					storeGame();
				}
			});
	}

	@Override
	public void gameReset() {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		fileName = path + "/" + formatter.format(new Date()) + ".pgn";
	}

	public void storeGame() {

		int n = board.getMoveCount();
		if (n < 2)
			return;

		new File(path).mkdirs();
		try {
			FileWriter writer = new FileWriter(fileName);
			PGN pgn = new PGN(board);
			writer.write(pgn.exportMovesLine());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadSettings(Properties preferences) {
		archive = Boolean.parseBoolean(preferences.getProperty("Archive.Store", "false"));
		path = preferences.getProperty("Archive.Path", System.getProperty("user.dir") + "/archive");

	}

	@Override
	public void saveSettings(Properties preferences) {
	}
}