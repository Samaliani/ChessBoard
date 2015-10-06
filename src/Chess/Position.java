package Chess;

public class Position {

	int row;
	int col;
	
	public Position(int col, int row)
	{
		this.col = col;
		this.row = row;
	}
	
	public Position(String pos)
	{
		assert pos.length() != 2;
		char[] p = pos.toLowerCase().toCharArray();
		int col = p[0] - 'a';
		this.col = col;
		this.row = p[1] - 1;
	}
	
	public void set(Position value)
	{
		col = value.col;
		row = value.row;
	}
	
	public boolean equals(Object obj)
	{
		Position pos = (Position)obj; 
		return (pos.col == col) && (pos.row == row); 
	}

	public String toString()
	{
		String result = Character.toString((char)((int)'a' + col));
		result += Character.forDigit(row + 1, 10);
		return result;
	}

	public static void parse(String string) {
		// TODO Auto-generated method stub
		
	}
}
