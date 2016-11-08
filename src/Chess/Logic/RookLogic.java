package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class RookLogic extends PieceLogic {

	public RookLogic(Board board, Piece piece) {
		super(board, piece);
	}

	protected boolean validatePosition(Position target) {
		return isSameLine(target, piece.getPosition());
	}
}
