package Game;

import Chess.Game;
import Chess.Move;
import Chess.Position;
import ictk.boardgame.AmbiguousMoveException;
import ictk.boardgame.IllegalMoveException;
import ictk.boardgame.chess.AmbiguousChessMoveException;
import ictk.boardgame.chess.ChessBoard;
import ictk.boardgame.chess.ChessGame;
import ictk.boardgame.chess.ChessMove;
import ictk.boardgame.chess.ChessResult;
import ictk.boardgame.chess.io.SAN;

public class ictkUtils {

	private static ChessMove copyMove(ChessBoard board, Move move) throws AmbiguousChessMoveException, IllegalMoveException {
		SAN san = new SAN();
		return (ChessMove) san.stringToMove(board, move.toString());
	}
	
	public static String moveToString(ChessMove move) {
		SAN san = new SAN();
		return san.moveToString(move);
	}
	
	public static void addMove(ChessGame game, Move move) {
		try {
			ChessMove chessMove = copyMove((ChessBoard) game.getBoard(), move);
			game.getHistory().add(chessMove, true);
		} catch (IllegalMoveException | AmbiguousMoveException e) {
			e.printStackTrace();
		}
	}

	public static ChessGame copyGame(Game game) {
	
		ChessGame result = new ChessGame();
		for(int i = 0; i < game.getMoveCount(); i++){
			ChessMove chessMove;
			try {
				chessMove = copyMove((ChessBoard) result.getBoard(), game.getMove(i));
				result.getHistory().add(chessMove);
			} catch (IllegalMoveException | IndexOutOfBoundsException | AmbiguousMoveException e) {
				e.printStackTrace();
			}
		}
		
		result.setResult(getGameResult(game));
		return result;
	}
	
	public static String transformNAG(String position, String line){

		Game game = new Game();
		game.loadFEN(position);
		
		String[] moves = line.split(" ");
		for(String move : moves){
			assert move.length() == 4;
			
			Position start = new Position(move.substring(0, 2));
			Position finish = new Position(move.substring(2, 4));
			if (!game.makeMove(start, finish))
				if(!game.makeCastling(start, finish))
					return "";

		}
		
		return game.exportMoveNotation();
	}
	
	public static ChessResult getGameResult(Game game){
		switch(game.getResult()){
		case White:
			return new ChessResult(ChessResult.WHITE_WIN);
		case Black:
			return new ChessResult(ChessResult.BLACK_WIN);
		case Tie:
			return new ChessResult(ChessResult.DRAW);
		default:
			return new ChessResult(ChessResult.UNDECIDED);
		}
	}
	
	

}
