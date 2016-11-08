package Chess.FEN;

import Chess.Board;
import Chess.Move;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Piece.Type;
import Chess.Position;

public class FEN {

	Board board;

	public FEN(Board board) {
		this.board = board;
	}

	private void readBoardLine(int row, String line) {
		int col = 0;

		char[] chars = line.toCharArray();
		for (int i = 0; col < 8; i++) {
			char ch = chars[i];

			if (Character.isAlphabetic(ch)) {
				Type pt = Type.fromChar(ch);
				Color pc = Color.White;
				if (ch > 'Z')
					pc = Color.Black;
				board.placePiece(pt, pc, new Position(col, row));
				col++;
			} else if (Character.isDigit(ch))
				col += (int) (ch - '0');
			else
				assert false;
		}

	}

	private String writeBoardLine(int row) {

		String result = "";
		int emptyCells = 0;
		for (int col = 0; col < 8; col++) {
			Position pos = new Position(col, row);
			Piece piece = board.getPiece(pos);
			if (piece == null) {
				emptyCells++;
				continue;
			}
			if (emptyCells != 0) {
				result += Integer.toString(emptyCells);
				emptyCells = 0;
			}

			char c = piece.getType().toChar(true).charAt(0);
			if (piece.getColor() == Color.Black)
				c = Character.toLowerCase(c);
			result += c;
		}
		if (emptyCells != 0)
			result += Integer.toString(emptyCells);
		return result;
	}

	public void load(String data) throws FENException {
		String[] lines = data.split("/");

		// Loading board
		if (lines.length != 8)
			throw new FENException();
		for (int i = 0; i < 8; i++) {
			readBoardLine(7 - i, lines[i]);
		}

		// Loading turn and details
		String[] details = lines[7].split(" ");
		/*
		 * if (details.length != 6) throw new FENException();
		 */

		// Turn
		if (details[1].length() != 1)
			throw new FENException();
		Color color = Color.fromChar(details[1].toCharArray()[0]);
		board.setTurnColor(color);

		// Castlings
		String castlings = details[2];
		board.setCastlingAvailable(Color.White, Move.Type.CastlingK, castlings.contains("K"));
		board.setCastlingAvailable(Color.White, Move.Type.CastlingQ, castlings.contains("Q"));
		board.setCastlingAvailable(Color.Black, Move.Type.CastlingK, castlings.contains("k"));
		board.setCastlingAvailable(Color.Black, Move.Type.CastlingQ, castlings.contains("q"));

		// Pawn for 2
		if (details[3] != "-") {
			board.setLastPawnMove(null);
		} else {
			Position p = new Position(details[3]);
			board.setLastPawnMove(p);
		}

		// Empty moves
		if (details.length > 4) {
			int emptyMoves = Integer.parseInt(details[4]);
			board.setEmptyMoves(emptyMoves);
		}

		// Turn number
		if (details.length > 5) {
			int turnNumber = Integer.parseInt(details[5]);
			board.setCurrentMove(turnNumber);
		}

	}

	public String save() {

		// Board
		String result = writeBoardLine(7);
		for (int row = 6; row >= 0; row--) {
			result += "/" + writeBoardLine(row);
		}

		// Turn
		result += " ";
		result += Character.toLowerCase(board.getTurnColor().toChar());

		// Castlings
		result += " ";
		String castlings = "";
		if (board.getCastlingAvailable(Color.White, Move.Type.CastlingK))
			castlings += "K";
		if (board.getCastlingAvailable(Color.White, Move.Type.CastlingQ))
			castlings += "Q";
		if (board.getCastlingAvailable(Color.Black, Move.Type.CastlingK))
			castlings += "k";
		if (board.getCastlingAvailable(Color.Black, Move.Type.CastlingQ))
			castlings += "q";
		if (castlings.length() != 0)
			result += castlings;
		else
			result += "-";

		// Pawn for 2
		result += " ";
		Position pawn2 = board.getLastPawnMove();
		if (pawn2 == null)
			result += "-";
		else
			result += pawn2.toString();

		// Empty moves
		result += " ";
		result += Integer.toString(board.getEmptyMoves());

		// Turn number
		result += " ";
		result += Integer.toString(board.getCurrentMove());

		return result;
	}
}
