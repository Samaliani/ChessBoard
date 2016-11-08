package Chess;

import java.util.ArrayList;
import java.util.List;

import Chess.Logic.CheckLogic;
import Chess.Logic.CheckmateLogic;
import Chess.Logic.MainLogic;
import Chess.Logic.PieceLogic;

public class Move {

	Board before;
	Board actual;

	public enum Type {
		Regular, Take, CastlingK, CastlingQ, Unknown
	}

	Type type;
	Piece.Type pieceType;

	Position startPosition;
	Position position;
	Position ambiguity;
	boolean check;
	boolean checkmate;
	boolean promotion;
	Piece.Type promoteTo;

	public Move(Move.Type type) {
		this.type = type;
	}

	public Move(Board before, Board actual) {
		this.before = before;
		this.actual = actual;
		updateData();
	}

	public String toString() {
		String result = "";

		switch (type) {
		case Take:
			result += pieceType.toChar(false);
			result += getAmbiguity();
			result += "x";
			result += position.toString();
			result += getMovePostfix();
			return result;
		case Regular:
			result += pieceType.toChar(false);
			result += getAmbiguity();
			result += position.toString();
			result += getMovePostfix();
			return result;
		case CastlingK:
			result = "O-O";
			result += getMovePostfix();
			return result;
		case CastlingQ:
			result = "O-O-O";
			result += getMovePostfix();
			return result;
		default:
			return "...";
		}
	}

	public String toString2() {
		return startPosition.toString() + position.toString();
	}

	public Board getBoard() {
		return actual;
	}

	public Board rollback() {
		return before;
	}

	private Board minus(Board board1, Board board2) {

		Board board = new Board(board1);
		for (Piece piece : board2.getPieces()) {
			Piece p = board.getPiece(piece.getPosition());
			if (piece.equals(p))
				board.removePiece(piece);
		}
		return board;
	}

	private void updateData() {

		Board changeAB = minus(actual, before);
		Board changeBA = minus(before, actual);

		Piece.Color activeColor = before.getTurnColor();
		int activeDiff = changeBA.getPieces(activeColor).size();
		int inactiveDiff = changeBA.getPieces(activeColor.inverse()).size();

		if (activeDiff == 1) {
			Piece piece = changeBA.getPieces(activeColor).get(0);
			pieceType = piece.getType();

			Piece actualPiece = changeAB.getPieces(activeColor).get(0);
			promotion = (actualPiece.getType() != pieceType);
			if (promotion)
				promoteTo = actualPiece.getType();

			startPosition = piece.getPosition();
			position = actualPiece.getPosition();
			type = (inactiveDiff == 0) ? Type.Regular : Type.Take;
			ambiguity = calculateAmbiguity();
		} else if (activeDiff == 2) {
			Piece piece = changeBA.getPieces(activeColor, Piece.Type.King).get(0);
			startPosition = piece.getPosition();
			Piece king = changeAB.getPieces(activeColor, Piece.Type.King).get(0);
			position = king.getPosition();
			type = (king.getPosition().getCol() == 6) ? Type.CastlingK : Type.CastlingQ;
		} else {
			type = Type.Unknown;
			return;
		}

		check = (new CheckLogic(actual)).isCheck();
		if (check)
			checkmate = (new CheckmateLogic(actual)).isCheckmate();
		else
			checkmate = false;
	}

	public void setPromotion(Piece.Type promoteTo) {
		promotion = true;
		this.promoteTo = promoteTo;
	}

	private Position calculateAmbiguity() {

		Piece.Color activeColor = before.getTurnColor();

		List<Piece> pieces = before.getPieces(activeColor, pieceType);
		if (pieces.size() == 1)
			return null;

		List<Piece> ambiguityPieces = new ArrayList<Piece>();
		for (Piece p : pieces) {
			if (p.getPosition().equals(startPosition))
				continue;

			PieceLogic logic = MainLogic.getPieceLogic(before, p);
			switch (type) {
			case Regular:
				if (!logic.validateMove(position))
					continue;
				break;
			case Take:
				if (!logic.validateTake(position))
					continue;
				break;
			default:
				continue;
			}
			ambiguityPieces.add(p);
		}

		if (ambiguityPieces.size() == 0)
			return null;

		Position ambiguity = new Position(startPosition.getCol(), -1);
		for (Piece p : ambiguityPieces)
			if (p.getPosition().getCol() == startPosition.getCol())
				ambiguity = new Position(startPosition);

		return ambiguity;
	}

	private String getAmbiguity() {
		if (ambiguity == null)
			return "";

		String result = "";
		if (ambiguity.getRow() != -1)
			result += Character.forDigit(ambiguity.getRow(), 10);
		result = Character.toString((char) ((int) 'a' + ambiguity.getCol())) + result;
		return result;
	}

	private String getMovePostfix() {
		String postfix = "";
		if (promotion)
			postfix = "=" + promoteTo.toChar(false);

		if (checkmate)
			return postfix + "#";
		if (check)
			return postfix + "+";
		return postfix;
	}

}
