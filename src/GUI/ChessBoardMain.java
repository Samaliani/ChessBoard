package GUI;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import App.ChessBoardManager;
import Chess.Game;
import Chess.GameResult;
import Communication.CommunicationListener;
import Communication.CommunicationManager;
import Communication.Event;
import Communication.SerialHelper;
import Core.EventManager;
import Game.GameEventListener;
import Game.GameManager;
import Game.GameModelListener;
import Game.GameModelManager;

import javax.swing.ListSelectionModel;

import Timer.TimerListener;
import Timer.TimerManager;
import java.awt.SystemColor;
import java.awt.Font;

public class ChessBoardMain extends javax.swing.JFrame
		implements CommunicationListener, GameEventListener, GameModelListener, TimerListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5192615077164184682L;

	private final JPanel toolBar = new JPanel();

	private final JComboBox<String> cmbPort = new JComboBox<String>();
	private final JComboBox<String> cmbTimeControl = new JComboBox<String>();
	
	private final List<JToggleButton> modeButtons;
	private final List<JButton> resultButtons;

	public final ChessBoardPanel boardPanel = new ChessBoardPanel();

	TimerPanel timerPanel;
	JLabel lblResult;
	JLabel lblTextResult;
	
	final JTextPane notation_text = new JTextPane();
	final JButton copy_button;

	ChessBoardManager manager;

	Game game;

	public ChessBoardMain(ChessBoardManager manager) {

		this.manager = manager;

		setTitle("Chess Board");
		setMinimumSize(new Dimension(400, 300));
		initComponents();
		toolBar.setBackground(SystemColor.inactiveCaption);

		toolBar.setPreferredSize(new Dimension(this.getWidth(), 30));
		this.getContentPane().add(toolBar, BorderLayout.NORTH);
		toolBar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 3));

		// Connection
		JLabel lblConnection = new JLabel("Соединение:");
		toolBar.add(lblConnection);
		cmbPort.setPreferredSize(new Dimension(80, 23));
		toolBar.add(cmbPort);

		// TImer
		JLabel lblTimeControl = new JLabel("Контроль времени:");
		toolBar.add(lblTimeControl);
		cmbTimeControl.setPreferredSize(new Dimension(120, 23));
		toolBar.add(cmbTimeControl);
		fillTimerModes();

		// Modes
		modeButtons = new ArrayList<JToggleButton>();
		addModeButton(0);
		addModeButton(1);
		addModeButton(2);

		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		boardPanel.setPreferredSize(new Dimension(400, 400));
		splitPane.setLeftComponent(boardPanel);

		JPanel tool_panel = new JPanel();
		tool_panel.setPreferredSize(new Dimension(300, 10));
		splitPane.setRightComponent(tool_panel);
		//getContentPane().add(tool_panel, BorderLayout.EAST);
		
		GridBagLayout gbl_tool_panel = new GridBagLayout();
		gbl_tool_panel.columnWidths = new int[] { 150, 150 };
		gbl_tool_panel.rowHeights = new int[] {30, 100, 100, 30, 100};
		gbl_tool_panel.columnWeights = new double[] { 0.0, 1.0 };
		gbl_tool_panel.rowWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0 };
		tool_panel.setLayout(gbl_tool_panel);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		tool_panel.add(panel, gbc_panel);

		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		resultButtons = new ArrayList<JButton>();
		addGameResultButton(panel, App.Messages.GUI.WhiteWin);
		addGameResultButton(panel, App.Messages.GUI.Tie);
		addGameResultButton(panel, App.Messages.GUI.BlackWin);
		
		JPanel restul_panel = new JPanel();
		restul_panel.setBackground(SystemColor.info);
		GridBagConstraints gbc_restul_panel = new GridBagConstraints();
		gbc_restul_panel.gridwidth = 2;
		gbc_restul_panel.insets = new Insets(0, 0, 5, 5);
		gbc_restul_panel.fill = GridBagConstraints.BOTH;
		gbc_restul_panel.gridx = 0;
		gbc_restul_panel.gridy = 1;
		tool_panel.add(restul_panel, gbc_restul_panel);
		restul_panel.setLayout(new BorderLayout(0, 0));
		
		lblResult = new JLabel();
		lblResult.setFont(new Font("Tahoma", Font.PLAIN, 60));
		lblResult.setHorizontalAlignment(SwingConstants.CENTER);
		restul_panel.add(lblResult);
		
		lblTextResult = new JLabel();
		lblTextResult.setFont(new Font("Tahoma", Font.PLAIN, 24));
		lblTextResult.setHorizontalAlignment(SwingConstants.CENTER);
		restul_panel.add(lblTextResult, BorderLayout.SOUTH);

		JPanel notation_panel = new JPanel();
		GridBagConstraints gbc_notation_panel = new GridBagConstraints();
		gbc_notation_panel.gridwidth = 2;
		gbc_notation_panel.insets = new Insets(0, 0, 5, 0);
		gbc_notation_panel.fill = GridBagConstraints.BOTH;
		gbc_notation_panel.gridx = 0;
		gbc_notation_panel.gridy = 2;
		tool_panel.add(notation_panel, gbc_notation_panel);
		notation_panel.setLayout(new BorderLayout(0, 0));

		JLabel notation_label = new JLabel("Notation:");
		notation_label.setLabelFor(notation_text);
		notation_label.setHorizontalAlignment(SwingConstants.CENTER);
		notation_panel.add(notation_label, BorderLayout.NORTH);
		notation_text.setEditable(false);
		notation_panel.add(notation_text, BorderLayout.CENTER);

		JTable table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Moves", "N" }));
		table.setShowHorizontalLines(false);
		table.setShowGrid(false);
		table.setRowSelectionAllowed(false);
		notation_panel.add(table, BorderLayout.SOUTH);
				
						copy_button = new JButton("Copy");
						GridBagConstraints gbc_copy_button = new GridBagConstraints();
						gbc_copy_button.anchor = GridBagConstraints.LINE_END;
						gbc_copy_button.insets = new Insets(0, 0, 5, 0);
						gbc_copy_button.gridx = 1;
						gbc_copy_button.gridy = 3;
						tool_panel.add(copy_button, gbc_copy_button);
		
				timerPanel = new TimerPanel();
				timerPanel.setPreferredSize(new Dimension(300, 120));
				
						GridBagConstraints gbc_timer_panel = new GridBagConstraints();
						gbc_timer_panel.gridwidth = 2;
						gbc_timer_panel.gridx = 0;
						gbc_timer_panel.gridy = 4;
						gbc_timer_panel.fill = GridBagConstraints.BOTH;
						tool_panel.add(timerPanel, gbc_timer_panel);

		pack();
	}

	private void addModeButton(int modeIndex) {

		JToggleButton button = new JToggleButton();
		button.setPreferredSize(new Dimension(100, 23));
		
		GameModelManager modelManager = (GameModelManager) manager.getComponent(GameModelManager.id);
		button.setText(modelManager.getModelName(modeIndex));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GameModelManager modelManager = (GameModelManager) manager.getComponent(GameModelManager.id);
				modelManager.setActiveModel(modeButtons.indexOf(arg0.getSource()));
			}
		});

		modeButtons.add(button);
		toolBar.add(button);
	}
	
	private void addGameResultButton(JPanel panel, String text){
		JButton button = new JButton(text);
		//button.setPreferredSize(new Dimension(100, 23));
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GameManager gameManager = (GameManager) manager.getComponent(GameManager.GameManagerId);
				GameResult result = GameResult.Unknown;
				switch(resultButtons.indexOf(arg0.getSource())){
				case 0:
					result = GameResult.White;
					break;
				case 1:
					result = GameResult.Black;
					break;
				case 3:
					result = GameResult.Tie;
					break;
				}
				gameManager.finishGame(result);
			}
		});
		
		resultButtons.add(button);
		panel.add(button);
	}

	/**
	 * This method is called from within the constructor to * initialize the
	 * form. * WARNING: Do NOT modify this code. The content of this method is *
	 * always regenerated by the Form Editor.
	 */
	private void initComponents() {
		getContentPane().setLayout(new java.awt.BorderLayout());
	}

	private void updateActiveMode() {

		GameModelManager modelManager = (GameModelManager) manager.getComponent(GameModelManager.id);
		for(JToggleButton button : modeButtons)
			button.setSelected(false);
		modeButtons.get(modelManager.getActiveModel()).setSelected(true);
	}

	@Override
	public void processEvent(Event event) {
	}

	boolean isChangingPort;

	@Override
	public void portChanged(String portName) {
		if (isChangingPort)
			return;
		cmbPort.removeAllItems();
		cmbPort.addItem(portName);
	}

	@Override
	public void beforeGame(Game game) {
		boardPanel.setBoard(game.getBoard());
		doNotationUpdate(game);
		doBoardUpdate();
		doUpdateResult(game.getResult());
	}

	@Override
	public void startGame(Game game) {

		this.game = game;
		boardPanel.setBoard(game.getBoard());

		doNotationUpdate(game);
		doBoardUpdate();

		updateEnabled(false);
	}

	@Override
	public void makeMove(Game game) {

		doNotationUpdate(game);
		doBoardUpdate();
	}

	@Override
	public void endGame(Game game) {
		doUpdateResult(game.getResult());
		updateEnabled(true);
	}

	@Override
	public void timerChanged() {
		TimerManager timer = (TimerManager) manager.getComponent(TimerManager.TimerManagerId);
		timerPanel.setWhiteTime(timer.getWhiteTime());
		timerPanel.setBlackTime(timer.getBlackTime());
	}
	
	@Override
	public void timerModeChanged() {
		TimerManager timer = (TimerManager) manager.getComponent(TimerManager.TimerManagerId);

		timerPanel.setWhiteTime(timer.getWhiteTime());
		timerPanel.setBlackTime(timer.getBlackTime());
		timerPanel.modeChanged();

		cmbTimeControl.setSelectedIndex(timer.getActiveMode());
	}

	@Override
	public void gameModeChanged() {
		updateActiveMode();
	}
	
	private void updateEnabled(boolean enable) {
		
		cmbTimeControl.setEnabled(enable);
		
		for(JToggleButton button: modeButtons)
			button.setEnabled(enable);
		
		modeButtons.get(2).setEnabled(false);

		for(JButton button: resultButtons)
			button.setEnabled(!enable);
}

	public void initialize() {

		initEvents();
		timerModeChanged();
		updateActiveMode();
		updateEnabled(true);
	}

	public void initEvents() {

		EventManager eventManager = (EventManager) manager.getComponent(EventManager.EventManagerId);
		eventManager.addListener(this);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				manager.stop();
			}
		});

		// Copy
		copy_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				copyToClipboard();
			}
		});

		// COM Port
		cmbPort.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent arg0) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				fillPorts();
			}
		});
		cmbPort.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					CommunicationManager communication = (CommunicationManager) manager
							.getComponent(CommunicationManager.CommunicationManagerId);
					isChangingPort = true;
					communication.changePort((String) item);
					isChangingPort = false;
				}
			}
		});
		
		// Timer
		cmbTimeControl.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					TimerManager timer = (TimerManager) manager.getComponent(TimerManager.TimerManagerId);
					timer.setActiveMode(cmbTimeControl.getSelectedIndex());
				}
			}
		});
		
	}

	private void doNotationUpdate(Game game) {
		notation_text.setText(game.exportMoveNotation());
	}
	
	private void doUpdateResult(GameResult result){
		
		lblResult.setText(result.toString());
		switch(result)
		{
		case White:
			lblTextResult.setText(App.Messages.GUI.WhiteWin);
			break;
		case Black:
			lblTextResult.setText(App.Messages.GUI.BlackWin);
			break;
		case Tie:
			lblTextResult.setText(App.Messages.GUI.Tie);
			break;
		default:
			lblTextResult.setText("");
		}
	}

	private void doBoardUpdate() {
		boardPanel.repaint();
	}

	private void fillTimerModes() {
		TimerManager timerManager = (TimerManager) manager.getComponent(TimerManager.TimerManagerId);
		cmbTimeControl.removeAllItems();
		for (int i = 0; i < timerManager.getModeCount(); i++)
			cmbTimeControl.addItem(timerManager.getModeName(i));
	}

	private void fillPorts() {
		cmbPort.removeAllItems();
		List<String> ports = SerialHelper.getPorts();
		for (String port : ports)
			cmbPort.addItem(port);
	}

	public void copyToClipboard() {
		StringSelection contents = new StringSelection(notation_text.getText());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
	}

}