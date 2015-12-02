package App;

import java.util.EventListener;
import java.util.List;

import Chess.Board;
import Chess.Piece.Color;
import Chess.Position;
import Communication.CommunicationListener;
import Communication.Event;
import Core.Manager;
import LowLevel.BoardData;
import LowLevel.Utils;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;

public class GameManager extends Component implements EventProvider, CommunicationListener {

	enum State {
		Wait, Invalid
	}

	public static final String GameManagerId = "game";;

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

	public GameManager(Manager manager, Board board) {

		super(manager);
		this.board = board;
		currentState = State.Wait;
		resetTurn();
	}

	@Override
	public String getId() {
		return GameManagerId;
	}

	@Override
	public void appInitialization() {
	}

	@Override
	public void appFinalization() {
	}

	@Override
	public void appStart() {
	}

	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof GameEventListener);
	}

	@Override
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

	@Override
	public void portChanged(String portName) {
	}

	private void raiseGameResetEvent() {
		EventManager eventManager = (EventManager) getManager().getComponent(EventManager.EventManagerId);
		List<EventListener> listeners = eventManager.getListeners(getId());
		for (EventListener listener : listeners)
			((GameEventListener) listener).gameReset();
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
			return data.getData(0);
		else
			return data.getData(7);
	}

	private void setKingRank(BoardData data, byte rank) {
		if (board.getTurnColor() == Color.White)
			data.setData(0, rank);
		else
			data.setData(7, rank);
	}

	private void processWait(Event event) {

		switch (event.getType()) {
		case ButtonWhite:
		case ButtonBlack:
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

		if (lastData.equals(turnData))
			return State.Wait;

		int delta = turnData.getPieceCount() - lastData.getPieceCount();
		BoardData appeared = getAppeared(turnData, lastData);
		BoardData disappeared = getDisappeared(turnData, lastData);

		boolean success = false;
		// Move
		if (delta == 0) {
			// Castling
			if (appeared.getPieceCount() == 2) {
				int disRank = getKingRank(disappeared);
				setKingRank(disappeared, (byte) (disRank & 0x08));
				int appRank = getKingRank(appeared);
				setKingRank(appeared, (byte) (appRank & 0x22));
				success = makeCastling(Utils.getPiecePosition(disappeared), Utils.getPiecePosition(appeared));
			} else
				// Regular
				success = makeMove(Utils.getPiecePosition(disappeared), Utils.getPiecePosition(appeared));
		}
		// Take
		else if ((delta == 1) && (inactivePos != -1)) {

			// Regular take
			if (appeared.getPieceCount() == 0)
				success = makeMove(Utils.getPiecePosition(disappeared), inactivePos);
			// An passant
			else {
				BoardData disActive = BoardData.intersect(getActiveData(), disappeared);
				if ((disActive.getPieceCount() == 1) && (appeared.getPieceCount() == 1))
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
				raiseGameResetEvent();
				board.reset();
				currentState = State.Wait;
				resetTurn();
			}
		}
	}

	private State processBoardChange(BoardData data) {

		lastData = data;

		if (data.equals(turnData)) {
			resetTurn();
			return State.Wait;
		}

		if (data.getPieceCount() > turnData.getPieceCount())
			return State.Invalid;

		BoardData dissapeared = getDisappeared(turnData, data);

		BoardData disActive = BoardData.intersect(getActiveData(), dissapeared);
		if (disActive.getPieceCount() > 2)
			return State.Invalid;
		else if (disActive.getPieceCount() == 2) {
			// Possible Castling
			return State.Wait;
		} else if (disActive.getPieceCount() == 1) {
			activePos = Utils.getPiecePosition(disActive);
		}

		BoardData disInactive = BoardData.intersect(getInactiveData(), dissapeared);
		if (disInactive.getPieceCount() > 1)
			return State.Invalid;
		else if (disInactive.getPieceCount() == 1) {
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
