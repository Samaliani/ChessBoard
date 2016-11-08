package GUI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JComboBox;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
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

import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import App.ChessBoardManager;
import Chess.Game;
import Chess.GameResult;
import Chess.Piece;
import Comment.CommentListener;
import Communication.CommunicationListener;
import Communication.CommunicationManager;
import Communication.Event;
import Communication.SerialHelper;
import Core.EventManager;
import Engine.AnalysisInfo;
import Engine.ChessEngineListener;
import Engine.ChessEngineManager;
import Game.GameEventListener;
import Game.GameManager;
import Game.GameModel;
import Game.GameModelListener;
import Game.GameModelManager;
import Game.ictkUtils;

import Timer.TimerListener;
import Timer.TimerManager;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class ChessBoardMain extends javax.swing.JFrame implements CommunicationListener, GameEventListener,
		GameModelListener, TimerListener, ChessEngineListener, CommentListener {

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
	JLabel score_label;

	final JTextPane notation_text = new JTextPane();
	final JTextPane comment_text = new JTextPane();
	final JPanel comment_panel = new JPanel();
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
		addModeDropDownButton(1);
		addModeButton(2);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.7);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel chessPanel = new JPanel();
		chessPanel.setLayout(new BorderLayout(0, 0));
		splitPane.setLeftComponent(chessPanel);

		boardPanel.setPreferredSize(new Dimension(400, 400));
		chessPanel.add(boardPanel, BorderLayout.CENTER);

		comment_panel.setLayout(new BorderLayout(0, 0));
		chessPanel.add(comment_panel, BorderLayout.SOUTH);
		comment_panel.setVisible(false);

		JLabel comment_label = new JLabel("\u041A\u043E\u043C\u0435\u043D\u0442\u0430\u0440\u0438\u0439:");
		comment_label.setLabelFor(comment_text);
		comment_label.setHorizontalAlignment(SwingConstants.CENTER);
		comment_panel.add(comment_label, BorderLayout.NORTH);

		JScrollPane commentScrollPane = new JScrollPane(comment_text);
		commentScrollPane.setPreferredSize(new Dimension(400, 100));
		commentScrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		commentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		commentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		comment_panel.add(commentScrollPane, BorderLayout.CENTER);

		comment_text.setPreferredSize(new Dimension(400, 100));
		comment_text.setEditable(false);
		Style commentStyle = comment_text.addStyle("default", null);
		StyleConstants.setFontSize(commentStyle, 16);
		//comment_panel.add(comment_text, BorderLayout.CENTER);

		JPanel tool_panel = new JPanel();
		tool_panel.setPreferredSize(new Dimension(300, 10));
		splitPane.setRightComponent(tool_panel);
		// getContentPane().add(tool_panel, BorderLayout.EAST);

		GridBagLayout gbl_tool_panel = new GridBagLayout();
		gbl_tool_panel.columnWidths = new int[] { 150, 150 };
		gbl_tool_panel.rowHeights = new int[] { 30, 100, 100, 30, 100 };
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
		gbc_restul_panel.weighty = 1.0;
		gbc_restul_panel.gridwidth = 2;
		gbc_restul_panel.insets = new Insets(0, 0, 5, 0);
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
		gbc_notation_panel.weighty = 1.0;
		gbc_notation_panel.fill = GridBagConstraints.BOTH;
		gbc_notation_panel.gridwidth = 2;
		gbc_notation_panel.insets = new Insets(0, 0, 5, 0);
		gbc_notation_panel.gridx = 0;
		gbc_notation_panel.gridy = 2;
		tool_panel.add(notation_panel, gbc_notation_panel);
		notation_panel.setLayout(new BorderLayout(0, 0));

		JLabel notation_label = new JLabel("\u0417\u0430\u043F\u0438\u0441\u044C:");
		notation_label.setLabelFor(notation_text);
		notation_label.setHorizontalAlignment(SwingConstants.CENTER);
		notation_panel.add(notation_label, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(notation_text);
		scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		notation_panel.add(scrollPane, BorderLayout.CENTER);

		// Default style to notation
		notation_text.setEditable(false);
		Style defaultStyle = notation_text.addStyle("default", null);
		StyleConstants.setFontSize(defaultStyle, 16);

		/*
		 * JTable table = new JTable();
		 * table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		 * table.setModel(new DefaultTableModel(new Object[][] {}, new String[]
		 * { "Moves", "N" })); table.setShowHorizontalLines(false);
		 * table.setShowGrid(false); table.setRowSelectionAllowed(false);
		 * notation_panel.add(table, BorderLayout.SOUTH);
		 */

		score_label = new JLabel("");
		score_label.setFont(new Font("Tahoma", Font.BOLD, 18));
		score_label.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_score_label = new GridBagConstraints();
		gbc_score_label.insets = new Insets(0, 0, 5, 5);
		gbc_score_label.gridx = 0;
		gbc_score_label.gridy = 3;
		tool_panel.add(score_label, gbc_score_label);

		copy_button = new JButton(
				"\u0421\u043A\u043E\u043F\u0438\u0440\u043E\u0432\u0430\u0442\u044C \u0432 \u0431\u0443\u0444\u0444\u0435\u0440");
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

	private JPopupMenu createModePopupMenu(GameModel model) {

		JPopupMenu popupMenu = new JPopupMenu();
		for (int i = 0; i < model.getModeCount(); i++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(model.getModeName(i));
			item.setState(model.getActiveMode() == i);
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JMenuItem item = (JMenuItem) (arg0.getSource());

					GameModelManager modelManager = (GameModelManager) manager.getComponent(GameModelManager.id);
					GameModel activeModel = modelManager.getCurrentModel();
					for (int i = 0; i < activeModel.getModeCount(); i++)
						if (item.getText().equals(activeModel.getModeName(i)))
							activeModel.setActiveMode(i);

					item.getParent().setVisible(false);
				}
			});
			popupMenu.add(item);
		}
		return popupMenu;
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

	private void addModeDropDownButton(int modeIndex) {

		JToggleButton button = new JToggleButton();
		button.setPreferredSize(new Dimension(100, 23));

		GameModelManager modelManager = (GameModelManager) manager.getComponent(GameModelManager.id);
		button.setText(modelManager.getModelName(modeIndex));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JToggleButton button = (JToggleButton) arg0.getSource();

				GameModelManager modelManager = (GameModelManager) manager.getComponent(GameModelManager.id);
				modelManager.setActiveModel(modeButtons.indexOf(button));

				GameModel activeModel = modelManager.getCurrentModel();
				if (activeModel.getModeCount() > 0) {
					Point location = button.getLocationOnScreen();

					JPopupMenu menu = createModePopupMenu(activeModel);
					menu.setLocation(location.x, location.y + button.getHeight());
					menu.setInvoker(button);
					menu.setVisible(true);
				}
			}
		});

		modeButtons.add(button);
		toolBar.add(button);
	}

	private void addGameResultButton(JPanel panel, String text) {
		JButton button = new JButton(text);
		// button.setPreferredSize(new Dimension(100, 23));

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GameManager gameManager = (GameManager) manager.getComponent(GameManager.GameManagerId);
				GameResult result = GameResult.Unknown;
				switch (resultButtons.indexOf(arg0.getSource())) {
				case 0:
					result = GameResult.White;
					break;
				case 2:
					result = GameResult.Black;
					break;
				case 1:
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
		for (JToggleButton button : modeButtons)
			button.setSelected(false);
		modeButtons.get(modelManager.getActiveModel()).setSelected(true);
	}

	private Object notation_lock = new Object();

	private void updateInfoPanel(AnalysisInfo info) {

		ChessEngineManager engineManager = (ChessEngineManager) manager.getComponent(ChessEngineManager.Id);
		score_label.setVisible(engineManager.useEngine());

		synchronized (notation_lock) {

			doNotationUpdate(game);

			if (info == null) {
				score_label.setText("");
				return;
			}
			if (game.getTurnColor() == Piece.Color.Black)
				info.invertScore();
			score_label.setText(info.toString());

			String line = ictkUtils.transformNAG(info.getPosition(), info.getLine());
			addColorText(notation_text, new Color(50, 150, 50), String.format("( %s)", line));
		}
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
	public void rollbackMove(Game game) {

		doNotationUpdate(game);
		doBoardUpdate();
	}

	@Override
	public void endGame(Game game) {
		doUpdateResult(game.getResult());
		updateInfoPanel(null);
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

	@Override
	public void analysisInfo(AnalysisInfo info) {
		updateInfoPanel(info);
	}

	private void updateEnabled(boolean enable) {

		cmbTimeControl.setEnabled(enable);

		for (JToggleButton button : modeButtons)
			button.setEnabled(enable);

		modeButtons.get(2).setEnabled(false);

		for (JButton button : resultButtons)
			button.setEnabled(!enable);
	}

	public void initialize() {

		initEvents();
		timerModeChanged();
		updateInfoPanel(null);
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

	private void addColorText(JTextPane edit, Color color, String text) {

		StyledDocument document = edit.getStyledDocument();

		Style style = document.getStyle(color.toString());
		if (style == null) {
			style = edit.addStyle(color.toString(), edit.getStyle("default"));
			StyleConstants.setForeground(style, color);
		}

		try {
			document.insertString(document.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void doNotationUpdate(Game game) {

		notation_text.setText("");
		if (game != null)
			addColorText(notation_text, Color.BLACK, game.exportMoveNotation());
	}

	private void doUpdateResult(GameResult result) {

		lblResult.setText(result.toString());
		switch (result) {
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

	@Override
	public void addComment(String commentText) {

		comment_text.setText("");
		addColorText(comment_text, Color.BLACK, commentText);
		comment_panel.setVisible(true);
	}

	@Override
	public void removeComment() {
		comment_text.setText("");
	}

}