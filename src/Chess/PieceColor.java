package Chess;

public enum PieceColor {
	White,
	Black;
	
	
	public static PieceColor fromChar(char ch)
	{
		switch(Character.toUpperCase(ch))
		{
		case 'W': 
			return White;
		case 'B':
			return Black;
		default:
			assert false;
			return White;
		}
	}

}
