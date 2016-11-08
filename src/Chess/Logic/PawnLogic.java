package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Position;

public class PawnLogic extends PieceLogic {

	public PawnLogic(Board board, Piece piece) {
		super(board, piece);
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

	@Override
	protected boolean validateMovePosition(Position target){
		
		int direction = getMoveDirection();
		Position delta = target.minus(piece.getPosition());
		if (delta.getCol() != 0)
			return false;

		return (delta.getRow() == direction) || ((delta.getRow() == 2 * direction)
				&& (piece.getPosition().getRow() == getStartRow(piece.getColor())));
	}

	@Override
	protected boolean validateTakePosition(Position target){

		int direction = getMoveDirection();
		Position delta = target.minus(piece.getPosition());
		return ((delta.getRow() == direction) && (Math.abs(delta.getCol()) == 1));
	}

	@Override
	protected boolean hasEnemyPiece(Position target) {

		Position last2Move = board.getLastPawnMove();
		if (last2Move != null) {
			Position delta = target.minus(last2Move);
			if (delta.equals(new Position(0, getMoveDirection())))
				return super.hasEnemyPiece(last2Move);
		}
		
		return super.hasEnemyPiece(target);
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
