package LowLevel;

public class BoardData {
	
	byte[] data = new byte[8];
	int pieceCount;

	public BoardData(byte[] data)
	{
		assert data.length != 8;
		this.data = data;		
		for(int row = 0; row < 8; row++)
			pieceCount += Integer.bitCount(Byte.toUnsignedInt(this.data[row]));
	}
	
	public BoardData(String data)
	{
		assert data.length() != 16;
		
		for(int row = 0; row < 8; row++)
		{
			String str = data.substring(row * 2, row * 2 + 2);
			byte b = (byte)Integer.parseInt(str, 16);
			this.data[row] = b;
			
			pieceCount += Integer.bitCount(Byte.toUnsignedInt(b));
		}
	}
	
	public byte[] getData()
	{
		return data;		
	}
	
	public int getPieceCount()
	{
		return pieceCount;		
	}
	
	public String toString()
	{
		String result = "";
		for(int row = 0; row < 8; row++)
		{
			
			String line = Integer.toHexString(Byte.toUnsignedInt(data[row]));
			if (line.length() == 1)
				line = "0" + line;

			result += line;
		}			
		
		return result;
	}

	
}
