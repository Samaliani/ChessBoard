package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class QueenLogic extends CommonPieceLogic {

	public QueenLogic(Board board, Piece piece) {
		super(board, piece);
	}

	protected boolean validatePosition(Position target) {
		return isSameLine(target, piece.getPosition()) || isSameDiagonal(target, piece.getPosition());
	}
	
}
