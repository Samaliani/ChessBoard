package LowLevel;

import java.util.ArrayList;
import java.util.List;

import Chess.Board;
import Chess.Move;
import Chess.MoveType;
import Chess.Piece;
import Chess.Position;
import Communication.Event;
import Communication.EventStorage;
import Communication.EventType;

public class Analyzer {

	Board board;
	EventStorage events;
	BoardData currentData;

	public Analyzer(Board board, EventStorage events) {
		this.board = board;
		this.events = events;
	}

	private BoardData getEventBoard(Event moveEvent) {
		// TODO More precise find of board data
		Event boardEvent = null;
		for (int i = 0; i < events.getEventCount(); i++) {
			Event currentEvent = events.getEvent(i);
			if (currentEvent.getType() != EventType.BoardChange)
				continue;

			if (currentEvent.getTime().before(moveEvent.getTime()))
				boardEvent = currentEvent;
			else
				break;
		}

		if (boardEvent == null)
			return null;
		else
			return boardEvent.getData();
	}

	private List<Piece> findMovePieces(BoardData moveData) {
		List<Position> positions = Utils.getDissapeared(board, currentData, moveData);

		List<Piece> pieces = new ArrayList<Piece>();
		for (Position pos : positions) {
			Piece piece = board.getPiece(pos);
			if (piece != null)
				pieces.add(piece);
		}

		return pieces;
	}
	
	private void processSimpleMove(Piece piece, BoardData moveData)
	{
		List<Position> positions = Utils.getAppeared(board, currentData, moveData);
		
		if (positions.size() == 1)
			piece.getPosition().set(positions.get(0));
		else
			// TODO
			;
			
		Move move = new Move(MoveType.Regular, piece.getType(), piece.getPosition());
		board.addMove(move);
		currentData = moveData;	
	}

	public void start() {
		currentData = Utils.getBoardData(board);
	}

	public void processEvents() {

		Event event = events.findEvent(EventType.Move);
		if (event == null)
			return;

		// Get move data
		BoardData boardData = getEventBoard(event);

		// Validate board
		int delta = currentData.getPieceCount() - boardData.getPieceCount();
		if (!((delta == 0) && (delta == 1)))
			;		
		
		// Moved pieces
		List<Piece> movedPieces = findMovePieces(boardData);

		if (movedPieces.size() == 1)
		{
			if (delta == 0)
				// Simple move
				processSimpleMove(movedPieces.get(0), boardData);
			else
				// Take move
				;
		}
		else if (movedPieces.size() == 2)
			// Castling 
			;
		
		
		// Clear
		events.clearEvents(event);
	}

}
