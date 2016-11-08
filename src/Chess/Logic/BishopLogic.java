package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class BishopLogic extends PieceLogic {

	public BishopLogic(Board board, Piece piece) {
		super(board, piece);
	}

	protected boolean validatePosition(Position target) {
		return isSameDiagonal(target, piece.getPosition());
	}
}
