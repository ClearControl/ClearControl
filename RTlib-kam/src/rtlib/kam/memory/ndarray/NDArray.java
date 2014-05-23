package rtlib.kam.memory.ndarray;

import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.cursor.NDCursor;
import rtlib.kam.memory.cursor.NDCursorAccessible;
import rtlib.kam.memory.ram.RAM;

public class NDArray implements
										NDStructured,
										NDCursorAccessible,
										NDDefaultCursorAccessible

{

	protected final NDBoundedCursor mDefaultBoundedCursor;
	protected final RAM mRAM;

	public static NDArray wrap(RAM pRAM, final NDBoundedCursor pCursor)
	{
		return new NDArray(pRAM, pCursor);
	}

	public NDArray(RAM pRAM, NDBoundedCursor pCursor)
	{
		this(pRAM, 1, pCursor);
	}

	public NDArray(	RAM pRAM,
									long pElementSizeInBytes,
									NDBoundedCursor pCursor)
	{
		super();
		if (pRAM.getSizeInBytes() != pCursor.getLengthInElements() * pElementSizeInBytes)
		{
			String lErrorMessage = String.format(	"Can't create NDArray, wrong dimensions (ram.length=%d, cursor.length=%d )   ",
																						pRAM.getSizeInBytes(),
																						pCursor.getLengthInElements());
			throw new InvalidNDArrayDefinitionException(lErrorMessage);
		}
		mRAM = pRAM;
		mDefaultBoundedCursor = pCursor;
	}

	public RAM getRAM()
	{
		return mRAM;
	}

	@Override
	public long getDimension()
	{
		return mDefaultBoundedCursor.getDimension();
	}

	@Override
	public long getSizeAlongDimension(int pDimensionIndex)
	{
		return mDefaultBoundedCursor.getDimensions()[pDimensionIndex];
	}

	@Override
	public NDCursor getDefaultCursor()
	{
		return mDefaultBoundedCursor;
	}

	@Override
	public byte getByteAtCursor()
	{
		return mRAM.getByte(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor()
	{
		return mRAM.getChar(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor()
	{
		return mRAM.getShort(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor()
	{
		return mRAM.getInt(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor()
	{
		return mRAM.getLong(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor()
	{
		return mRAM.getFloat(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor()
	{
		return mRAM.getDouble(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(byte pValue)
	{
		mRAM.setByte(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setCharAtCursor(char pValue)
	{
		mRAM.setChar(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setShortAtCursor(short pValue)
	{
		mRAM.setShort(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setIntAtCursor(int pValue)
	{
		mRAM.setInt(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setLongAtCursor(long pValue)
	{
		mRAM.setLong(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setFloatAtCursor(float pValue)
	{
		mRAM.setFloat(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setDoubleAtCursor(double pValue)
	{
		mRAM.setDouble(	mDefaultBoundedCursor.getCurrentFlatIndex(),
										pValue);
	}

	@Override
	public byte getByteAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getByte(pCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getChar(pCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getShort(pCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getInt(pCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getLong(pCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getFloat(pCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getDouble(pCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(NDBoundedCursor pCursor, byte pValue)
	{
		mRAM.setByte(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setCharAtCursor(NDBoundedCursor pCursor, char pValue)
	{
		mRAM.setChar(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setShortAtCursor(NDBoundedCursor pCursor, short pValue)
	{
		mRAM.setShort(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setIntAtCursor(NDBoundedCursor pCursor, int pValue)
	{
		mRAM.setInt(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setLongAtCursor(NDBoundedCursor pCursor, long pValue)
	{
		mRAM.setLong(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setFloatAtCursor(NDBoundedCursor pCursor, float pValue)
	{
		mRAM.setFloat(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setDoubleAtCursor(NDBoundedCursor pCursor, double pValue)
	{
		mRAM.setDouble(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public long getVolume()
	{
		return mDefaultBoundedCursor.getVolume();
	}

	@Override
	public long getLengthInElements()
	{
		return mDefaultBoundedCursor.getLengthInElements();
	}

	@Override
	public boolean isVectorized()
	{
		return mDefaultBoundedCursor.isVectorized();
	}

}
