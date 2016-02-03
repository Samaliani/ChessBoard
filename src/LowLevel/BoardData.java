package LowLevel;

public class BoardData {

	byte[] data = new byte[8];
	int pieceCount;

	public BoardData() {
	}

	public BoardData(byte[] data) {
		assert data.length != 8;
		this.data = data;
		update();
	}

	public BoardData(String data) {
		assert data.length() != 16;

		for (int row = 0; row < 8; row++) {
			String str = data.substring(row * 2, row * 2 + 2);
			byte b = (byte) Integer.parseInt(str, 16);
			this.data[row] = b;
		}
		update();
	}

	public static BoardData initialData = new BoardData("ffff00000000ffff");

	private void update() {
		pieceCount = 0;
		for (int row = 0; row < 8; row++)
			pieceCount += Integer.bitCount(Byte.toUnsignedInt(this.data[row]));
	}

	public void clear() {
		data = new byte[8];
	}

	public byte[] getData() {
		return data;
	}

	public byte getData(int index) {
		return data[index];
	}

	public void setData(int index, byte value) {
		data[index] = value;
	}
	
	public int getPieceCount() {
		return pieceCount;
	}

	public String toString() {
		String result = "";
		for (int row = 0; row < 8; row++) {

			String line = Integer.toHexString(Byte.toUnsignedInt(data[row]));
			if (line.length() == 1)
				line = "0" + line;

			result += line;
		}

		return result;
	}

	public boolean equals(BoardData data) {
		if (data.pieceCount != pieceCount)
			return false;

		for (int i = 0; i < 8; i++)
			if (this.data[i] != data.data[i])
				return false;
		return true;
	}

	public void plus(BoardData data) {
		for (int i = 0; i < 8; i++)
			this.data[i] |= data.data[i];
		update();
	}

	public void minus(BoardData data) {
		for (int i = 0; i < 8; i++)
			this.data[i] ^= data.data[i];
		update();
	}

	public void and(BoardData data) {
		for (int i = 0; i < 8; i++)
			this.data[i] &= data.data[i];
		update();
	}

	// Logic operators
	public static BoardData intersect(BoardData data1, BoardData data2) {
		BoardData result = new BoardData();
		for (int i = 0; i < 8; i++)
			result.data[i] = (byte) (data1.data[i] & data2.data[i]);
		result.update();
		return result;
	}

	public static BoardData not(BoardData data) {
		BoardData result = new BoardData();
		for (int i = 0; i < 8; i++)
			result.data[i] = (byte) (~data.data[i]);
		result.update();
		return result;
	}
	
}
