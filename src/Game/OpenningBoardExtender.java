package Game;

import java.awt.Color;

import GUI.ChessBoardPanelExtender;
import LowLevel.BoardData;

public class OpenningBoardExtender extends ChessBoardPanelExtender {

	//Manager manager;
	BoardData data;

	public OpenningBoardExtender() {
		//this.manager = manager;
	}

	public boolean needProcessSquare(int x, int y) {
		if (data == null)
			return false;
		return (((data.getData()[7 - y]) & (1 << (7 - x))) != 0);
	}

	@Override
	public Color getColor(Color color) {

		double alpha = 0.95;
		float[] rgb = new float[3];
		rgb = color.getRGBColorComponents(rgb);

		double maskAlpha = 0.05;
		float[] mask = { 0, 0, (float) 0.75 };

		for (int i = 0; i < 3; i++) {
			double value = rgb[i] * alpha * (1 - maskAlpha + mask[i] * alpha)
					* (1 / (1 - (1 - alpha) * (1 - maskAlpha)));
			if (value > 1.0)
				value = 1.0;
			rgb[i] = (float) value;
		}

		return new Color(rgb[0], rgb[1], rgb[2]);
	}

	@Override
	public void refresh() {
	}

	public void setData(BoardData data) {
		this.data = data;
		update();
	}


}
