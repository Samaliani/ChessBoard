package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import App.ChessBoardManager;
import Chess.Game;
import Chess.GameResult;
import Chess.Move;
import Chess.Piece;
import Chess.Position;
import Comment.CommentManager;
import Communication.OutEvent;
import Core.Manager;
import GUI.ChessBoardPanel;
import LowLevel.BoardData;
import LowLevel.Utils;
import ictk.boardgame.chess.ChessGame;
import ictk.boardgame.chess.ChessMove;

public class OpenningModel extends GameModel {

	public enum Mode {
		Normal(0), ForWhite(1), ForBlack(2), ForAll(3);
		private final int value;

		private Mode(final int value) {
			this.value = value;
		}

		public static Mode valueOf(int value) {
			for (Mode mode : Mode.values()) {
				if (mode.getValue() == value)
					return mode;
			}
			return Normal;
		}

		public int getValue() {
			return value;
		}
	}

	Mode mode;
	int hintModeStart;
	boolean readComments;
	boolean finishOnBadMove;
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

	public int getModeCount() {
		return 4;
	}

	public String getModeName(int i) {
		switch (i) {
		case (0):
			return App.Messages.Game.OpenningModeNormal;
		case (1):
			return App.Messages.Game.OpenningModeHintForWhite;
		case (2):
			return App.Messages.Game.OpenningModeHintForBlack;
		case (3):
			return App.Messages.Game.OpenningModeHintForAll;
		default:
			return "";
		}
	}

	public int getActiveMode() {
		return mode.getValue();
	}

	public void setActiveMode(int value) {
		mode = Mode.valueOf(value);
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

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
	public void loadSettings(Properties preferences) {
		opennings = new OpenningBase(preferences.getProperty("Openning.Path", ".\\opennings\\"));
		mode = Mode.valueOf(
				Integer.parseInt(preferences.getProperty("Openning.Mode", Integer.toString(Mode.Normal.getValue()))));
		hintModeStart = Integer.parseInt(preferences.getProperty("Openning.HintModeStart", "5"));
		readComments = Boolean.parseBoolean(preferences.getProperty("Openning.ReadComments", "false"));
		finishOnBadMove = Boolean.parseBoolean(preferences.getProperty("Openning.FinishOnMiss", "true"));
	}

	@Override
	public void saveSettings(Properties preferences) {
		preferences.setProperty("Openning.Mode", Integer.toString(mode.getValue()));
	}

	@Override
	public void startGame(Game game) {
		currentGame = new ChessGame();

		processMode(game);
	}

	@Override
	public void makeMove(Game game) {

		super.makeMove(game);

		addMove(game);
		// Check move
		if (!opennings.checkGame(currentGame)) {
			if (finishOnBadMove)
				getGameManager().finishGame(GameResult.winColor(game.getTurnColor()));
			else
				getCommunicationManager().sendEvent(new OutEvent(OutEvent.Type.SadSignal));
		} else
			super.makeMove(game);

		processMode(game);
	}

	@Override
	public void rollbackMove(Game game) {

		super.rollbackMove(game);
		removeMove(game);
		processMode(game);
	}

	@Override
	public void endGame(Game game) {
		// currentGame = null;
	}

	private ChessBoardPanel getBoardPanel() {
		return ((ChessBoardManager) manager).getFrame().boardPanel;
	}

	private void processMode(Game game) {

		boardExtender.setData(null);
		if (mode == Mode.Normal)
			return;

		if (game.getBoard().getCurrentMove() < hintModeStart)
			return;

		String comments = getComment();
		if (comments.length() > 0)
			comments += System.lineSeparator();
		
		BoardData data = getNextMoves();
		List<String> options = getNextMovesText();
		for (int i = 0; i < options.size(); i++)
			comments += Integer.toString(i + 1) + " - " + options.get(i) + System.lineSeparator();

		switch (mode) {
		case ForWhite:
			if (game.getBoard().getTurnColor() != Piece.Color.White)
				return;
		case ForBlack:
			if (game.getBoard().getTurnColor() != Piece.Color.Black)
				return;
		default:
		}

		boardExtender.setData(data);
		getCommentManager().addComment(comments);
	}

	private CommentManager getCommentManager() {
		return (CommentManager) manager.getComponent(CommentManager.CommentManagerId);
	}

	private String getComment() {
		if (readComments)
			return opennings.getComment(currentGame);
		else
			return "";
	}

	private BoardData getNextMoves() {

		List<ictk.boardgame.Move> continuation = opennings.getNextMoves(currentGame);

		BoardData data = new BoardData();

		if (continuation == null)
			return data;

		for (ictk.boardgame.Move move : continuation) {
			ChessMove chessMove = (ChessMove) move;
			data.plus(Utils.getBoardData(
					new Position(chessMove.getDestination().getFile() - 1, chessMove.getDestination().getRank() - 1)));
		}
		return data;
	}

	private List<String> getNextMovesText() {

		List<String> result = new ArrayList<String>();

		List<ictk.boardgame.Move> continuation = opennings.getNextMoves(currentGame);
		if (continuation == null)
			return result;

		for (ictk.boardgame.Move move : continuation) {
			ChessMove chessMove = (ChessMove) move;

			String moveText = ictkUtils.moveToString(chessMove);
			if (!result.contains(moveText))
				result.add(moveText);
		}
		return result;
	}

	private void addMove(Game game) {
		Move lastMove = game.getMove(game.getMoveCount() - 1);
		ictkUtils.addMove(currentGame, lastMove);
	}

	private void removeMove(Game game) {
		currentGame.getHistory().prev();
		//currentGame.getHistory().getContinuationList().removeAll();
	}

}
