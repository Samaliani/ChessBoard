package Logic;

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
		return false;
	}

	public boolean validateTake(Position target) {
		return false;
	}

	public List<Position> getValidMoves() {
		List<Position> result = new ArrayList<Position>();
		for (int col = 0; col < 8; col++)
			for (int row = 0; row < 8; row++) {
				Position p = new Position(col, row);
				if (p.equals(piece.getPosition()))
					continue;
				if (validateMove(p))
					result.add(p);
			}
		return result;
	}

	public Position getTakenPosition(Position target) {
		return null;
	}

	protected boolean hasEnemyPiece(Position target) {
		Piece targetPiece = board.getPiece(target);
		return (targetPiece != null) && (targetPiece.getColor() != piece.getColor());
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
