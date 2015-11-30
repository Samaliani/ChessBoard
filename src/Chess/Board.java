package Chess;

import java.util.ArrayList;
import java.util.List;

import Chess.BoardEventListener;
import Chess.Piece.Color;
import Chess.FEN.FEN;
import Chess.FEN.FENException;
import Chess.Logic.CastlingLogic;
import Chess.Logic.MainLogic;
import Chess.Logic.PieceLogic;

public class Board {

	List<Move> moves = new ArrayList<Move>();
	List<Piece> pieces = new ArrayList<Piece>();

	Color turnColor;

	int currentMove;
	int emptyMoves;
	Position lastPawn2Move;
	boolean[] castlingWhite = new boolean[2];
	boolean[] castlingBlack = new boolean[2];

	// --------------------------------------
	// Moves
	// --------------------------------------

	public boolean makeMove(Position start, Position finish) {

		Piece piece = getPiece(start);
		if (piece.getColor() != turnColor)
			return false;
		
		Move.Type type;
		Position position = finish;

		PieceLogic logic = MainLogic.getPieceLogic(this, piece);
		if (logic.validateTake(position))
			type = Move.Type.Take;
		else if (logic.validateMove(position))
			type = Move.Type.Regular;
		else
			return false;

		if (!MainLogic.canMovePiece(this, piece))
			return false;

		Position ambiguity = getAmbiguity(piece, position, type);
		if (type == Move.Type.Take)
			removePiece(logic.getTakenPosition(position));
		processMove(type, piece, position);
		piece.move(position);

		Move move = new Move(type, piece.getType(), position, ambiguity);
		addMove(move);
		return true;
	}

	public boolean makeCastling(Position start, Position finish) {

		Piece king = getPiece(start);
		Position position = finish;

		Move.Type type = getCastlingType(king, position);

		CastlingLogic cv = new CastlingLogic(this);
		if (!cv.validate(type))
			return false;

		processMove(type, king, position);

		king.move(position);
		Piece rook;
		switch (type) {
		case CastlingQ:
			rook = getPiece(new Position(0, position.getRow()));
			rook.move(position.plus(new Position(1, 0)));
			break;
		case CastlingK:
			rook = getPiece(new Position(7, position.getRow()));
			rook.move(position.minus(new Position(1, 0)));
			break;
		default:
			return false;
		}

		Move move = new Move(type);
		addMove(move);
		return true;
	}
	
	private void addMove(Move move){
		move.check = MainLogic.isCheck(this, Color.not(turnColor));
		if (move.check)
			move.checkmate = MainLogic.isCheckmate(this, Color.not(turnColor));
		moves.add(move);

		if (turnColor == Color.Black)
			currentMove++;
		turnColor = Color.not(turnColor);
		raiseBoardMoveEvent();
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

	public int getMoveCount() {
		return moves.size();
	}

	public Move getMove(int index) {
		return moves.get(index);
	}

	// --------------------------------------
	// Events
	// --------------------------------------
	List<BoardEventListener> listeners = new ArrayList<BoardEventListener>();

	public void addBoardEventListener(BoardEventListener listener) {
		listeners.add(listener);
	}

	public void raiseBoardMoveEvent() {
		for (BoardEventListener listener : listeners)
			listener.boardMove();
	}
	
	public void raiseBoardChangeEvent() {
		for (BoardEventListener listener : listeners)
			listener.boardChanged();
	}	

	// --------------------------------------
	// Pieces
	// --------------------------------------

	// This method only for loading chess position (FEN, start, etc)
	public void placePiece(Piece.Type type, Color color, Position position) {
		pieces.add(new Piece(type, color, position));
	}

	public void removePiece(Position position) {
		Piece piece = getPiece(position);
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

	private Position getAmbiguity(Piece piece, Position target, Move.Type type) {

		List<Piece> pieces = getPieces(piece.getColor(), piece.getType());
		if (pieces.size() == 1)
			return null;

		List<Piece> ambiguityPieces = new ArrayList<Piece>();
		for (Piece p : pieces) {
			PieceLogic logic = MainLogic.getPieceLogic(this, p);
			switch (type) {
			case Regular:
				if (!logic.validateMove(target))
					continue;
				break;
			case Take:
				if (!logic.validateTake(target))
					continue;
				break;
			default:
				continue;
			}
			ambiguityPieces.add(p);
		}

		ambiguityPieces.remove(piece);
		if (ambiguityPieces.size() == 0)
			return null;

		Position ambiguity = new Position(ambiguityPieces.get(0).getPosition());
		for (Piece p : ambiguityPieces)
			if (p.getPosition().getCol() == piece.getPosition().getCol())
				ambiguity = new Position(p.getPosition());

		return ambiguity;
	}

	public void setTurnColor(Color value) {
		if ((turnColor == Color.Black) && (moves.size() == 0))
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

	private static String cStartFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

	public void reset() {
		// Start position
		loadFEN(cStartFen);
		raiseBoardChangeEvent();
	}

	private void clear() {
		moves.clear();
		pieces.clear();
	}

	// Processing after each move
	private void processMove(Move.Type type, Piece piece, Position position) {
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

	public void saveFEN(String data) {
		FEN fen = new FEN(this);
		data = fen.save();
	}

}
