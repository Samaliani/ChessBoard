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

	static public boolean canMovePiece(Board board, Piece piece) {
		Position pos = piece.getPosition();
		piece.move(new Position());
		boolean isCheck = isCheck(board, piece.getColor());
		piece.move(pos);
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

}
