package Chess.PGN;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Chess.Board;
import Chess.Game;
import Chess.Move;
import Chess.Piece;

public class PGN {

	Game game;
	String event;
	String site;
	Date date;
	String round;
	String white;
	String black;
	String result;
	
	public PGN(Game game) {
		this.game = game;
		this.date = new Date();
	}
	
	public String toString()
	{
		String value = "";
		if (event.length() != 0)
			value += String.format("[Event \"%s\"]", event);
		if (site.length() != 0)
			value += String.format("[Site \"%s\"]", site);
		if (round.length() != 0)
			value += String.format("[Round \"%s\"]", round);
		if (white.length() != 0)
			value += String.format("[White \"%s\"]", white);
		if (black.length() != 0)
			value += String.format("[Black \"%s\"]", black);
		if (result.length() != 0)
			value += String.format("[Result \"%s\"]", result);
		
		value += "/r/n";
		value += exportMoves();
		return value;		
	}
	
	public String exportMoves() {
		
		Board startPos = game.getStartPosition();

		List<Move> moves = new ArrayList<Move>();
		if (startPos.getTurnColor() == Piece.Color.Black)
			moves.add(new Move(Move.Type.Unknown));
		for(int i = 0; i < game.getMoveCount(); i++)
			moves.add(game.getMove(i));

		
		String result = "";
		int moveNo = startPos.getCurrentMove();
		for (int i = 0; i < moves.size(); i++) {
			Move move = moves.get(i);
			if (i % 2 == 0)
				result += String.format("%d.%s ", moveNo, move.toString());
			else {
				result += String.format("%s ", move.toString());
				moveNo++;
			}
		}
		
		result += game.getResult().toString(); 
		return result;
	}
	
	public String exportMovesLine() {
		String result = "";
		int moveNo = 1;

		for (int i = 0; i < game.getMoveCount(); i++) {
			Move move = game.getMove(i);
			if (i % 2 == 0)
				result += String.format("%d.%s ", moveNo, move.toString());
			else {
				result += String.format("%s ", move.toString());
				result += "\r\n";
				moveNo++;
			}
		}
		result += game.getResult().toString(); 
		return result;
	}
	
}
