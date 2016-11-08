package Chess.Logic;

import java.util.List;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;

public class TieLogic extends CheckLogic {

	public TieLogic(Board board) {
		super(board);
	}

	public boolean isTie(Color color) {

		List<Piece> pieces = board.getPieces(color);
		for(Piece piece : pieces){
			PieceLogic logic = MainLogic.getPieceLogic(board, piece);
			if (logic.getValidMoves().size() != 0)
				return false;
			if (logic.getValidTakes().size() != 0)
				return false;
		}
		return true;
	}

}
