package Chess;

import java.util.ArrayList;
import java.util.List;

import Chess.PieceType;
import FEN.FEN;
import FEN.FENException;

public class Board {

	List<Move> moves = new ArrayList<Move>();

	List<Piece> whitePieces = new ArrayList<Piece>();
	List<Piece> blackPieces = new ArrayList<Piece>();
	
	PieceColor nextMove;

	int currentMove;
	int emptyMoves;
	Position lastPawn2Move;
	
	// --------------------------------------
	// Moves
	// --------------------------------------

	public void addMove(Move move)
	{
		//
		moves.add(move);
	}
	
	public int getMoveCount()
	{
		return moves.size();
	}
	
	public Move getMove(int index)
	{
		return moves.get(index);
	}	
	
	// --------------------------------------
	// Pieces
	// --------------------------------------

	// This method only for loading chess position (FEN, start, etc) 
	public void placePiece(PieceType type, PieceColor color, Position position)
	{
		switch(color)
		{
		case White:
			whitePieces.add(new Piece(type, position));
			break;
		case Black:
			blackPieces.add(new Piece(type, position));
			break;
		default:
			assert false;
		}
	}
	
	public void removePiece(Position position)
	{
		// TODO
	}
	
	public Piece getPiece(Position position)
	{
		for(Piece piece : whitePieces)
			if (piece.position.equals(position))
				return piece;

		for(Piece piece : blackPieces)
			if (piece.position.equals(position))
				return piece;

		return null;
	}

	
	public void setCurrentMove(int value)
	{
		currentMove = value;
	}
	
	public int getCurrentMove()
	{
		return currentMove;
	}

	public void setEmptyMoves(int value)
	{
		emptyMoves = value;
	}
	
	public int getEmptyMoves()
	{
		return emptyMoves;
	}

	public void setLastPawnMove(Position p) {
		lastPawn2Move = p;
	}

	
	private static String cStartFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	public void reset()
	{
		// Start position
		loadFEN(cStartFen);
	}

	private void clear() {
		moves.clear();
		whitePieces.clear();
		blackPieces.clear();
	}

	public void loadFEN(String data)
	{
		clear();
		FEN fen = new FEN(this);
		try {
			fen.load(data);
		} catch (FENException e) {
		}
	}
	public void saveFEN(String data)
	{
		FEN fen = new FEN(this);
		data = fen.save();
	}

	
}
