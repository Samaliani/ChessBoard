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
	boolean loading;
	List<History> base;

	public OpenningBase(String path) {
		this.path = path;
		loading = false;
	}

	private void loadFromFile(List<History> historyList, File file)
			throws IllegalMoveException, AmbiguousMoveException, InvalidGameFormatException, IOException {

		PGNReader reader = new PGNReader(new FileReader(file));

		Game game = reader.readGame();
		while (game != null) {
			ictk.boardgame.History history = game.getHistory();
			historyList.add(history);
			game = reader.readGame();
		}
		reader.close();
	}

	public void load(List<History> historyList) {
		String[] extensions = { "pgn" };
		File directory = new File(path);
		if (!directory.exists())
			return;

		Iterator<File> files = FileUtils.iterateFiles(directory, extensions, true);
		while (files.hasNext()) {
			try {
				loadFromFile(historyList, files.next());
			} catch (IllegalMoveException | AmbiguousMoveException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadOpenningBase() {

		while (loading)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

		if (base == null) {
			loading = true;
			try {
				List<History> history = new ArrayList<History>();
				load(history);
				base = history;
			} finally {
				loading = false;
			}
		}
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

	private List<Move> extractMoves(Move move) {
		List<Move> result = new ArrayList<Move>();
		result.add(move);
		return result;
	}

	private List<Move> extractMoves(ContinuationList list) {
		List<Move> result = new ArrayList<Move>();
		for (int i = 0; i < list.size(); i++)
			result.add(list.get(i));
		return result;
	}

	private Move getCurrentMove(Move move, Move baseMove) {

		if (move == null)
			return null;

		if (!move.equals(baseMove))
			return null;

		Move nextMove = move.getNext();
		if (nextMove == null)
			return baseMove;

		ContinuationList list = baseMove.getContinuationList();
		for (int i = 0; i < list.size(); i++) {
			Move current = getCurrentMove(nextMove, list.get(i));
			if (current != null)
				return current;
		}
		return null;
	}

	private List<Move> getContinuation(Move move, Move baseMove) {

		if (move == null)
			return extractMoves(baseMove);

		if (!move.equals(baseMove))
			return null;

		ContinuationList result = baseMove.getContinuationList();
		Move nextMove = move.getNext();
		if (move.getHistory().getCurrentMove() == move)
			// if (nextMove == null)
			return extractMoves(result);

		ContinuationList list = result;
		for (int i = 0; i < list.size(); i++) {
			List<Move> currentList = getContinuation(nextMove, list.get(i));
			if ((currentList != null) && (currentList.size() != 0))
				return currentList;
		}
		return null;
	}

	public String getComment(Game game) {

		if (game == null)
			return "";
		if (base == null)
			loadOpenningBase();

		if (base.size() == 0)
			return "";

		for (History history : base) {
			Move currentMove = getCurrentMove(game.getHistory().getFirst(), history.getFirst());
			if (currentMove != null)
				if (currentMove.getAnnotation() != null) {
					String comment = currentMove.getAnnotation().getComment();
					if (comment != "")
						return comment;
				}
		}

		return "";
	}

	public List<Move> getNextMoves(Game game) {

		List<Move> result = null;

		if (game == null)
			return new ArrayList<Move>();
		if (base == null)
			loadOpenningBase();

		if (base.size() == 0)
			return result;

		for (History history : base) {
			ContinuationList firstMoves = history.getFirstAll();
			for (int i = 0; i < firstMoves.size(); i++) {
				List<Move> continuation = getContinuation(game.getHistory().getFirst(), firstMoves.get(i));
				if (continuation != null) {
					if (result == null)
						result = new ArrayList<Move>();
					result.addAll(continuation);
				}
			}
		}
		return result;
	}
}
