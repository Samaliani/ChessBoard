package Chess.Logic;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Position;

public class MainLogic {

	static public PieceLogic getPieceLogic(Board board, Piece piece) {
		switch (piece.getType()) {
		case Pawn:
			return new PawnLogic(board, piece);
		case Knight:
			return new KnightLogic(board, piece);
		case Bishop:
			return new BishopLogic(board, piece);
		case Rook:
			return new RookLogic(board, piece);
		case Queen:
			return new QueenLogic(board, piece);
		case King:
			return new KingLogic(board, piece);
		default:
			return null;
		}
	}

	static public boolean canMovePiece(Board board, Piece piece, Position target) {
		Position pos = piece.getPosition();

		Piece removedPiece = board.getPiece(target);
		if (removedPiece != null)
			removedPiece.move(new Position());

		piece.move(target);
		boolean isCheck = isCheck(board, piece.getColor());

		piece.move(pos);
		if (removedPiece != null)
			removedPiece.move(target);

		return !isCheck;
	}

	static public boolean isCheck(Board board, Color color) {

		CheckLogic logic = new CheckLogic(board);
		return logic.isCheck(color);
	}

	static public boolean isCheckmate(Board board, Color color) {

		CheckmateLogic logic = new CheckmateLogic(board);
		return logic.isCheckmate(color);
	}
	
	static public boolean isTie(Board board, Color color){
		TieLogic logic = new TieLogic(board);
		return logic.isTie(color);
	}

	static public boolean isPromotion(Board board, Piece piece, Position target) {

		PromotionLogic logic = new PromotionLogic(board, piece);
		return logic.isPromotion(target);
	}
}
