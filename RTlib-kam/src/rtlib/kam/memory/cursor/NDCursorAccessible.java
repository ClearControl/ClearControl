package rtlib.kam.memory.cursor;

public interface NDCursorAccessible
{

	public NDCursor getDefaultCursor();

	public byte getByteAtCursor(NDBoundedCursor pCursor);

	public char getCharAtCursor(NDBoundedCursor pCursor);

	public short getShortAtCursor(NDBoundedCursor pCursor);

	public int getIntAtCursor(NDBoundedCursor pCursor);

	public long getLongAtCursor(NDBoundedCursor pCursor);

	public float getFloatAtCursor(NDBoundedCursor pCursor);

	public double getDoubleAtCursor(NDBoundedCursor pCursor);

	public void setByteAtCursor(NDBoundedCursor pCursor, byte pByte);

	public void setCharAtCursor(NDBoundedCursor pCursor, char pChar);

	public void setShortAtCursor(NDBoundedCursor pCursor, short pShort);

	public void setIntAtCursor(NDBoundedCursor pCursor, int pInt);

	public void setLongAtCursor(NDBoundedCursor pCursor, long pLong);

	public void setFloatAtCursor(NDBoundedCursor pCursor, float pFloat);

	public void setDoubleAtCursor(NDBoundedCursor pCursor,
																double pDouble);
}
