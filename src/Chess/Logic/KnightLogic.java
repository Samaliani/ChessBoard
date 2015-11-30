package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class KnightLogic extends CommonPieceLogic {

	public KnightLogic(Board board, Piece piece) {
		super(board, piece);
	}

	public boolean validateMove(Position target) {

		if (!validatePosition(target))
			return false;

		return (board.getPiece(target) == null);
	}

	public boolean validateTake(Position target) {

		if (!validatePosition(target))
			return false;

		Piece takenPiece = board.getPiece(target);
		return (takenPiece != null) && (takenPiece.getColor() != piece.getColor());
	}
	
	protected boolean validatePosition(Position target) {

		Position delta = target.minus(piece.getPosition());
		return ((Math.abs(delta.getCol()) == 2) && (Math.abs(delta.getRow()) == 1)) || 
				((Math.abs(delta.getCol()) == 1) && (Math.abs(delta.getRow()) == 2)); 
	}
	
}
