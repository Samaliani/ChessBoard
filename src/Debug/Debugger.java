package Debug;

import java.util.Properties;

import Chess.Game;
import Communication.CommunicationListener;
import Communication.Event;
import Core.Manager;
import Core.Component;
import Core.SettingSubscriber;
import GUI.ChessBoardMain;
import Game.GameEventListener;

public class Debugger extends Component implements SettingSubscriber, GameEventListener, CommunicationListener {

	static final String DebuggerId = "debugger";

	ChessBoardMain frame;

	boolean dataDebug;
	String debugPath;
	boolean visualDebug;

	DebugOutput debugOutput;
	DebugBoardExtender boardExtender; 

	public Debugger(Manager manager, ChessBoardMain frame) {
		super(manager);
		this.frame = frame;
		boardExtender = new DebugBoardExtender(manager);
	}

	@Override
	public String getId() {
		return DebuggerId;
	}

	@Override
	public void appInitialization() {
		if (visualDebug)
			frame.boardPanel.addExtender(boardExtender);
	}

	@Override
	public void appFinalization() {
		debugOutput = null;
	}

	@Override
	public void appStart() {
		debugOutput = new DebugOutput(debugPath);
	}

	@Override
	public void loadSettings(Properties preferences) {
		dataDebug = Boolean.parseBoolean(preferences.getProperty("Debug.Output", "false"));
		debugPath = preferences.getProperty("Debug.Path", System.getProperty("user.dir") + "/debug");
		visualDebug = Boolean.parseBoolean(preferences.getProperty("Debug.Positions", "false"));
	}

	@Override
	public void saveSettings(Properties preferences) {
	}

	@Override
	public void beforeGame(Game game){
		boardExtender.refresh();
	}
	
	@Override
	public void startGame(Game game) {
		if (dataDebug) {
			debugOutput.reset(game.saveFEN());
		}
	}

	@Override
	public void makeMove(Game game) {
	}

	@Override
	public void endGame(Game game) {
	}

	@Override
	public void processEvent(Event event) {

		if (event.getData() != null) {
			boardExtender.setEventData(event.getData());
		}

		if (dataDebug) {
			debugOutput.processEvent(event);
		}
	}

	@Override
	public void portChanged(String portName) {
	}

}
