package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class CommonPieceLogic extends PieceLogic {

	public CommonPieceLogic(Board board, Piece piece) {
		super(board, piece);
	}

	public boolean validateMove(Position target) {

		if (!validatePosition(target))
			return false;

		return checkLine(piece.getPosition(), target, false);
	}

	public boolean validateTake(Position target) {

		if (!validatePosition(target))
			return false;

		if (!checkLine(piece.getPosition(), target, true))
			return false;

		return hasEnemyPiece(target);
	}
	
	public Position getTakenPosition(Position target) {
		if(validateTake(target))
			return target;
		else
			return null;
	}
	
	protected boolean validatePosition(Position target) {
		return false;
	}
	
}
