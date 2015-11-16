package Chess.PGN;

import Chess.Board;
import Chess.Move;

public class PGN {

	Board board;
		
	public PGN(Board board)
	{
		this.board = board;		
	}
	
	public String exportMoves()
	{
		String result = "";
		int moveNo = 1;
		
		for(int i = 0; i < board.getMoveCount(); i++)
		{
			Move move = board.getMove(i);
			if (i % 2 == 0)
				result += String.format("%d.%s", moveNo, move.toString());
			else
			{
				result += String.format(" %s ", move.toString());
				moveNo++;
			}
		}
		return result;
		
	}
}
