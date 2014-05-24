package rtlib.kam.memory.ndarray;

import rtlib.kam.memory.cursor.NDCursor;

public interface NDDefaultCursorAccessible
{

	public NDCursor getDefaultCursor();

	public byte getByteAtCursor();

	public char getCharAtCursor();

	public short getShortAtCursor();

	public int getIntAtCursor();

	public long getLongAtCursor();

	public float getFloatAtCursor();

	public double getDoubleAtCursor();

	public void setByteAtCursor(byte pByte);

	public void setCharAtCursor(char pChar);

	public void setShortAtCursor(short pShort);

	public void setIntAtCursor(int pInt);

	public void setLongAtCursor(long pLong);

	public void setFloatAtCursor(float pFloat);

	public void setDoubleAtCursor(double pDouble);
}
