package Game;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ictk.boardgame.chess.io.PGNReader;
import ictk.boardgame.io.InvalidGameFormatException;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.ContinuationList;
import ictk.boardgame.Game;
import ictk.boardgame.History;
import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.Move;

public class OpenningBase {

	String path;
	List<History> base;

	public OpenningBase(String path) {
		this.path = path;
	}

	private void loadFromFile(File file) throws IllegalMoveException,
			AmbiguousMoveException, InvalidGameFormatException, IOException {

		PGNReader reader = new PGNReader(new FileReader(file));

		Game game = reader.readGame();
		while (game != null) {
			ictk.boardgame.History history = game.getHistory();
			base.add(history);
			game = reader.readGame();
		}
		reader.close();
	}

	public void load() {
		String[] extensions = { "pgn" };
		File directory = new File(path);
		if(!directory.exists())
			return;
		
		Iterator<File> files = FileUtils.iterateFiles(directory,
				extensions, false);
		while (files.hasNext()) {
			try {
				loadFromFile(files.next());
			} catch (IllegalMoveException | AmbiguousMoveException
					| IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadOpenningBase() {
		base = new ArrayList<History>();
		load();
	}

	public boolean compareMove(Move move, Move baseMove) {

		if (!move.equals(baseMove))
			return false;

		Move nextMove = move.getNext();
		if (nextMove == null)
			return true;

		ContinuationList list = baseMove.getContinuationList();
		// Openning is over
		if (list.size() == 0)
			return true;

		for (int i = 0; i < list.size(); i++)
			if (compareMove(nextMove, list.get(i)))
				return true;
		return false;
	}

	public boolean compareHistory(History history, History baseHistory) {
		Move move = history.getFirst();
		Move baseMove = baseHistory.getFirst();

		return compareMove(move, baseMove);
	}

	public boolean checkGame(Game game) {

		if (game == null)
			return false;
		if (base == null)
			loadOpenningBase();

		if (base.size() == 0)
			return true;
		
		for (History history : base)
			if (compareHistory(game.getHistory(), history))
				return true;
		return false;
	}
}
