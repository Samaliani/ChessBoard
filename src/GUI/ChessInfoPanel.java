package GUI;

import java.awt.Graphics;
import java.time.LocalDateTime;

public class ChessInfoPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5494424114786381693L;

	public enum Mode {
		Time, Result
	};

	Mode mode;
	LocalDateTime time1;
	LocalDateTime time2;
	String result;
	
	public void paintComponent(Graphics g) {

		final int width = getWidth();
		final int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		if (mode == Mode.Time)
			;
		else
			;
	}
}
