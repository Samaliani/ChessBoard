package GUI;

import java.awt.Color;

public class ChessBoardPanelExtender {

	ChessBoardPanel panel;
	
	protected void update(){
		if (panel != null)
			panel.repaint();
	}
	
	public void setOwner(ChessBoardPanel panel){
		this.panel = panel;
	}

	public void refresh(){
	}
	
	public boolean needProcessSquare(int x, int y) {
		return false;
	}

	public Color getColor(Color color) {
		return color;
	}

}
