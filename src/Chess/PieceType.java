package Chess;

public enum PieceType {

	Pawn, Knight, Bishop, Rook, Queen, King;
	
	public static PieceType fromChar(char ch)
	{
		switch(Character.toUpperCase(ch))
		{
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
	
	public static String toChar(PieceType pt, boolean usePawnLetter)
	{
		switch(pt)
		{
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
