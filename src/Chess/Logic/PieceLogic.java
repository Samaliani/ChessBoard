package Chess.Logic;

import java.util.ArrayList;
import java.util.List;

import Chess.Board;
import Chess.Piece;
import Chess.Position;

public class PieceLogic extends BaseLogic {

	Piece piece;

	public PieceLogic(Board board, Piece piece) {
		super(board);
		this.piece = piece;
	}

	public boolean validateMove(Position target) {

		if (hasAnyPiece(target))
			return false;

		if (!validateMovePosition(target))
			return false;

		if (!checkLine(piece.getPosition(), target, false))
			return false;

		if (isPinned(target))
			return false;

		return true;
	}

	public boolean validateTake(Position target) {

		if (!hasEnemyPiece(target))
			return false;

		if (hasTeamPiece(target))
			return false;

		if (!validateTakePosition(target))
			return false;

		if (!checkLine(piece.getPosition(), target, true))
			return false;

		if(isPinned(target))
			return false;
		
		return true;
	}

	public List<Position> getValidMoves() {
		List<Position> result = new ArrayList<Position>();
		for (int col = 0; col < 8; col++)
			for (int row = 0; row < 8; row++) {
				Position p = new Position(col, row);
				if (validateMove(p))
					result.add(p);
			}
		return result;
	}

	public List<Position> getValidTakes() {
		List<Position> result = new ArrayList<Position>();
		for (int col = 0; col < 8; col++)
			for (int row = 0; row < 8; row++) {
				Position p = new Position(col, row);
				if (validateTake(p))
					result.add(p);
			}
		return result;
	}

	public Position getTakenPosition(Position target) {
		if(validateTake(target))
			return target;
		else
			return null;
	}
	
	protected boolean validateMovePosition(Position target){
		return validatePosition(target);
	}
	
	protected boolean validateTakePosition(Position target){
		return validatePosition(target);
	}

	protected boolean validatePosition(Position target) {
		return false;
	}

	protected boolean isPinned(Position target) {

		Position pos = piece.getPosition();

		Piece removedPiece = board.getPiece(target);
		if (removedPiece != null)
			removedPiece.move(new Position());

		piece.move(target);

		CheckLogic logic = new CheckLogic(board);
		boolean isCheck = logic.isCheck(piece.getColor());

		piece.move(pos);
		if (removedPiece != null)
			removedPiece.move(target);

		return isCheck;
	}
	
	protected boolean hasEnemyPiece(Position target) {
		Piece targetPiece = board.getPiece(target);
		return (targetPiece != null) && (targetPiece.getColor() != piece.getColor());
	}
	
	protected boolean hasTeamPiece(Position target) {
		Piece targetPiece = board.getPiece(target);
		return (targetPiece != null) && (targetPiece.getColor() == piece.getColor());
	}

	protected boolean hasAnyPiece(Position target) {
		Piece targetPiece = board.getPiece(target);
		return (targetPiece != null);
	}

	protected boolean isSameLine(Position p1, Position p2) {
		Position delta = p2.minus(p1);
		return (delta.getCol() == 0) || (delta.getRow() == 0);
	}

	protected boolean isSameDiagonal(Position p1, Position p2) {
		Position delta = p2.minus(p1);
		return Math.abs(delta.getCol()) == Math.abs(delta.getRow());
	}

}
