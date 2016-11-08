package Chess.Logic;

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

	public boolean isCheck() {
		return (isCheck(Piece.Color.White) || isCheck(Piece.Color.Black));
	}

	public boolean isCheck(Color color) {

		return isCheck(getKing(color));
	}

	public boolean isCheck(Piece king) {

		List<Piece> pieces = getCheckingPieces(king);
		return (pieces.size() != 0);
	}

	public boolean isCheckAt(Piece king, Position target) {

		pushPosition(king);
		Piece piece = board.getPiece(target);
		if (piece != null)
			board.removePiece(piece);
		king.move(target);

		boolean result = isCheck(king);
		popPosition(king);
		if (piece != null)
			board.addPiece(piece);

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

		List<Piece> pieces = board.getPieces(king.getColor().inverse());
		for (Piece piece : pieces) {
			PieceLogic logic = MainLogic.getPieceLogic(board, piece);
			if (logic.validateTake(king.getPosition()))
				result.add(piece);
		}
		return result;
	}

}
