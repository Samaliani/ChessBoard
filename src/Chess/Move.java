package Chess;

public class Move {

	MoveType type;
	PieceType pieceType;
	Position position;

	public Move(MoveType type)
	{
		assert (type == MoveType.CastlingK) || (type == MoveType.CastlingQ); 
		this.type = type;
	}
	
	public Move(MoveType type, PieceType pieceType, Position position)
	{
		assert (type == MoveType.Regular) || (type == MoveType.Take); 
		this.type = type;
		this.pieceType = pieceType;
		this.position = position;
	}
	
	public String toString()
	{
		String result = "";
		
		switch(type)
		{
		case Take:
			result = "x";
		case Regular:
			result += PieceType.toChar(pieceType, false);
			result += position.toString();
			break;
		case CastlingK:
			result = "O-O";
			break;
		case CastlingQ:
			result = "O-O-O";
			break;
		}
		
		return result;
	}
	
}
