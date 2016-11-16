package Game;

import java.util.EventListener;
import java.util.List;

import Chess.Board;
import Chess.Game;
import Chess.GameResult;
import Chess.Piece;
import Chess.Piece.Color;
import Chess.Position;
import Communication.CommunicationListener;
import Communication.CommunicationManager;
import Communication.Event;
import Communication.OutEvent;
import Core.Manager;
import LowLevel.BoardData;
import LowLevel.Utils;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;

public class GameManager extends Component implements EventProvider, CommunicationListener {

	enum State {
		Start, Wait, End, Invalid
	}

	public static final String GameManagerId = "game";

	Game game;

	// Currents
	State currentState;
	// Move
	BoardData turnData;
	BoardData lastData;

	int activePos;
	int inactivePos;

	BoardData whiteData;
	BoardData blackData;

	public GameManager(Manager manager) {

		super(manager);
	}

	private void reset() {
		currentState = State.Start;
		game.loadFEN(Board.StartFEN);
		sendBoardEvent(OutEvent.Type.RequestBoard);
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
	public void appStart() {
		game = new Game();
		loadFEN(Board.StartFEN);
	}

	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof GameEventListener);
	}

	@Override
	public void processEvent(Event event) {
		switch (currentState) {
		case Start:
			processStart(event);
			break;
		case Wait:
			processWait(event);
			break;
		case End:
			processEnd(event);
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

	public Board getBoard() {

		if (game != null)
			return game.getBoard();
		else
			return new Board();		
	}

	public void loadFEN(String data) {
		reset();
		game.loadFEN(data);

	}

	public String saveFEN() {
		return game.saveFEN();
	}

	public void finishGame(GameResult result) {

		if (currentState == State.End)
			return;
		
		game.setResult(result);
		doEndGame();
		currentState = State.End;
	}

	private List<EventListener> getListeners() {
		EventManager eventManager = (EventManager) getManager().getComponent(EventManager.EventManagerId);
		return eventManager.getListeners(getId());
	}

	private void doBeforeGame() {

		sendBoardEvent(OutEvent.Type.PositionSignal);

		for (EventListener listener : getListeners())
			((GameEventListener) listener).beforeGame(game);
	}

	private void doStartGame() {
		for (EventListener listener : getListeners())
			((GameEventListener) listener).startGame(game);
	}

	private void doMakeMove() {
		sendBoardEvent(OutEvent.Type.MoveSignal);
		for (EventListener listener : getListeners())
			((GameEventListener) listener).makeMove(game);
	}
	
	private void doRollbackMove() {
		sendBoardEvent(OutEvent.Type.MoveSignal);
		for (EventListener listener : getListeners())
			((GameEventListener) listener).rollbackMove(game);
	}

	private void doEndGame() {
		sendBoardEvent(OutEvent.Type.FinalSignal);
		for (EventListener listener : getListeners())
			((GameEventListener) listener).endGame(game);
	}

	private BoardData getAppeared(BoardData begin, BoardData end) {
		return BoardData.intersect(BoardData.not(begin), end);
	}

	private BoardData getDisappeared(BoardData begin, BoardData end) {
		return BoardData.intersect(begin, BoardData.not(end));
	}

	private BoardData getActiveData() {
		if (game.getTurnColor() == Color.White)
			return whiteData;
		else
			return blackData;
	}

	private BoardData getInactiveData() {
		if (game.getTurnColor() == Color.White)
			return blackData;
		else
			return whiteData;
	}

	private byte getKingRank(BoardData data) {
		if (game.getTurnColor() == Color.White)
			return data.getData(0);
		else
			return data.getData(7);
	}

	private void setKingRank(BoardData data, byte rank) {
		if (game.getTurnColor() == Color.White)
			data.setData(0, rank);
		else
			data.setData(7, rank);
	}

	private void sendBoardEvent(OutEvent.Type eventType) {
		CommunicationManager communication = (CommunicationManager) getManager()
				.getComponent(CommunicationManager.CommunicationManagerId);
		communication.sendEvent(new OutEvent(eventType));
	}

	private void processStart(Event event) {

		switch (event.getType()) {
		case ButtonWhite:
			if ((game.getTurnColor() != Piece.Color.White) && (lastData.equals(Utils.getBoardData(getBoard())))) {
				sendBoardEvent(OutEvent.Type.MoveSignal);
				doStartGame();
				currentState = State.Wait;
			}
			break;
		case ButtonBlack:
			if ((game.getTurnColor() != Piece.Color.Black) && (lastData.equals(Utils.getBoardData(getBoard())))) {
				sendBoardEvent(OutEvent.Type.MoveSignal);
				doStartGame();
				currentState = State.Wait;
			}
			break;
		case BoardChange:
			lastData = event.getData();
			if (lastData.equals(Utils.getBoardData(getBoard())))
				doBeforeGame();
			break;
		case Rollback:
			break;
		default:
			assert false;
		}
	}

	private void processWait(Event event) {

		switch (event.getType()) {
		case ButtonWhite:
			currentState = processMove(Piece.Color.White);
			break;
		case ButtonBlack:
			currentState = processMove(Piece.Color.Black);
			break;
		case Rollback:
			processRollback();
			break;
		case BoardChange:
			currentState = processBoardChange(event.getData());
			break;
		default:
			assert false;
		}
	}

	private State processMove(Piece.Color color) {

		if (game.getTurnColor() != color) {
			sendBoardEvent(OutEvent.Type.ErrorSignal);
			return State.Wait;
		}
		if ((lastData == null) || (lastData.equals(turnData)))
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
				BoardData disInactive = BoardData.intersect(getInactiveData(), disappeared);
				BoardData disActive = BoardData.intersect(getActiveData(), disappeared);
				if ((disActive.getPieceCount() == 1) && (disInactive.getPieceCount() == 1) && (appeared.getPieceCount() == 1))
					success = makePassant(Utils.getPiecePosition(disActive), Utils.getPiecePosition(appeared), Utils.getPiecePosition(disInactive));
			}
		}

		if (!success) {
			sendBoardEvent(OutEvent.Type.ErrorSignal);
			return State.Invalid;
		}

		resetTurn();
		turnData = lastData;

		doMakeMove();
		return currentState;
	}

	private void processRollback() {

		if (!game.rollbackMove()){
			sendBoardEvent(OutEvent.Type.ErrorSignal);
			return;
		}

		resetTurn();
		sendBoardEvent(OutEvent.Type.RequestBoard);
		doRollbackMove();
	}

	private void processInvalid(Event event) {

		// Wait for previous move or initial state
		switch (event.getType()) {
		case ButtonWhite:
		case ButtonBlack:
			sendBoardEvent(OutEvent.Type.ErrorSignal);
			break;
		case Rollback:
			processRollback();
			break;
		case BoardChange:
			BoardData data = event.getData();

			currentState = processBoardChange(data);
			if (currentState != State.Invalid)
				sendBoardEvent(OutEvent.Type.NoErrorSignal);

			if (data.equals(BoardData.initialData)) {
				finishGame(GameResult.Unknown);
				processEvent(event);
			}
			break;
		default:
			assert false;
		}
	}

	private void processEnd(Event event) {

		// Wait for previous move or initial state
		if ((event.getType() == Event.Type.ButtonWhite) || (event.getType() == Event.Type.ButtonBlack))
			sendBoardEvent(OutEvent.Type.ErrorSignal);
		else if (event.getType() == Event.Type.BoardChange) {

			BoardData data = event.getData();

			if (data.equals(BoardData.initialData)) {
				reset();
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

		turnData = Utils.getBoardData(game.getBoard());
		whiteData = Utils.getBoardData(game.getBoard(), Color.White);
		blackData = Utils.getBoardData(game.getBoard(), Color.Black);
	}

	private boolean makeMove(int start, int finish) {
		Position posStart = new Position(start % 8, start / 8);
		Position posFinish = new Position(finish % 8, finish / 8);

		return game.makeMove(posStart, posFinish);
	}
	
	private boolean makePassant(int start, int finish, int taken) {
		Position posStart = new Position(start % 8, start / 8);
		Position posFinish = new Position(finish % 8, finish / 8);
		Position posTaken = new Position(taken % 8, taken / 8);

		return game.makeMovePassant(posStart, posFinish, posTaken);
	}
	

	private boolean makeCastling(int start, int finish) {
		Position posStart = new Position(start % 8, start / 8);
		Position posFinish = new Position(finish % 8, finish / 8);

		return game.makeCastling(posStart, posFinish);
	}

}
