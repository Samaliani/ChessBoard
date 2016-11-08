package Debug;

import java.awt.Color;

import Core.Manager;
import GUI.ChessBoardPanelExtender;
import Game.GameManager;
import LowLevel.BoardData;
import LowLevel.Utils;

public class DebugBoardExtender extends ChessBoardPanelExtender {

	Manager manager;
	BoardData eventData;
	BoardData boardData;

	public DebugBoardExtender(Manager manager) {
		this.manager = manager;
	}

	public boolean needProcessSquare(int x, int y) {
		if (eventData == null)
			return false;		
		return ((boardData.getData()[7 - y]) & (1 << (7 - x))) != ((eventData.getData()[7 - y]) & (1 << (7 - x)));
	}

	@Override
	public Color getColor(Color color) {

		double alpha = 0.5;
		float[] rgb = new float[3];
		rgb = color.getRGBColorComponents(rgb);

		double maskAlpha = 0.05;
		float[] mask = { (float) 0.75, 0, 0 };

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
		super.refresh();
		GameManager gameManager = (GameManager) manager.getComponent(GameManager.GameManagerId);
		boardData = Utils.getBoardData(gameManager.getBoard());                                           
	}

	public void setEventData(BoardData data) {
		eventData = data;
		update();
	}

}
