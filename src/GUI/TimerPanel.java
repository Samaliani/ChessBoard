package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.time.Duration;

public class TimerPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5494424114786381693L;

	Duration white;
	Duration black;
	boolean active;

	int calcWidth;
	int calcHeight;
	int fontSize;

	public TimerPanel() {
		white = Duration.ofSeconds(0);
		black = Duration.ofSeconds(0);
	}

	public void setWhiteTime(Duration time) {
		white = time;
		repaint();
	}

	public void setBlackTime(Duration time) {
		black = time;
		repaint();
	}
	
	public void modeChanged(){
		calcWidth = -1;
		calcHeight = -1;
	}
	
	private String getTimeText(Duration time) {
		if (time.toHours() != 0)
			return String.format("%d:%02d:%02d", time.toHours(), time.toMinutes() % 60, time.getSeconds() % 60);
		else if (time.getSeconds() > 10)
			return String.format("%d:%02d", time.toMinutes(), time.getSeconds() % 60);
		else
			return String.format("%d.%d", time.getSeconds(), (time.toMillis() % 1000) / 100);
	}

	public void paintComponent(Graphics g) {

		int width = getWidth();
		int height = getHeight();

		if ((width != calcWidth) || (height != calcHeight))
			recalculate(width, height, g);

		g.setColor(Color.white);
		g.fillRect(0, 0, width / 2, height);
		g.setColor(Color.black);
		g.fillRect(width / 2, 0, width, height);

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		Font font = new Font(g.getFont().getFontName(), Font.PLAIN, fontSize);
		g.setFont(font);
		drawText(g, Color.black, getTimeText(white), new Rectangle(0, 0, width / 2, height));
		drawText(g, Color.white, getTimeText(black), new Rectangle(width / 2, 0, width / 2, height));
	}

	private void drawText(Graphics g, Color color, String text, Rectangle rectangle) {

		g.setColor(color);

		FontMetrics metrics = g.getFontMetrics();
		int textHeight = metrics.getAscent() - metrics.getDescent();
		int x = (rectangle.width - (int) metrics.stringWidth(text)) / 2;
		int y = rectangle.height / 2 + textHeight / 2;

		g.drawString(text, rectangle.x + x, rectangle.y + y);
	}

	private void recalculate(int width, int height, Graphics g) {
		calcWidth = width;
		calcHeight = height;

		Font font = g.getFont();
		String time1 = getTimeText(white);
		String time2 = getTimeText(black);

		fontSize = 120;
		while (true) {

			Font f = new Font(font.getFontName(), Font.PLAIN, fontSize);
			FontMetrics metrics = g.getFontMetrics(f);
			int textWidth = Math.max(metrics.stringWidth(time1), metrics.stringWidth(time2));
			int textHeight = metrics.getAscent();// - metrics.getDescent();

			double c = 0.9;
			if ((textHeight < height * c) && (textWidth < (width * c) / 2))
				break;
			fontSize--;
		}

	}
}
