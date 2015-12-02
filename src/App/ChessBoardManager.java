package App;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import Chess.Board;
import Chess.BoardEventListener;
import Chess.PGN.PGN;
import Communication.CommunicationListener;
import Communication.CommunicationManager;
import Communication.Event;
import Communication.SerialHelper;
import Core.EventManager;
import Core.Manager;
import Core.SettingManager;
import Debug.Debugger;
import GUI.ChessBoardMain;

public class ChessBoardManager extends Manager implements CommunicationListener {

	Board board;
	GameManager sm;
	ChessBoardMain frame;

	CommunicationManager communication;
	EventManager eventManager;

	SerialHelper serial;
	
	// Flags
	boolean closeApp;

	public ChessBoardManager() {
		super();
	}

	@Override
	public void createComponents() {

		eventManager = new EventManager(this); 
		addComponent(eventManager);
		addComponent(new SettingManager(this, "settings.", "Electronic Chess Board settings"));

		communication = new CommunicationManager(this);
		addComponent(communication);

		// Create board
		board = new Board();
		board.reset();

		// Game
		addComponent(new GameManager(this, board));

		// Frame
		frame = new ChessBoardMain();
		frame.boardPanel.setBoard(board);

		addComponent(new Debugger(this, frame));
		addComponent(new GameArchive(this, board));

		// Communication
		serial = new SerialHelper();
	}

	@Override
	protected void initialize() {

		super.initialize();
		initEvents();
		
		EventManager eventManager = (EventManager)getComponent(EventManager.EventManagerId);
		eventManager.addListener(this);
	}

	@Override
	public void processEvent(Event event) {
	}

	@Override
	public void portChanged(String portName) {

		frame.port_combo.removeAllItems();
		frame.port_combo.addItem(portName);
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

		communication.stop();
	}

	public void stop() {
		closeApp = true;
	}

	public void initEvents() {

		// Board
		board.addBoardEventListener(new BoardEventListener() {
			public void boardMove() {
				doBoardUpdate();
			}

			public void boardReset() {
				doBoardUpdate();
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});

		// Copy
		frame.copy_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				copyToClipboard();
			}
		});

		// COM Port
		frame.port_combo.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				fillPorts();
			}
		});
		frame.port_combo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					communication.changePort((String) item);
				}
			}
		});
	}

	private void fillPorts() {
		frame.port_combo.removeAllItems();
		List<String> ports = serial.getPorts();
		for (String port : ports)
			frame.port_combo.addItem(port);
	}

	public void copyToClipboard() {
		PGN pgn = new PGN(board);
		StringSelection contents = new StringSelection(pgn.exportMoves());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
	}

	public void doBoardUpdate() {
		frame.boardPanel.repaint();

		PGN pgn = new PGN(board);
		frame.notation_text.setText(pgn.exportMovesLine());
	}
}