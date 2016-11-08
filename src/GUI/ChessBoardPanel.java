package GUI;

import java.awt.*;
import java.awt.Font;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.*;
import javax.swing.*;
import com.kitfox.svg.*;
import com.kitfox.svg.app.beans.*;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Type;
import Chess.Position;

public class ChessBoardPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7125947909580273364L;
	private Board board;
	private SVGIcon helper;
	private Map<String, URI> sprites;
	private List<ChessBoardPanelExtender> extenders;
	int cellSize;
	int boardSize;

	public ChessBoardPanel() {

		extenders = new ArrayList<ChessBoardPanelExtender>();

		sprites = new HashMap<String, URI>();
		loadSprites(Piece.Color.White);
		loadSprites(Piece.Color.Black);

		helper = new SVGIcon();
		helper.setAntiAlias(true);
		helper.setScaleToFit(true);

		setPreferredSize(new Dimension(800, 400));
	}

	private String getPieceStr(Piece.Type type, Piece.Color color) {
		return type.toString() + color.toString();
	}

	private String getPieceStr(Piece piece) {
		return getPieceStr(piece.getType(), piece.getColor());
	}

	private void loadSprites(Piece.Color color) {
		for (Type t : Type.values()) {
			String key = getPieceStr(t, color);

			String name = "/Resources/sprites/" + key + ".svg";
			InputStream stream = ChessBoardPanel.class.getResourceAsStream(name);
			URI uri;
			try {
				uri = SVGCache.getSVGUniverse().loadSVG(stream, key);
				sprites.put(key, uri);
			} catch (IOException e) {
			}
		}
	}

	private void refreshExtenders() {
		for (ChessBoardPanelExtender extender : extenders)
			extender.refresh();
	}

	int calcWidth;
	int calcHeight;
	int fontSize;
	int symbolHeight;
	int symbolWidth;
	
	int LETTER_SIZE = 4;
	
	private void recalculate(int width, int height, Graphics g) {

		calcWidth = width;
		calcHeight = height;

		cellSize = Math.min(width, height) / 8;
		boardSize = 8 * cellSize;
		
		Font font = g.getFont();
		fontSize = 120;
		while (true) {

			Font f = new Font(font.getFontName(), Font.PLAIN, fontSize);
			FontMetrics metrics = g.getFontMetrics(f);
			double c = 1.0 / LETTER_SIZE;
			if (metrics.getAscent() < cellSize * c){
				symbolHeight = metrics.getAscent();
				symbolWidth = metrics.stringWidth("i");
				break;
			}
			fontSize--;
		}

	}
	
	private Color getCellColor(int x, int y, Color color) {
		Color result = color;
		for (ChessBoardPanelExtender extender : extenders)
			if (extender.needProcessSquare(x, y))
				result = extender.getColor(result);
		return result;
	}

	
	private Color getColor(int row, int column){
		Color black = new Color(181, 136, 99);// (139, 69, 19);//(50, 200, 200);
		Color white = new Color(240, 217, 181);// (255, 255, 255);
		
		if ((row + column) % 2 == 0)
			return black;
		else
			return white;		
	}
	
	private void drawLetters(Graphics g, int x, int y, int cellSize) {

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		Font font = new Font(g.getFont().getFontName(), Font.PLAIN, fontSize);
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		
		int left = x + symbolWidth / 2;
		int top = y + cellSize * 7 + symbolHeight;
		for (int row = 0; row < 8; row++) {
			Color color = getColor(1, row);
			g.setColor(color);
			String s = Integer.toString(row + 1); 
			g.drawString(s, left, top - row * cellSize);
		}
		
		left = x + cellSize - symbolWidth / 2;
		top = y + cellSize * 8 - symbolHeight / 4;
		for (int column = 0; column < 8; column++) {
			Color color = getColor(column, 1);
			g.setColor(color);
			String s = Character.toString((char)('a' + column)); 
			g.drawString(s, left - metrics.stringWidth(s) + column * cellSize, top);
		}
			
			
	}
	
	private void drawBoard(Graphics g, int x, int y, int cellSize) {

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				Color currentColor = getColor(j, 7 - i);
				currentColor = getCellColor(j, i, currentColor);
				g.setColor(currentColor);
				g.fillRect(x + j * cellSize, y + i * cellSize, cellSize, cellSize);
			}
		
		drawLetters(g, x, y, cellSize);
	}

	private void drawPieces(Graphics g, int x, int y, int cellSize) {

		if (board == null)
			return;

		int pieceSize = (int) (0.9 * cellSize);
		int delta = (cellSize - pieceSize) / 2;
		helper.setPreferredSize(new Dimension(pieceSize, pieceSize));

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				Piece piece = board.getPiece(new Position(j, 7 - i));
				if (piece != null) {
					helper.setSvgURI(sprites.get(getPieceStr(piece)));
					helper.paintIcon(this, g, x + delta + j * cellSize, y + i * cellSize + cellSize - pieceSize);// + delta);
				}
			}
	}

	public void setBoard(Board board) {
		this.board = board;
		repaint();
	}

	public Board getBoard() {
		return board;
	}

	public void addExtender(ChessBoardPanelExtender extender) {
		extender.setOwner(this);
		extenders.add(extender);
	}

	public void removeExtender(ChessBoardPanelExtender extender) {
		extender.setOwner(null);
		extenders.remove(extender);
	}

	public void paintComponent(Graphics g) {

		final int width = getWidth();
		final int height = getHeight();

		if ((width != calcWidth) || (height != calcHeight))
			recalculate(width, height, g);
		
		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		int x = (width - boardSize) / 2;
		int y = (height - boardSize) / 2;

		refreshExtenders();
		drawBoard(g, x, y, cellSize);
		drawPieces(g, x, y, cellSize);
	}

}
