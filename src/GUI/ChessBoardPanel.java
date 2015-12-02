package GUI;

import java.awt.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import javax.swing.*;
import com.kitfox.svg.*;
import com.kitfox.svg.app.beans.*;

import Chess.Board;
import Chess.Piece;
import Chess.Piece.Type;
import Chess.Position;
import LowLevel.BoardData;

public class ChessBoardPanel extends JPanel {
	public static final long serialVersionUID = 0;

	private Board board;
	private BoardData data;
	private SVGIcon helper;
	private Map<String, URI> sprites;

	public ChessBoardPanel() {

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
			InputStream stream = ChessBoardPanel.class
					.getResourceAsStream(name);
			URI uri;
			try {
				uri = SVGCache.getSVGUniverse().loadSVG(stream, key);
				sprites.put(key, uri);
			} catch (IOException e) {
			}
		}
	}

	private boolean isMarkError(int x, int y) {
		if (data == null)
			return false;

		boolean piece = (board.getPiece(new Position(x, 7 - y)) != null);
		boolean place = (((data.getData()[7 - y]) & (1 << (7 - x))) != 0);
		return place != piece;
	}
	
	
	private Color getMarkColor(Color color)
	{
		float[] rgb = new float[3];
		rgb = color.getRGBColorComponents(rgb);
	
		float sum = 0;
		for(float value : rgb)
			sum += value;
		
		float ampC = (float)2.0;
		float red = rgb[0] * ampC;
		rgb[0] = red;
		if (red > 1)
			for(int i = 0; i < rgb.length; i++)
				rgb[i] /= red;

		float sum2 = rgb[1] + rgb[2];
		rgb[1] *= (sum - rgb[0]) / sum2;
		rgb[2] *= (sum - rgb[0]) / sum2;
		return new Color(rgb[0], rgb[1], rgb[2]);		
	}

	private void drawBoard(Graphics g, int x, int y, int cellSize) {

		Color black = new Color(181, 136, 99);// (139, 69, 19);//(50, 200, 200);
		Color white = new Color(240, 217, 181);// (255, 255, 255);

		Color currentColor;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				if ((i + j) % 2 == 0)
					currentColor = white;
				else
					currentColor = black;

				if (isMarkError(j, i))
					currentColor = getMarkColor(currentColor);

				g.setColor(currentColor);
				g.fillRect(x + j * cellSize, y + i * cellSize, cellSize,
						cellSize);
			}
	}

	private void drawPieces(Graphics g, int x, int y, int cellSize) {

		if (board == null)
			return;

		int pieceSize = (int) (0.95 * cellSize);
		int delta = (cellSize - pieceSize) / 2;
		helper.setPreferredSize(new Dimension(pieceSize, pieceSize));

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++) {
				Piece piece = board.getPiece(new Position(j, 7 - i));
				if (piece != null) {
					helper.setSvgURI(sprites.get(getPieceStr(piece)));
					helper.paintIcon(this, g, x + delta + j * cellSize, y
							+ (i + 1) * cellSize - pieceSize);
				}
			}
	}

	public void setBoard(Board board) {
		this.board = board;
		revalidate();
	}

	public void setData(BoardData data) {
		this.data = data;
		revalidate();
	}

	public void paintComponent(Graphics g) {

		final int width = getWidth();
		final int height = getHeight();

		g.fillRect(0, 0, width, height);
		
		int cellSize = Math.min(width, height) / 8;
		int boardSize = 8 * cellSize;

		int x = (width - boardSize) / 2;
		int y = (height - boardSize) / 2;

		drawBoard(g, x, y, cellSize);
		drawPieces(g, x, y, cellSize);
	}
}
