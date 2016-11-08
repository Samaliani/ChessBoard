package Engine;

public class AnalysisInfo {

	String info;
	boolean hasScore;

	boolean useCP;
	boolean useMate;
	int score;
	int mate;
	String position;
	String line;

	public AnalysisInfo(String info, String position) {
		this.info = info;
		this.position = position;
		update();
	}

	private void update() {

		hasScore = false;
		useCP = false;
		useMate = false;

		String[] data = info.split(" ");
		if (data.length == 0)
			return;

		if (data[0] == ChessEngine.INFO)
			return;

		int idx = getIndex(data, ChessEngine.SCORE);
		hasScore = (idx != -1);
		if (!hasScore)
			return;

		idx = getIndex(data, ChessEngine.CP);
		useCP = (idx != -1);
		if (useCP)
			score = Integer.parseInt(data[idx + 1]);

		idx = getIndex(data, ChessEngine.MATE);
		useMate = (idx != -1);
		if (useMate)
			mate = Integer.parseInt(data[idx + 1]);

		idx = getIndex(data, ChessEngine.PV);
		if (idx != -1) {
			line = data[idx + 1];
			for (int i = idx + 2; i < data.length; i++)
				line += " " + data[i];
		}
	}

	static private int getIndex(String[] list, String value) {
		for (int i = 0; i < list.length; i++)
			if (value.equals(list[i]))
				return i;
		return -1;
	}

	public boolean hasScore() {
		return hasScore;
	}

	public void invertScore() {
		score = -score;
	}

	public int getScore() {
		return score;
	}

	public int getMate() {
		return mate;
	}

	public String getLine() {
		return line;
	}

	public String getPosition() {
		return position;
	}

	@Override
	public String toString() {
		if (useCP)
			return String.format(score > 0 ? "+%s" : "%s", score / 100.0);
		else if (useMate)
			return String.format("#%s", mate);
		else
			return "";
	}

}
