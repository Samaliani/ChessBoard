package Debug;

import java.util.Properties;

import App.GameEventListener;
import Communication.CommunicationListener;
import Communication.Event;
import Core.Manager;
import Core.Component;
import Core.SettingSubscriber;
import GUI.ChessBoardMain;

public class Debugger extends Component implements SettingSubscriber, GameEventListener, CommunicationListener {

	static final String DebuggerId = "debugger";

	ChessBoardMain frame;

	boolean dataDebug;
	String debugPath;
	boolean visualDebug;

	DebugOutput debugOutput;

	public Debugger(Manager manager, ChessBoardMain frame) {
		super(manager);
		this.frame = frame;
	}

	public String getId() {
		return DebuggerId;
	}

	@Override
	public void appInitialization() {
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
	public void gameReset() {
		if (dataDebug)
			debugOutput.reset();
	}

	@Override
	public void processEvent(Event event) {
		if (visualDebug)
			if (event.getData() != null) {
				frame.boardPanel.setData(event.getData());
				frame.boardPanel.repaint();
			}

		if (dataDebug) {
			debugOutput.processEvent(event);
		}
	}

	@Override
	public void portChanged(String portName) {
	}

}
