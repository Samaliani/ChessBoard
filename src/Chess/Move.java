package Chess;

public class Move {

	public enum Type {
		Regular, Take, CastlingK, CastlingQ, Unknown, Invalid
	}
	
	Type type;
	Piece.Type pieceType;
	Position position;
	Position ambiguity;
	boolean check;
	boolean checkmate;

	public Move(Type type) {
		assert(type == Type.CastlingK) || (type == Type.CastlingQ);
		this.type = type;
	}
	
	public Move(Type type, Piece.Type pieceType, Position position) {
		assert(type == Type.Regular) || (type == Type.Take);
		this.type = type;
		this.pieceType = pieceType;
		this.position = position;
	}

	public Move(Type type, Piece.Type pieceType, Position position, Position ambiguity) {
		assert(type == Type.Regular) || (type == Type.Take);
		this.type = type;
		this.pieceType = pieceType;
		this.position = position;
		this.ambiguity = ambiguity;
	}

	public String toString() {
		String result = "";

		switch (type) {
		case Take:
			result += Piece.Type.toChar(pieceType, false);
			result += getAmbiguity();
			result += "x";
			result += position.toString();
			result += getMovePostfix();
			return result;
		case Regular:
			result += Piece.Type.toChar(pieceType, false);
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

	private String getAmbiguity() {
		if (ambiguity == null)
			return "";

		String result = "";
		if (ambiguity.getCol() == position.getCol())
			result += Character.forDigit(ambiguity.getRow() + 1, 10);
		result = Character.toString((char) ((int) 'a' + ambiguity.getCol())) + result;
		return result;
	}

	private String getMovePostfix() {
		if (checkmate)
			return "#";
		if (check)
			return "+";
		return "";
	}

}
