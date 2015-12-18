package Chess;

public enum GameResult {
	Unknown, White, Black, Tie;
	
	public static GameResult winColor(Piece.Color color){
		if (color == Piece.Color.White)
			return White;
		else
			return Black;
	}

	public String toString() {
		switch (this) {
		case White:
			return "1-0";
		case Black:
			return "0-1";
		case Tie:
			return "1/2-1/2";
		default:
			return "";
		}
	}
}
