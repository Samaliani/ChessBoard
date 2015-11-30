package Chess.Logic;

import Chess.Board;
import Chess.Position;

public class BaseLogic {

	Board board;
	
	public BaseLogic(Board board)
	{
		this.board = board;
	}

	protected boolean checkLine(Position p1, Position p2, boolean exclude) {

		Position line = p2.minus(p1);
		int length = getLineLength(line);
		Position step = getLineStep(line);
		if (exclude)
			length--;

		Position p = p1.plus(step);
		for (int i = 0; i < length; i++) {
			if (board.getPiece(p) != null)
				return false;
			p = p.plus(step);
		}
		return true;
	}

	protected int getLineLength(Position line) {
		if (line.getCol() != 0)
			return Math.abs(line.getCol());
		else if (line.getRow() != 0)
			return Math.abs(line.getRow());
		else
			return 1;
	}

	protected Position getLineStep(Position line) {
		int size = getLineLength(line);
		return new Position(line.getCol() / size, line.getRow() / size);
	}
}
