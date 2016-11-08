package GUI;

import java.awt.Color;
import java.awt.Graphics;

import Engine.AnalysisInfo;

public class ChessInfoPanel extends javax.swing.JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5494424114786381693L;

	AnalysisInfo info;
	String text;
	double value;

	public enum Mode {
		Time, Result
	};

	static int PANEL_WIDTH = 50;
	static double SCALE = 5.0;
	

	public ChessInfoPanel(){
		text = "";
		value = 0;
	}
	
	public void paintComponent(Graphics g) {

		final int width = getWidth();
		final int height = getHeight();

		g.setColor(getBackground());
		g.fillRect(0, 0, width, height);

		g.setColor(Color.WHITE);
		g.fillRect((width - PANEL_WIDTH) / 2, 0, (width + PANEL_WIDTH) / 2, height);
		
		g.setColor(Color.BLACK);
		g.drawString(text, 0, height / 2);
		//g.setColor(Color.BLACK);
		

	}

	public void setAnalysisInfo(AnalysisInfo info) {
		this.info = info;

		if (info.hasScore()) {
			if (info.getMate() != -1){
				text = String.format("# %d", info.getMate());
				value = 0; 
			}
			else{
				value = info.getScore() / 100.0; 
				text = String.format("%s", value);
			}
		}
		else
		{
			text = "...";
			value = 0;
					
		}
	}
}
