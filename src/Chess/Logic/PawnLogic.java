package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Position;

public class PawnLogic extends PieceLogic {

	public PawnLogic(Board board, Piece piece) {
		super(board, piece);
	}

	public boolean validateMove(Position target) {

		int direction = getMoveDirection();
		Position delta = target.minus(piece.getPosition());
		if (delta.getCol() != 0)
			return false;

		if (!checkLine(piece.getPosition(), target, false))
			return false;

		return (delta.getRow() == direction) || ((delta.getRow() == 2 * direction)
				&& (piece.getPosition().getRow() == getStartRow(piece.getColor())));
	}

	public boolean validateTake(Position target) {

		// Validate position
		int direction = getMoveDirection();
		Position delta = target.minus(piece.getPosition());
		if ((delta.getRow() != direction) || (Math.abs(delta.getCol()) != 1))
			return false;

		if (hasEnemyPiece(target))
			return true;

		Position last2Move = board.getLastPawnMove();
		if (last2Move != null) {
			delta = target.minus(last2Move);
			if (delta.equals(new Position(0, direction)))
				return hasEnemyPiece(last2Move);
		}
		return false;
	}

	public Position getTakenPosition(Position target) {

		if (validateTake(target)) {
			if (hasEnemyPiece(target))
				return target;
			else
				return board.getLastPawnMove();
		} else
			return null;
	}

	private int getMoveDirection() {
		if (piece.getColor() == Color.White)
			return 1;
		else
			return -1;
	}

	private int getStartRow(Color color) {
		if (color == Color.White)
			return 1;
		else
			return 6;
	}

}
