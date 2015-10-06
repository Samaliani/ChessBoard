package Logic;

import java.util.List;

import Chess.Move;

public class BaseLogic {

	List<Move> moves;
	
	public BaseLogic(List<Move> moves)
	{
		this.moves = moves;
	}
	
	public boolean isEligible()
	{
		return false;		
	}
	
}
