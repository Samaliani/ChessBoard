package Game;

import Chess.Game;
import Chess.GameResult;
import Chess.Logic.MainLogic;
import Core.Manager;
import Timer.TimerListener;
import Timer.TimerManager;

public class GameModel implements GameEventListener, TimerListener {

	Manager manager;
	String name;
	
	public GameModel(Manager manager, String name) {
		this.manager = manager;
		this.name = name;
	}

	private static final String id = "base";

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public void beforeGame(Game game) {
	}

	@Override
	public void startGame(Game game) {
	}

	@Override
	public void makeMove(Game game) {
		
		if(MainLogic.isCheckmate(game.getBoard(), game.getTurnColor()))
			getGameManager().finishGame(GameResult.winColor(game.getTurnColor().inverse()));
		else if(MainLogic.isTie(game.getBoard(), game.getTurnColor()))
			getGameManager().finishGame(GameResult.Tie);		
	}

	@Override
	public void endGame(Game game) {
	}

	@Override
	public void timerChanged() {
		if (getTimerManager().getWhiteTime().isZero())
			getGameManager().finishGame(GameResult.Black);
		if (getTimerManager().getBlackTime().isZero())
			getGameManager().finishGame(GameResult.White);
		
	}
	
	@Override
	public void timerModeChanged() {
	}
	
	protected GameManager getGameManager() {
		return (GameManager) manager.getComponent(GameManager.GameManagerId);
	}

	private TimerManager getTimerManager() {
		return (TimerManager) manager.getComponent(TimerManager.TimerManagerId);
	}

}