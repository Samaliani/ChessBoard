package FEN;

import Chess.Board;
import Chess.PieceColor;
import Chess.PieceType;
import Chess.Position;

public class FEN {

	Board board;
	
	
	public FEN(Board board)
	{
		this.board = board;		
	}
	
	private void readBoardLine(int row, String line)
	{
		int col = 0;
		
		char[] chars = line.toCharArray();
		for(int i = 0; col < 8; i++)
		{
			char ch = chars[i];
			
			if (Character.isAlphabetic(ch))
			{
				PieceType pt = PieceType.fromChar(ch);
				PieceColor pc = PieceColor.White;
				if (ch > 'Z')
					pc = PieceColor.Black;
				board.placePiece(pt, pc, new Position(col, row));				
				col++;
			}
			else if (Character.isDigit(ch))
				col += (int)(ch - '0');
			else
				assert false;
		}
		
	}
	
	public void load(String data) throws FENException
	{
		String[] lines = data.split("/");

		// Loading board
		if (lines.length != 8)
			throw new FENException();
		for(int i = 0; i < 8; i++)
		{
			readBoardLine(7 - i, lines[i]);
		}

		// Loading turn and details		
		String[] details = lines[7].split(" ");
		if (details.length != 6)
			throw new FENException();

		// Turn
		if (details[1].length() != 1)
			throw new FENException();
		PieceColor.fromChar(details[1].toCharArray()[0]);
		
		// Castlings
		if (details[2] == "-")
		{
			
		}
		else
		{
			//throw new FENException();
		}
		
		// Pawn for 2
		if (details[3] != "-")
		{
			board.setLastPawnMove(null);
		}
		else
		{
			Position p = new Position(details[3]);
			board.setLastPawnMove(p);
		}

		// Empty moves
		int emptyMoves = Integer.parseInt(details[4]);
		board.setEmptyMoves(emptyMoves);
		
		// Turn number
		int turnNumber = Integer.parseInt(details[5]);
		board.setCurrentMove(turnNumber);
		
	}
	
	public String save()
	{
		/*
		for(int row = 8; row > 0; row--)
			for(int col = 1; col <= 8; col++)
			{
				Position pos = new Position(col, row);
				piece = game.getPiece(position);
				
			}
			*/
		// TODO
		return "";
	}	
}
