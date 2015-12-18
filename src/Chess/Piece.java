package Chess;

public class Piece {

	public enum Color {
		White, Black;

		public static Color fromChar(char ch) {
			switch (Character.toUpperCase(ch)) {
			case 'W':
				return White;
			case 'B':
				return Black;
			default:
				assert false;
				return White;
			}
		}

		public char toChar() {
			switch (this) {
			case White:
				return 'W';
			case Black:
				return 'B';
			default:
				assert false;
				return 'W';
			}
		}

		public Color inverse(){
			if (this== White)
				return Black;
			else
				return White;
		}
	}

	public enum Type {

		Pawn, Knight, Bishop, Rook, Queen, King;

		public static Type fromChar(char ch) {
			switch (Character.toUpperCase(ch)) {
			case 'P':
				return Pawn;
			case 'N':
				return Knight;
			case 'B':
				return Bishop;
			case 'R':
				return Rook;
			case 'Q':
				return Queen;
			case 'K':
				return King;
			default:
				assert false;
				return Pawn;
			}
		}

		public String toChar(boolean usePawnLetter) {
			switch (this) {
			case Pawn:
				if (usePawnLetter)
					return "P";
				else
					return "";
			case Knight:
				return "N";
			case Bishop:
				return "B";
			case Rook:
				return "R";
			case Queen:
				return "Q";
			case King:
				return "K";
			default:
				return "";
			}
		}
	}

	Type type;
	Color color;
	Position position;

	public Piece(Type type, Color color) {
		this.type = type;
		this.color = color;
		position = new Position(-1, -1);
	}

	public Piece(Type type, Color color, Position position) {
		this.type = type;
		this.color = color;
		this.position = position;
	}

	public String toString() {
		return String.format("%s %s %s", color.toString(), type.toString(), position.toString());
	}

	public void move(Position newPos) {
		position = newPos;
	}

	public Position getPosition() {
		return position;
	}

	public Color getColor() {
		return color;
	}

	public Type getType() {
		return type;
	}
}
