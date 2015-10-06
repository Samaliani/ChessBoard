package Chess;

public class Piece {

	PieceType type;
	Position position;
	
	public Piece(PieceType type, Position position)
	{
		this.type = type;
		this.position = position;
	}
	
	public String toString()
	{
		return type.toString() + " " + position.toString();
	}

	public Position getPosition() {
		return position;		
	}

	public PieceType getType() {
		return type;
	}
}

