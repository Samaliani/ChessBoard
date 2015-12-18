package Chess;

import java.util.ArrayList;
import java.util.List;

import Chess.Piece.Color;
import Chess.FEN.FEN;
import Chess.FEN.FENException;

public class Board {

	List<Move> moves = new ArrayList<Move>();
	List<Piece> pieces = new ArrayList<Piece>();

	Color turnColor;

	int currentMove;
	int emptyMoves;
	Position lastPawn2Move;
	boolean[] castlingWhite = new boolean[2];
	boolean[] castlingBlack = new boolean[2];

	public void setCastlingAvailable(Color color, Move.Type castling,
			boolean value) {
		boolean[] castlings;
		if (color == Color.White)
			castlings = castlingWhite;
		else
			castlings = castlingBlack;

		switch (castling) {
		case CastlingK:
			castlings[0] = value;
			break;
		case CastlingQ:
			castlings[1] = value;
			break;
		default:
		}
	}

	public boolean getCastlingAvailable(Color color, Move.Type castling) {
		boolean[] castlings;
		if (color == Color.White)
			castlings = castlingWhite;
		else
			castlings = castlingBlack;

		switch (castling) {
		case CastlingK:
			return castlings[0];
		case CastlingQ:
			return castlings[1];
		default:
			return false;
		}
	}

	// --------------------------------------
	// Pieces
	// --------------------------------------

	// This method only for loading chess position (FEN, start, etc)
	public void placePiece(Piece.Type type, Color color, Position position) {
		pieces.add(new Piece(type, color, position));
	}

	public void removePieceAt(Position position) {
		Piece piece = getPiece(position);
		pieces.remove(piece);
	}

	public void addPiece(Piece piece) {
		pieces.add(piece);
	}

	public void removePiece(Piece piece) {
		pieces.remove(piece);
	}

	public boolean hasPiece(Position position) {
		return (getPiece(position) != null);
	}

	public Piece getPiece(Position position) {
		for (Piece piece : pieces)
			if (piece.position.equals(position))
				return piece;

		return null;
	}

	public List<Piece> getPieces(Color color) {
		List<Piece> result = new ArrayList<Piece>();
		for (Piece piece : pieces)
			if (piece.getColor() == color)
				result.add(piece);
		return result;
	}

	public List<Piece> getPieces(Color color, Piece.Type type) {
		List<Piece> result = new ArrayList<Piece>();
		for (Piece piece : pieces)
			if ((piece.getType() == type) && (piece.getColor() == color))
				result.add(piece);
		return result;
	}

	public void setTurnColor(Color value) {
		if ((value == Color.Black) && (moves.size() == 0))
			moves.add(new Move(Move.Type.Unknown));

		turnColor = value;
	}

	public Piece.Color getTurnColor() {
		return turnColor;
	}

	public void setCurrentMove(int value) {
		currentMove = value;
	}

	public int getCurrentMove() {
		return currentMove;
	}

	public void setEmptyMoves(int value) {
		emptyMoves = value;
	}

	public int getEmptyMoves() {
		return emptyMoves;
	}

	public void setLastPawnMove(Position p) {
		lastPawn2Move = p;
	}

	public Position getLastPawnMove() {
		return lastPawn2Move;
	}

	public static final String StartFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	public void reset() {
		loadFEN(StartFEN);
	}

	private void clear() {
		moves.clear();
		pieces.clear();
	}

	// Processing after each move
	public void processMove(Move.Type type, Piece piece, Position position) {
		updateCastlings(piece, type);
		updateEmptyMove(piece, type);
		updateLastPawnMove(piece, position);
	}

	private void updateCastlings(Piece piece, Move.Type type) {
		switch (piece.getType()) {
		case King:
			setCastlingAvailable(getTurnColor(), Move.Type.CastlingK, false);
			setCastlingAvailable(getTurnColor(), Move.Type.CastlingQ, false);
			break;
		case Rook:
			if (piece.getPosition().getCol() == 7)
				setCastlingAvailable(getTurnColor(), Move.Type.CastlingK, false);
			if (piece.getPosition().getCol() == 0)
				setCastlingAvailable(getTurnColor(), Move.Type.CastlingQ, false);
			break;
		default:
			// Nothing
		}
	}

	private void updateEmptyMove(Piece piece, Move.Type type) {
		if ((piece.getType() != Piece.Type.Pawn) && (type == Move.Type.Regular))
			emptyMoves++;
		else
			emptyMoves = 0;
	}

	private void updateLastPawnMove(Piece piece, Position position) {
		lastPawn2Move = null;
		if (piece.getType() == Piece.Type.Pawn) {
			Position pos = position.minus(piece.getPosition());
			if (Math.abs(pos.row) == 2)
				lastPawn2Move = position;
		}
	}

	public void loadFEN(String data) {
		clear();
		FEN fen = new FEN(this);
		try {
			fen.load(data);
		} catch (FENException e) {
		}
	}

	public String saveFEN() {
		FEN fen = new FEN(this);
		return fen.save();
	}

}
