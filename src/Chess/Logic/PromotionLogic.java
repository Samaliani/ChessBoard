package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Position;

public class PromotionLogic extends BaseLogic {

	Piece piece;

	public PromotionLogic(Board board, Piece piece) {
		super(board);
		this.piece = piece;
	}

	public boolean isPromotion(Position target) {
		if (piece.getType() != Piece.Type.Pawn)
			return false;
		return (target.getRow() == getPromotionRank());
	}

	private int getPromotionRank() {
		if (piece.getColor() == Color.White)
			return 7;
		else
			return 0;
	}

}
