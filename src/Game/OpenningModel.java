package Game;

import java.util.List;
import java.util.Properties;

import App.ChessBoardManager;
import Chess.Game;
import Chess.GameResult;
import Chess.Move;
import Chess.Piece;
import Chess.Position;
import Core.Manager;
import GUI.ChessBoardPanel;
import LowLevel.BoardData;
import LowLevel.Utils;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.chess.AmbiguousChessMoveException;
import ictk.boardgame.chess.ChessBoard;
import ictk.boardgame.chess.ChessGame;
import ictk.boardgame.chess.ChessMove;
import ictk.boardgame.chess.io.SAN;

public class OpenningModel extends GameModel {

	public enum Mode {
		Normal, WhiteTrainer, BlackTrainer;
	}
	
	Mode mode;
	String path;
	OpenningBase opennings;

	ChessGame currentGame;
	OpenningBoardExtender boardExtender;

	public OpenningModel(Manager manager, String name) {
		super(manager, name);
	}

	private static final String id = "openning";

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void selected() {

		Thread loadThread = new Thread() {
			public void run() {
				opennings.loadOpenningBase();
			}
		};
		loadThread.start();

		if (boardExtender == null)
			boardExtender = new OpenningBoardExtender();
		getBoardPanel().addExtender(boardExtender);
	}

	@Override
	public void unselected() {
		
		getBoardPanel().removeExtender(boardExtender);
	}
	
	public void setMode(Mode mode){
		this.mode = mode;
	}

	@Override
	public void loadSettings(Properties preferences) {
		opennings = new OpenningBase(preferences.getProperty("Openning.Path", ".\\data\\opennings\\"));
		mode = Mode.valueOf(preferences.getProperty("Openning.Mode", Mode.Normal.toString()));
	}
	
	@Override
	public void saveSettings(Properties preferences) {
		preferences.setProperty("Openning.Mode", mode.toString());
	}

	@Override
	public void startGame(Game game) {
		currentGame = new ChessGame();

		processMode(game);
	}

	@Override
	public void makeMove(Game game) {

		addMove(game);
		// Check move
		if (!opennings.checkGame(currentGame))
			getGameManager().finishGame(GameResult.winColor(game.getTurnColor()));
		else
			super.makeMove(game);

		processMode(game);
	}

	@Override
	public void endGame(Game game) {
		currentGame = null;
	}

	private ChessBoardPanel getBoardPanel() {
		return ((ChessBoardManager) manager).getFrame().boardPanel;
	}

	private void processMode(Game game){

		boardExtender.setData(null);
		switch(mode){
		case WhiteTrainer:
			if (game.getBoard().getTurnColor() == Piece.Color.White) {
				BoardData data = getNextMoves();
				boardExtender.setData(data);
			}
			break;
		case BlackTrainer:
			if (game.getBoard().getTurnColor() == Piece.Color.Black) {
				BoardData data = getNextMoves();
				boardExtender.setData(data);
			}
			break;
		default:
		}
	}
	
	private BoardData getNextMoves() {

		List<ictk.boardgame.Move> continuation = opennings.getNextMoves(currentGame);
		
		BoardData data = new BoardData();
		for (ictk.boardgame.Move move : continuation) {
			ChessMove chessMove = (ChessMove) move;
			data.plus(Utils.getBoardData(
					new Position(chessMove.getDestination().getFile() - 1, chessMove.getDestination().getRank() - 1)));
		}
		return data;
	}

	private ChessMove copyMove(ChessBoard board, Move move) throws AmbiguousChessMoveException, IllegalMoveException {
		SAN san = new SAN();
		return (ChessMove) san.stringToMove(board, move.toString());
	}

	private void addMove(Game game) {
		Move lastMove = game.getMove(game.getMoveCount() - 1);

		try {
			ChessMove move = copyMove((ChessBoard) currentGame.getBoard(), lastMove);
			currentGame.getHistory().add(move);
		} catch (IllegalMoveException | AmbiguousMoveException e) {
			e.printStackTrace();
		}
	}

}
