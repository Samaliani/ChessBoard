package Game;

import Chess.Game;
import Chess.GameResult;
import Chess.Move;
import Core.Manager;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.chess.AmbiguousChessMoveException;
import ictk.boardgame.chess.ChessBoard;
import ictk.boardgame.chess.ChessGame;
import ictk.boardgame.chess.ChessMove;
import ictk.boardgame.chess.io.SAN;

public class OpenningModel extends GameModel {

	OpenningBase opennings;
	
	ChessGame currentGame;

	public OpenningModel(Manager manager, String name) {
		super(manager, name);
		loadOpennings();
	}

	private static final String id = "openning";

	@Override
	public String getId() {
		return id;
	}

	public void startGame(Game game) {
		currentGame = new ChessGame();
	}

	@Override
	public void makeMove(Game game) {

		addMove(game);
		// Check move
		if (!opennings.checkGame(currentGame))
			getGameManager().finishGame(GameResult.winColor(game.getTurnColor()));
		else
			super.makeMove(game);
	}

	@Override
	public void endGame(Game game) {
		currentGame = null;
	}

	private void loadOpennings() {
		opennings = new OpenningBase();
	}

	private ChessMove copyMove(ChessBoard board, Move move) throws AmbiguousChessMoveException, IllegalMoveException {
		SAN san = new SAN();
		return (ChessMove)san.stringToMove(board, move.toString());
	}
	
	private void addMove(Game game){
		Move lastMove = game.getMove(game.getMoveCount() - 1);

		try {
			ChessMove move = copyMove((ChessBoard)currentGame.getBoard(), lastMove);
			currentGame.getHistory().add(move);
		} catch (IllegalMoveException | AmbiguousMoveException e) {
			e.printStackTrace();
		}
	}

}
