package Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Type;
import Chess.Position;

public class KingLogic extends CommonPieceLogic {

	public KingLogic(Board board, Piece piece) {
		super(board, piece);
		assert piece.getType() == Type.King;
	}

	public boolean validateMove(Position target) {

		if (!super.validateMove(target))
			return false;

		CheckLogic logic = new CheckLogic(board);
		return (!logic.isCheckAt(piece, target));
	}

	public boolean validateTake(Position target) {

		if (!super.validateTake(target))
			return false;

		CheckLogic logic = new CheckLogic(board);
		return (!logic.isCheckAt(piece, target));
	}

	protected boolean validatePosition(Position target) {

		Position delta = target.minus(piece.getPosition());
		return (Math.abs(delta.getCol()) < 2) && (Math.abs(delta.getRow()) < 2);
	}

}
