package App;

import Communication.CommunicationManager;
import Core.EventManager;
import Core.Manager;
import Core.SettingManager;
import Debug.Debugger;
import GUI.ChessBoardMain;
import Game.GameArchive;
import Game.GameManager;
import Game.GameModelManager;
import Timer.TimerManager;

public class ChessBoardManager extends Manager {

	GameManager sm;
	ChessBoardMain frame;

	CommunicationManager communication;
	GameManager gameManager;
	
	// Flags
	boolean closeApp;

	public ChessBoardManager() {
		super();
	}

	@Override
	public void createComponents() {

		addComponent(new EventManager(this));
		addComponent(new SettingManager(this, "settings.", "Electronic Chess Board settings"));

		communication = new CommunicationManager(this);
		addComponent(communication);

		addComponent(new GameManager(this));
		addComponent(new GameModelManager(this));
		addComponent(new TimerManager(this));
		
		// Frame
		frame = new ChessBoardMain(this);

		// Debug & Archive 
		addComponent(new Debugger(this, frame));
		addComponent(new GameArchive(this));
	}

	@Override
	protected void initialize() {

		super.initialize();
		frame.initialize();
	}

	public ChessBoardMain getFrame() {
		return frame;
	}

	public void run() {

		super.run();

		while (!closeApp)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}

		finish();
	}

	public void stop() {
		closeApp = true;
	}
}