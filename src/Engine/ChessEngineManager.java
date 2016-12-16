package Engine;

import java.time.Duration;
import java.time.Instant;
import java.util.EventListener;
import java.util.List;
import java.util.Properties;

import Chess.Board;
import Chess.Game;
import Core.Component;
import Core.EventManager;
import Core.EventProvider;
import Core.Manager;
import Core.SettingSubscriber;
import Game.GameEventListener;

public class ChessEngineManager extends Component
		implements Runnable, SettingSubscriber, EventProvider, GameEventListener {

	boolean useEngine;
	int timeout;
	String engineCmd;
	Instant startTime;

	boolean running;

	Board board;
	String startPos = "";
	String moves = "";
	String currentPosition = "";
	String calculatingPosition = "";

	boolean startNewGame = false;
	boolean startCalculation = false;
	boolean stopCalculation = false;

	public ChessEngineManager(Manager manager) {
		super(manager);
	}

	public static final String Id = "engine";

	@Override
	public void appStart() {

		running = true;

		Thread eventThread = new Thread(this);
		eventThread.setDaemon(true);
		eventThread.start();
	}

	@Override
	public void appFinalization() {
		synchronized (this) {
			running = false;
		}
	}

	@Override
	public String getId() {
		return Id;
	}

	@Override
	public void loadSettings(Properties preferences) {

		useEngine = Boolean.parseBoolean(preferences.getProperty("Engine.Use", "false"));
		timeout = Integer.parseInt(preferences.getProperty("Engine.Time", "30000"));
		engineCmd = preferences.getProperty("Engine.Cmd", "");
	}

	@Override
	public void saveSettings(Properties preferences) {
	}

	@Override
	public boolean isSupportedListener(EventListener listener) {
		return (listener instanceof ChessEngineListener);
	}

	private void sendAnalysisInfoEvent(AnalysisInfo info) {

		EventManager eventManager = (EventManager) getManager().getComponent(EventManager.EventManagerId);
		List<EventListener> listeners = eventManager.getListeners(getId());
		for (EventListener listener : listeners)
			((ChessEngineListener) listener).analysisInfo(info);
	}

	/*
	 * private void sendAnalysisReadyEvent(String bestMove) {
	 * 
	 * EventManager eventManager = (EventManager)
	 * getManager().getComponent(EventManager.EventManagerId);
	 * List<EventListener> listeners = eventManager.getListeners(getId()); for
	 * (EventListener listener : listeners) ((ChessEngineEventListener)
	 * listener).analysisReady(bestMove); }
	 */

	public void run() {

		if (!useEngine)
			return;

		ChessEngine engine = new ChessEngine(engineCmd);
		if (!engine.isAvailable())
			return;

		engine.start();
		if (!engine.uciok)
		{
			System.out.println("No UCIOK");
			return;
		}

		String position = "";
		boolean calculating = false;
		//Instant startTime = Instant.now();

		while (true) {

			delay(10);
			
			synchronized (this) {
				// Check running
				if (!running)
					break;
			}

			synchronized (this) {
				if (startNewGame) {
					engine.startNewGame();
					position = startPos;
					startNewGame = false;
				}
			}

			if (calculating) {

				synchronized (this) {
					if (stopCalculation) {
						engine.cancelFind();
						calculating = false;
						continue;
					}
				}
				
				String line = engine.getInfoLine();
				if (line != "") {
					AnalysisInfo info = new AnalysisInfo(line, calculatingPosition);
					if (info.hasScore)
						sendAnalysisInfoEvent(info);
				}

				//Duration period = Duration.between(startTime, Instant.now());
				//if (period.toMillis() > timeout) { 
				//	stopCalculation();
				// }
			}

			String movesToCalc = "";
			synchronized (this) {
				if (startCalculation) {
					calculatingPosition = currentPosition;
					movesToCalc = moves;
					startCalculation = false;
				}
			}

			if (movesToCalc != "") {
				if (calculating)
					engine.cancelFind();

				engine.findBestMove(position, movesToCalc);
				startTime = Instant.now();
				calculating = true;
			}
		}

		engine.stop();
	}

	@Override
	public void beforeGame(Game game) {
	}

	@Override
	public void startGame(Game game) {

		String position = game.getBoard().saveFEN();
		startPos = "startpos";
		if (!position.equals(Board.StartFEN))
			startPos = "fen " + position;

		synchronized (this) {
			startNewGame = true;
		}
	}

	@Override
	public void makeMove(Game game) {
		startCalculation(game);
	}

	@Override
	public void rollbackMove(Game game) {
		makeMove(game);
	}

	@Override
	public void endGame(Game game) {
		stopCalculation();
	}

	private void stopCalculation() {
		synchronized (this) {
			stopCalculation = true;
		}
	}

	private void startCalculation(Game game) {

		moves = getMoves(game);
		currentPosition = game.getBoard().saveFEN();
		synchronized (this) {
			startCalculation = true;
		}
	}

	private String getMoves(Game game) {

		String result = "";
		for (int i = 0; i < game.getMoveCount(); i++)
			result += " " + game.getMove(i).toString2();
		return result;
	}

	public boolean useEngine() {
		return useEngine;
	}

	private void delay(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
