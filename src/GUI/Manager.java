package GUI;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
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
import Communication.FileCommunication;
import Communication.SerialCommunication;
import Communication.SerialHelper;
import LowLevel.StateMachine;

public class Manager {

	ChessBoardMain frame;
	Properties preferences;

	Board board;
	BoardCommunication communication;
	StateMachine sm;

	String portName;

	boolean closeApp;

	public Manager() {

		// Create board
		board = new Board();
		board.reset();

		// Init settings
		preferences = new Properties(getDefaultPreferences());
		loadPreferences();

		// Init engine
		sm = new StateMachine(board);

		communication = new SerialCommunication(new CommunicationListener() {
			public void processEvent(Communication.Event event) {
				sm.processEvent(event);
			}
		});
		((SerialCommunication) communication).setPortName(portName);

		// Debug
		String fileName = ".\\Data\\openning5.txt";
		communication = new FileCommunication(new CommunicationListener() {
			public void processEvent(Communication.Event event) {
				sm.processEvent(event);
			}
		}, fileName);

	}

	public void setFrame(ChessBoardMain frame) {
		this.frame = frame;

		frame.boardPanel.setBoard(board);

		// apply preferences to UI
		frame.port_combo.addItem(portName);
		frame.port_combo.setSelectedItem(portName);

		initEvents();
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

		portName = preferences.getProperty("Communication.Port");
	}

	private void savePreferences() {

		preferences.setProperty("Communication.Port", portName);
		
		try {
			FileWriter writer = new FileWriter("settings.");
			preferences.store(writer, "Electronic Chess Board settings");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Thread eventThread;

	public void run() {
		// Стартануть коммуникацию
		eventThread = new Thread(communication);
		eventThread.setDaemon(true);
		eventThread.start();

		while (!closeApp)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		communication.stop();
		savePreferences();
	}

	public void stop() {
		closeApp = true;
	}

	public void initEvents() {

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				stop();
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

	public void fillPorts() {
		frame.port_combo.removeAllItems();
		List<String> ports = SerialHelper.getPortsAvailable();
		for (String port : ports)
			frame.port_combo.addItem(port);
	}

	public void changePort(String portName) {
	//	((SerialCommunication) communication).setPortName(portName);
	}

	public void copyToClipboard() {
		PGN pgn = new PGN(board);
		StringSelection contents = new StringSelection(pgn.exportMoves());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
	}

	public void doBoardUpdate() {
		frame.boardPanel.repaint();

		PGN pgn = new PGN(board);
		frame.notation_text.setText(pgn.exportMoves());
	}

	public void doBoardMove() {
		frame.boardPanel.repaint();

		PGN pgn = new PGN(board);
		frame.notation_text.setText(pgn.exportMoves());
	}

}
