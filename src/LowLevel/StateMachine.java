package LowLevel;

import Chess.Board;
import Chess.Piece.Color;
import Chess.Position;
import Communication.Event;

public class StateMachine {

	enum State {
		Wait, Invalid
	};

	// Main data
	Board board;

	// Currents
	State currentState;

	// Move
	BoardData turnData;
	BoardData lastData;

	int activePos;
	int inactivePos;

	BoardData whiteData;
	BoardData blackData;

	public StateMachine(Board board) {

		this.board = board;
		currentState = State.Wait;
		resetTurn();
	}

	public void processEvent(Event event) {

		switch (currentState) {
		case Wait:
			processWait(event);
			break;
		case Invalid:
			processInvalid(event);
			break;
		default:
			assert false;
		}
	}

	private BoardData getAppeared(BoardData begin, BoardData end) {
		return BoardData.intersect(BoardData.not(begin), end);
	}

	private BoardData getDisappeared(BoardData begin, BoardData end) {
		return BoardData.intersect(begin, BoardData.not(end));
	}

	private BoardData getActiveData() {
		if (board.getTurnColor() == Color.White)
			return whiteData;
		else
			return blackData;
	}

	private BoardData getInactiveData() {
		if (board.getTurnColor() == Color.White)
			return blackData;
		else
			return whiteData;
	}

	private byte getKingRank(BoardData data) {
		if (board.getTurnColor() == Color.White)
			return data.data[0];
		else
			return data.data[7];
	}

	private void setKingRank(BoardData data, byte rank) {
		if (board.getTurnColor() == Color.White)
			data.data[0] = rank;
		else
			data.data[7] = rank;
	}

	/*private void processStart() {

		currentState = State.Wait;
		resetTurn();
	}*/

	private void processWait(Event event) {

		switch (event.getType()) {
		case Move:
			currentState = processMove();
			break;
		case BoardChange:
			currentState = processBoardChange(event.getData());
			break;
		default:
			assert false;
		}

	}

	private State processMove() {

		int delta = turnData.pieceCount - lastData.pieceCount;
		BoardData appeared = getAppeared(turnData, lastData);
		BoardData disappeared = getDisappeared(turnData, lastData);

		boolean success = false;
		// Move
		if (delta == 0) {
			// Castling
			if (appeared.pieceCount == 2) {
				int disRank = getKingRank(disappeared);
				setKingRank(disappeared, (byte)(disRank & 0x08));
				int appRank = getKingRank(appeared);
				setKingRank(appeared, (byte)(appRank & 0x16));
				success = makeCastling(Utils.getPiecePosition(disappeared), Utils.getPiecePosition(appeared));
			} else
				// Regular
				success = makeMove(Utils.getPiecePosition(disappeared), Utils.getPiecePosition(appeared));
		}
		// Take
		else if ((delta == 1) && (inactivePos != -1)) {

			// Regular take
			if (appeared.pieceCount == 0)
				success = makeMove(Utils.getPiecePosition(disappeared), inactivePos);
			// An passant
			else {
				BoardData disActive = BoardData.intersect(getActiveData(), disappeared);
				if ((disActive.pieceCount == 1) && (appeared.pieceCount == 1))
					success = makeMove(Utils.getPiecePosition(disActive), Utils.getPiecePosition(appeared));
			}
		}

		if (!success)
			return State.Invalid;
		
		resetTurn();
		turnData = lastData;
		return State.Wait;
	}

	private void processInvalid(Event event) {

		// Wait for previous move or initial state
		if (event.getType() == Event.Type.BoardChange) {
			BoardData data = event.getData();

			currentState = processBoardChange(data);

			if (data.equals(BoardData.initialData)) {
				// TODO
				//currentState = State.Start;
				return;
			}
		}
	}

	private State processBoardChange(BoardData data) {

		lastData = data;

		if (data.equals(turnData)) {
			resetTurn();
			return State.Wait;
		}

		if (data.pieceCount > turnData.pieceCount)
			return State.Invalid;

		BoardData dissapeared = getDisappeared(turnData, data);

		BoardData disActive = BoardData.intersect(getActiveData(), dissapeared);
		if (disActive.pieceCount > 2)
			return State.Invalid;
		else if (disActive.pieceCount == 2) {
			// Possible Castling
			return State.Wait;
		} else if (disActive.pieceCount == 1) {
			activePos = Utils.getPiecePosition(disActive);
		}

		BoardData disInactive = BoardData.intersect(getInactiveData(), dissapeared);
		if (disInactive.pieceCount > 1)
			return State.Invalid;
		else if (disInactive.pieceCount == 1) {
			inactivePos = Utils.getPiecePosition(disInactive);
		}

		return State.Wait;
	}

	private void resetTurn() {
		activePos = -1;
		inactivePos = -1;

		turnData = Utils.getBoardData(board);
		whiteData = Utils.getBoardData(board, Color.White);
		blackData = Utils.getBoardData(board, Color.Black);
	}

	private boolean makeMove(int start, int finish) {
		Position posStart = new Position(start % 8, start / 8);
		Position posFinish = new Position(finish % 8, finish / 8);

		return board.makeMove(posStart, posFinish);
	}

	private boolean makeCastling(int start, int finish) {
		Position posStart = new Position(start % 8, start / 8);
		Position posFinish = new Position(finish % 8, finish / 8);

		return board.makeCastling(posStart, posFinish);
	}
}
