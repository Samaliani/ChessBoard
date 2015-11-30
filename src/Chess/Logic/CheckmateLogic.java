package Chess.Logic;

import java.util.List;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Piece.Type;
import Chess.Position;

public class CheckmateLogic extends CheckLogic {

	public CheckmateLogic(Board board) {
		super(board);
	}

	public boolean isCheckmate(Color color) {
		List<Piece> pieces = board.getPieces(color, Type.King);
		// Only one King
		Piece king = pieces.get(0);
		assert king.getType() == Type.King;

		return isCheckmate(king);
	}

	public boolean isCheckmate(Piece king) {

		List<Piece> checkingPieces = getCheckingPieces(king);

		if (checkingPieces.size() == 0)
			return false;

		if (checkingPieces.size() == 1) {
			Piece checkPiece = checkingPieces.get(0);
			if (canTake(king.getColor(), checkPiece.getPosition()))
				return false;

			if (canBarrier(king, checkPiece))
				return false;
		}
		return (!canLeave(king));
	}

	private boolean canTake(Color color, Position target) {
		List<Piece> pieces = board.getPieces(color);
		for (Piece piece : pieces) {
			PieceLogic logic = MainLogic.getPieceLogic(board, piece);
			if (logic.validateTake(target))
				return true;
		}
		return false;
	}

	private boolean canMove(Color color, Position target) {
		List<Piece> pieces = board.getPieces(color);
		for (Piece piece : pieces) {
			PieceLogic logic = MainLogic.getPieceLogic(board, piece);
			if (logic.validateMove(target))
				return true;
		}
		return false;
	}
	
	private boolean canLeave(Piece king){
		
		PieceLogic logic = MainLogic.getPieceLogic(board, king);
		List<Position> moves = logic.getValidMoves();
		return moves.size() != 0;
	}

	private boolean canBarrier(Piece king, Piece checkPiece) {
		Position piecePos = checkPiece.getPosition();
		Position kingPos = king.getPosition();

		Position line = kingPos.minus(piecePos);
		int length = getLineLength(line);
		Position step = getLineStep(line);

		Position p = piecePos.plus(step);
		for (int i = 1; i < length; i++) {
			if (canMove(king.getColor(), p))
				return true;
			p = p.plus(step);
		}
		return false;
	}

}
