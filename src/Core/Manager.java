package Core;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import Chess.Board;
import Chess.BoardEventListener;
import Chess.PGN.PGN;
import Communication.BoardCommunication;
import Communication.CommunicationListener;
import Communication.EmptyCommunication;
import Communication.FileCommunication;
import Communication.SerialCommunication;
import Communication.SerialHelper;
import GUI.ChessBoardMain;
import LowLevel.BoardData;
import LowLevel.StateMachine;
import LowLevel.StateMachineEventListener;

public class Manager {

	ChessBoardMain frame;
	Properties preferences;

	Board board;
	SerialHelper serial;
	StateMachine sm;

	BoardCommunication communication;
	DebugOutputListener output;

	// Flags
	boolean fileInput;
	boolean closeApp;

	// Debug parameters
	boolean dataDebug;
	String debugPath;
	boolean visualDebug;

	public Manager() {
		createComponents();
	}

	public void createComponents() {

		// Create board
		board = new Board();
		board.reset();

		// Game
		sm = new StateMachine(board);

		// Frame
		frame = new ChessBoardMain();
		frame.boardPanel.setBoard(board);

		// Communication
		serial = new SerialHelper();
	}

	private void initialize() {
		// Init settings
		preferences = new Properties(getDefaultPreferences());
		loadPreferences();

		if (dataDebug)
			output = new DebugOutputListener(debugPath);

		initEvents();
		initCommunicationEvents();
		initFrameEvents();
	}

	public ChessBoardMain getFrame() {
		return frame;
	}

	private Properties getDefaultPreferences() {
		Properties result = new Properties();
		result.setProperty("Communication.Port", "");

		return result;
	}

	private void loadPreferences() {

		try {
			FileReader reader = new FileReader("settings.");
			preferences.load(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		dataDebug = Boolean.parseBoolean(preferences.getProperty("Debug.Output", "false"));
		debugPath = preferences.getProperty("Debug.Path", System.getProperty("user.dir") + "/debug");
		visualDebug = Boolean.parseBoolean(preferences.getProperty("Debug.Positions", "false"));
		
		String portName = preferences.getProperty("Communication.Port");
		if (portName.equals("FILE")) {
			fileInput = true;
			String fileName = preferences.getProperty("Communication.File");
			communication = new FileCommunication(fileName);
		} else {
			if (changePort(portName))
				frame.port_combo.addItem(communication.getPortName());
		}
	}

	private void savePreferences() {

		preferences.setProperty("Communication.Port", communication.getPortName());

		try {
			FileWriter writer = new FileWriter("settings.");
			preferences.store(writer, "Electronic Chess Board settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Thread eventThread;

	public void run() {

		initialize();

		// ���������� ������������
		eventThread = new Thread(communication);
		eventThread.setDaemon(true);
		eventThread.start();

		while (!closeApp)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		communication.stop();
		savePreferences();
	}

	public void stop() {
		closeApp = true;
	}

	public void initEvents() {

		// StateMaching
		sm.addEventListener(new StateMachineEventListener() {
			protected void gameReset() {
				board.reset();
				if (dataDebug)
					output.reset();
			}
		});

		// Board
		board.addBoardEventListener(new BoardEventListener() {
			public void boardMove() {
				doBoardMove();
			}

			public void boardChanged() {
				doBoardUpdate();
			}
		});

	}

	private void initCommunicationEvents() {

		communication.removeAllListeners();
		
		if (dataDebug)
			communication.addListener(output);

		if (visualDebug)
			communication.addListener(new CommunicationListener() {
				public void processEvent(Communication.Event event) {
					if ((frame != null) && (event.getData() != null)) {
						frame.boardPanel.setData(event.getData());
						frame.boardPanel.repaint();
					}
				}
			});

		communication.addListener(new CommunicationListener() {
			public void processEvent(Communication.Event event) {
				sm.processEvent(event);
			}
		});
	}

	private void initFrameEvents() {

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
					changePort((String) item);
					// do something with object
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

	public boolean changePort(String portName) {

		if (fileInput)
			return false;

		if (communication != null) {
			communication.stop();
			communication = null;
		}

		if (portName.length() != 0) {
			try {
				communication = new SerialCommunication(portName);
				initCommunicationEvents();
				return true;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage());
			}
		}

		communication = new EmptyCommunication(portName);
		return false;
	}

	public void copyToClipboard() {
		PGN pgn = new PGN(board);
		StringSelection contents = new StringSelection(pgn.exportMoves());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
	}

	public void doBoardUpdate() {
		if (frame == null)
			return;

		frame.boardPanel.repaint();

		PGN pgn = new PGN(board);
		frame.notation_text.setText(pgn.exportMoves());
	}

	public void doBoardMove() {
		if (frame == null)
			return;

		frame.boardPanel.repaint();

		PGN pgn = new PGN(board);
		frame.notation_text.setText(pgn.exportMoves());
	}

}
