package rtlib.kam.memory.ndarray;

import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.cursor.NDCursorAccessible;
import rtlib.kam.memory.ram.RAM;

public class TypedNDArray<T> extends NDArray implements
																						NDStructured,
																						NDCursorAccessible,
																						NDDefaultCursorAccessible,
																						SizedInBytes

{

	private final Class<T> mType;
	private final int mElementSize;

	public static <T> TypedNDArray<T> allocateNDArray(RAM pRAM,
																										Class<T> pType,
																										final NDBoundedCursor pCursor)
	{
		return new TypedNDArray<T>(pRAM, pType, pCursor);
	}

	public TypedNDArray(RAM pRAM,
											Class<T> pType,
											NDBoundedCursor pCursor)
	{
		super(pRAM, SizeOf.sizeOf(pType), pCursor);
		mType = pType;
		mElementSize = SizeOf.sizeOf(pType);
	}

	@Override
	public byte getByteAtCursor()
	{
		return mRAM.getByte(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor()
	{
		return mRAM.getChar(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor()
	{
		return mRAM.getShort(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor()
	{
		return mRAM.getInt(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor()
	{
		return mRAM.getLong(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor()
	{
		return mRAM.getFloat(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor()
	{
		return mRAM.getDouble(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(byte pValue)
	{
		mRAM.setByte(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setCharAtCursor(char pValue)
	{
		mRAM.setChar(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setShortAtCursor(short pValue)
	{
		mRAM.setShort(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setIntAtCursor(int pValue)
	{
		mRAM.setInt(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
								pValue);
	}

	@Override
	public void setLongAtCursor(long pValue)
	{
		mRAM.setLong(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setFloatAtCursor(float pValue)
	{
		mRAM.setFloat(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setDoubleAtCursor(double pValue)
	{
		mRAM.setDouble(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
										pValue);
	}

	@Override
	public byte getByteAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getByte(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getChar(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getShort(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getInt(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getLong(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getFloat(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor(NDBoundedCursor pCursor)
	{
		return mRAM.getDouble(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(NDBoundedCursor pCursor, byte pValue)
	{
		mRAM.setByte(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setCharAtCursor(NDBoundedCursor pCursor, char pValue)
	{
		mRAM.setChar(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setShortAtCursor(NDBoundedCursor pCursor, short pValue)
	{
		mRAM.setShort(mElementSize * pCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setIntAtCursor(NDBoundedCursor pCursor, int pValue)
	{
		mRAM.setInt(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setLongAtCursor(NDBoundedCursor pCursor, long pValue)
	{
		mRAM.setLong(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setFloatAtCursor(NDBoundedCursor pCursor, float pValue)
	{
		mRAM.setFloat(mElementSize * pCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setDoubleAtCursor(NDBoundedCursor pCursor, double pValue)
	{
		mRAM.setDouble(	mElementSize * pCursor.getCurrentFlatIndex(),
										pValue);
	}

	@Override
	public long getSizeInBytes()
	{
		return getLengthInElements() * mElementSize;
	}

}
