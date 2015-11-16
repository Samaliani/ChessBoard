package Logic;

import java.util.ArrayList;
import java.util.List;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Piece.Type;
import Chess.Position;

public class CheckLogic extends BaseLogic {

	public CheckLogic(Board board) {
		super(board);
	}

	public boolean isCheck(Color color) {

		return isCheck(getKing(color));
	}

	public boolean isCheck(Piece king) {

		List<Piece> pieces = getCheckingPieces(king);
		return (pieces.size() != 0);
	}

	public boolean isCheckAt(Piece king, Position target) {

		Position position = king.getPosition();
		Piece piece = board.getPiece(target);
		if (piece != null)
			piece.move(new Position());
		king.move(target);

		boolean result = isCheck(king);
		king.move(position);
		if (piece != null)
			piece.move(target);

		return result;
	}

	protected Piece getKing(Color color) {
		List<Piece> pieces = board.getPieces(color, Type.King);
		// Only one King
		assert pieces.size() == 1;
		Piece king = pieces.get(0);
		return king;
	}

	protected List<Piece> getCheckingPieces(Piece king) {
		List<Piece> result = new ArrayList<Piece>();

		List<Piece> pieces = board.getPieces(Color.not(king.getColor()));
		for (Piece piece : pieces) {
			PieceLogic logic = MainLogic.getPieceLogic(board, piece);
			if (logic.validateTake(king.getPosition()))
				result.add(piece);
		}
		return result;
	}

}
