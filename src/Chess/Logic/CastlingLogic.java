package Chess.Logic;

import java.util.List;

import Chess.Board;
import Chess.Move;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Piece.Type;
import Chess.Position;

public class CastlingLogic extends BaseLogic {

	public CastlingLogic(Board board) {
		super(board);
	}

	public boolean validate(Move.Type castling) {

		// Castling prohibited
		if (!board.getCastlingAvailable(board.getTurnColor(), castling))
			return false;

		// Castling under check
		if (MainLogic.isCheck(board, board.getTurnColor()))
			return false;

		Position king = new Position(4, getKingRank());
		Position newKing;
		Position rook;
		if (castling == Move.Type.CastlingK) {
			rook = new Position(7, getKingRank());
			newKing = king.plus(new Position(2, 0));
		} else {
			rook = new Position(0, getKingRank());
			newKing = king.minus(new Position(2, 0));
		}

		// Line is empty for castling
		if (!checkLine(king, rook, true))
			return false;

		// Checks on king moves
		return checkLineForChecks(king, newKing);
	}

	private int getKingRank() {
		if (board.getTurnColor() == Color.White)
			return 0;
		else
			return 7;
	}

	private boolean checkLineForChecks(Position p1, Position p2) {
		List<Piece> pieces = board.getPieces(board.getTurnColor(), Type.King);
		// Only one King
		Piece king = pieces.get(0);

		Position line = p2.minus(p1);
		int length = getLineLength(line);
		Position step = getLineStep(line);

		CheckLogic logic = new CheckLogic(board);
		
		Position p = p1.plus(step);
		for (int i = 0; i < length; i++) {
			king.move(p);
			if (logic.isCheckAt(king, p)) 
				return false;
			p = p.plus(step);
		}
		
		return true;
	}
}
