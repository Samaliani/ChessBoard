package Game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import Chess.Game;
import Chess.PGN.PGN;
import Core.Component;
import Core.Manager;
import Core.SettingSubscriber;

public class GameArchive extends Component implements SettingSubscriber, GameEventListener {

	static final String GameArchiveId = "archive";

	String fileName;

	boolean archive;
	String path;

	public GameArchive(Manager manager) {
		super(manager);
	}

	@Override
	public String getId() {
		return GameArchiveId;
	}

	@Override
	public void beforeGame(Game game) {
	}

	@Override
	public void startGame(Game game) {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		fileName = path + "/" + formatter.format(new Date()) + ".pgn";
	}

	@Override
	public void makeMove(Game game) {
		if (archive&&needStoreGame())
			storeGame(game);
	}

	@Override
	public void endGame(Game game) {
		if (archive&&needStoreGame())
			storeGame(game);
	}

	public boolean needStoreGame() {
		//GameModelManager modelManager = (GameModelManager)getManager().getComponent(GameModelManager.id);
		//return (modelManager.getCurrentModel().getId() == GameModel.id);
		return true;
	}

	public void storeGame(Game game) {

		int n = game.getMoveCount();
		if (n < 2)
			return;

		new File(path).mkdirs();
		try {
			FileWriter writer = new FileWriter(fileName);
			PGN pgn = new PGN(game);
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