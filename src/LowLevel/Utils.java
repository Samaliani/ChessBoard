package LowLevel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Chess.Board;
import Chess.Position;

public class Utils {

	public static BoardData getBoardData(Board board) {

		byte[] data = new byte[8];
		for (int row = 0; row < 8; row++) {
			byte line = 0;
			for (int col = 0; col < 8; col++) {
				line <<= 1;
				if (board.getPiece(new Position(col, row)) != null)
					line |= 1;
			}
			data[row] = line;
		}

		return new BoardData(data);
	}

	public static List<Position> getAppeared(Board board, BoardData startData, BoardData finishData) {

		List<Position> result = new ArrayList<Position>();
		for (int row = 0; row < 8; row++) {
			int c = (~startData.data[row]) & finishData.data[row];
			if (c != 0) {
				for (int col = 0; col < 8; col++) {
					if (c == 128 >>> col)
						result.add(new Position(col, row));
				}
			}

		}
		return result;
	}
	
	public static List<Position> getDissapeared(Board board, BoardData startData, BoardData finishData) {

		List<Position> result = new ArrayList<Position>();
		for (int row = 0; row < 8; row++) {
			int c = startData.data[row] & (~finishData.data[row]);
			if (c != 0) {
				for (int col = 0; col < 8; col++) {
					if (c == 128 >>> col)
						result.add(new Position(col, row));
				}
			}

		}
		return result;
	}

	public static BoardData loadFromFile(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));

		byte[] data = new byte[8];
		for (int i = 8; i > 0; i--) {
			String line = br.readLine();
			data[i - 1] = (byte) Integer.parseInt(line, 2);
		}

		br.close();

		return new BoardData(data);
	}

	public static void saveToFile(String fileName, BoardData data) throws IOException {
		FileWriter writer = new FileWriter(fileName);

		byte[] d = data.getData();
		for (int i = 8; i > 0; i--) {
			String line = Integer.toBinaryString(Byte.toUnsignedInt(d[i]));
			while (line.length() < 8)
				line = "0" + line;
			writer.write(line);
			writer.write("\r\n");
		}

		writer.flush();
		writer.close();

	}
}
