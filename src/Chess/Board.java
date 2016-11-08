package Chess;

import java.util.ArrayList;
import java.util.List;

import Chess.Piece.Color;
import Chess.FEN.FEN;
import Chess.FEN.FENException;

public class Board {

	List<Piece> pieces = new ArrayList<Piece>();

	Color turnColor;

	int currentMove;
	int emptyMoves;
	Position lastPawn2Move;
	boolean[] castlingWhite = new boolean[2];
	boolean[] castlingBlack = new boolean[2];

	public Board() {
	}

	public Board(Board board) {
		apply(board);
	}

	public void apply(Board board) {
	
		pieces.clear();
		for (Piece piece : board.pieces) {
			pieces.add(new Piece(piece));
		}

		turnColor = board.turnColor;
		currentMove = board.currentMove;
		emptyMoves = board.emptyMoves;
		lastPawn2Move = board.lastPawn2Move;
		castlingWhite = board.castlingWhite.clone();
		castlingBlack = board.castlingBlack.clone();
	}

	public void setCastlingAvailable(Color color, Move.Type castling, boolean value) {
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

	public List<Piece> getPieces() {
		return pieces;
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
		pieces.clear();
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
