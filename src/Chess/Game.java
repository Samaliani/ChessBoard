package Chess;

import java.util.ArrayList;
import java.util.List;

import Chess.FEN.FEN;
import Chess.Logic.CastlingLogic;
import Chess.Logic.MainLogic;
import Chess.Logic.PieceLogic;
import Chess.PGN.PGN;
import Chess.Piece.Color;

public class Game {

	Board startPosition;
	Board board;
	List<Move> moves;
	GameResult result;
	int moveCount;

	public Game() {
		board = new Board();
		moves = new ArrayList<Move>();
		loadFEN(Board.StartFEN);
	}

	public Board getStartPosition() {
		return startPosition;
	}
	
	public Board getBoard() {
		return board;
	}

	public Color getTurnColor() {
		return board.getTurnColor();
	}

	public void loadFEN(String fen) {
		moveCount = 0;
		moves.clear();
		result = GameResult.Unknown;
		board.loadFEN(fen);
		startPosition = new Board(board);
	}

	public String saveFEN() {
		FEN fen = new FEN(board);
		return fen.save();
	}

	// --------------------------------------
	// Result
	// --------------------------------------
	public void setResult(GameResult result) {
		this.result = result;
	}

	public GameResult getResult() {
		return result;
	}

	// --------------------------------------
	// Moves
	// --------------------------------------

	public int getMoveCount() {
		return moveCount;
	}

	public Move getMove(int index) {
		return moves.get(index);
	}

	private void addMove(Move move) {

		while (moves.size() > moveCount) {
			moves.remove(moveCount);
		}

		moves.add(move);
		moveCount++;
	}

	private void pushMove() {

		Move move = moves.get(moveCount);
		board.apply(move.getBoard());
		moveCount++;

	}

	private void popMove() {

		moveCount--;
		Move move = moves.get(moveCount);
		board.apply(move.rollback());
	}

	public boolean commitMove() {

		if (moveCount == moves.size())
			return false;
		pushMove();
		return true;
	}
 
	public boolean rollbackMove() {

		if (moveCount == 0)
			return false;
		result = GameResult.Unknown;
		popMove();
		return true;
	}

	public boolean makeMove(Position start, Position finish) {

		Piece piece = board.getPiece(start);
		if (piece.getColor() != getTurnColor())
			return false;

		Move.Type type;
		Position position = finish;

		PieceLogic logic = MainLogic.getPieceLogic(board, piece);
		if (logic.validateTake(position))
			type = Move.Type.Take;
		else if (logic.validateMove(position))
			type = Move.Type.Regular;
		else
			return false;

		Board before = new Board(board);

		if (type == Move.Type.Take)
			board.removePieceAt(logic.getTakenPosition(position));
		piece.move(position);

		if (MainLogic.isPromotion(board, piece, position)) {
			Piece.Type promoteTo = Piece.Type.Queen;
			// TODO Add event about promotion
			board.removePiece(piece);
			Piece promotedPiece = new Piece(promoteTo, piece.getColor(), position);
			board.addPiece(promotedPiece);
		}

		Move move = new Move(before, new Board(board));
		addMove(move);

		processBoard(move, piece, position);

		return true;
	}

	public boolean makeCastling(Position start, Position finish) {

		Piece king = board.getPiece(start);
		Position position = finish;

		Move.Type type = getCastlingType(king, position);

		CastlingLogic cv = new CastlingLogic(board);
		if (!cv.validate(type))
			return false;

		Board before = new Board(board);

		king.move(position);
		Piece rook;
		switch (type) {
		case CastlingQ:
			rook = board.getPiece(new Position(0, position.getRow()));
			rook.move(position.plus(new Position(1, 0)));
			break;
		case CastlingK:
			rook = board.getPiece(new Position(7, position.getRow()));
			rook.move(position.minus(new Position(1, 0)));
			break;
		default:
			return false;
		}

		Move move = new Move(before, new Board(board));
		addMove(move);

		processBoard(move, king, position);
		
		return true;
	}

	private void processBoard(Move move, Piece piece, Position position) {

		updateCastlings(piece, move);
		updateEmptyMove(piece, move);
		updateLastPawnMove(piece, move);

		if (getTurnColor() == Color.Black)
			board.currentMove++;
		board.turnColor = board.turnColor.inverse();
	}

	private void updateCastlings(Piece piece, Move move) {

		switch (piece.getType()) {
		case King:
			board.setCastlingAvailable(getTurnColor(), Move.Type.CastlingK, false);
			board.setCastlingAvailable(getTurnColor(), Move.Type.CastlingQ, false);
			break;
		case Rook:
			if (piece.getPosition().getCol() == 7)
				board.setCastlingAvailable(getTurnColor(), Move.Type.CastlingK, false);
			if (piece.getPosition().getCol() == 0)
				board.setCastlingAvailable(getTurnColor(), Move.Type.CastlingQ, false);
			break;
		default:
			// Nothing
		}
	}

	private void updateEmptyMove(Piece piece, Move move) {
		if ((piece.getType() != Piece.Type.Pawn) && (move.type == Move.Type.Regular))
			board.emptyMoves++;
		else
			board.emptyMoves = 0;
	}

	private void updateLastPawnMove(Piece piece, Move move) {
		board.lastPawn2Move = null;
		if ((piece.getType() == Piece.Type.Pawn) && (move.type == Move.Type.Regular)) {
			Position pos = piece.getPosition().minus(move.startPosition);
			if (Math.abs(pos.row) == 2)
				board.lastPawn2Move = piece.getPosition();
		}
	}

	private Move.Type getCastlingType(Piece piece, Position position) {

		Position delta = position.minus(piece.getPosition());
		if ((piece.getType() == Piece.Type.King) && (delta.getRow() == 0) && (Math.abs(delta.getCol()) == 2))
			if (delta.getCol() > 0)
				return Move.Type.CastlingK;
			else
				return Move.Type.CastlingQ;

		return Move.Type.Unknown;
	}

	public String exportMoveNotation() {

		PGN pgn = new PGN(this);
		return pgn.exportMoves();
	}

}
