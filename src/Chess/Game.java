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

	Board board;
	List<Move> moves;
	GameResult result;

	public Game() {
		board = new Board();
		board.loadFEN(Board.StartFEN);
		moves = new ArrayList<Move>();
		result = GameResult.Unknown;
	}

	public Board getBoard() {
		return board;
	}

	public Color getTurnColor() {
		return board.getTurnColor();
	}

	public void loadFEN(String fen) {
		board.loadFEN(fen);
	}

	public String saveFEN() {
		FEN fen = new FEN(board);
		return fen.save();
	}

	private Color getActiveColor() {
		return board.getTurnColor();
	}

	private Color getInactiveColor() {
		return board.getTurnColor().inverse();
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
		return moves.size();
	}

	public Move getMove(int index) {
		return moves.get(index);
	}

	public boolean makeMove(Position start, Position finish) {

		Piece piece = board.getPiece(start);
		if (piece.getColor() != getActiveColor())
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

		if (!MainLogic.canMovePiece(board, piece, position))
			return false;

		Position ambiguity = getAmbiguity(piece, position, type);
		if (type == Move.Type.Take)
			board.removePieceAt(logic.getTakenPosition(position));
		board.processMove(type, piece, position);
		piece.move(position);

		Move move = new Move(type, piece.getType(), position, ambiguity);

		if (MainLogic.isPromotion(board, piece, position)) {
			Piece.Type promoteTo = Piece.Type.Queen;
			// TODO Add event about promotion
			move.setPromotion(promoteTo);
			board.removePiece(piece);
			Piece promotedPiece = new Piece(promoteTo, piece.getColor(), position);
			board.addPiece(promotedPiece);
		}

		addMove(move);
		return true;
	}

	public boolean makeCastling(Position start, Position finish) {

		Piece king = board.getPiece(start);
		Position position = finish;

		Move.Type type = getCastlingType(king, position);

		CastlingLogic cv = new CastlingLogic(board);
		if (!cv.validate(type))
			return false;

		board.processMove(type, king, position);

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

		Move move = new Move(type);
		addMove(move);
		return true;
	}

	private Position getAmbiguity(Piece piece, Position target, Move.Type type) {

		List<Piece> pieces = board.getPieces(piece.getColor(), piece.getType());
		if (pieces.size() == 1)
			return null;

		List<Piece> ambiguityPieces = new ArrayList<Piece>();
		for (Piece p : pieces) {
			PieceLogic logic = MainLogic.getPieceLogic(board, p);
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

	private void addMove(Move move) {
		move.check = MainLogic.isCheck(board, getInactiveColor());
		if (move.check)
			move.checkmate = MainLogic.isCheckmate(board, getInactiveColor());
		moves.add(move);

		if (getActiveColor() == Color.Black)
			board.currentMove++;
		board.turnColor = board.turnColor.inverse();
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
