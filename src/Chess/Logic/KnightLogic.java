package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class KnightLogic extends PieceLogic {

	public KnightLogic(Board board, Piece piece) {
		super(board, piece);
	}

	protected boolean validatePosition(Position target) {

		Position delta = target.minus(piece.getPosition());
		return ((Math.abs(delta.getCol()) == 2) && (Math.abs(delta.getRow()) == 1)) || 
				((Math.abs(delta.getCol()) == 1) && (Math.abs(delta.getRow()) == 2)); 
	}

	protected boolean checkLine(Position p1, Position p2, boolean exclude) {
		return true;
	}

}
