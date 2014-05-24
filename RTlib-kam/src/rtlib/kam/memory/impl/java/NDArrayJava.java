package rtlib.kam.memory.impl.java;

import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.cursor.NDCursor;
import rtlib.kam.memory.cursor.NDCursorAccessible;

public class NDArrayJava<T> extends RAMJava<T> implements
																							NDStructured,
																							NDCursorAccessible
{
	protected final long[] dimensions;

	// Trick: Cast Caching
	private byte[] bytearray = null;
	private char[] chararray = null;
	private short[] shortarray = null;
	private int[] intarray = null;
	private long[] longarray = null;
	private float[] floatarray = null;
	private double[] doublearray = null;

	public static <T> NDArrayJava<T> allocate1DArray(	Class<T> pType,
																										final long pLength)
	{

		return new NDArrayJava<T>(pType, 1, pLength);
	}

	public static <T> NDArrayJava<T> allocate2DArray(	Class<T> pType,
																										final long pWidth,
																										final long pHeight)
	{
		return new NDArrayJava<T>(pType, 1, pWidth, pHeight);
	}

	public static <T> NDArrayJava<T> allocate2DArray(	Class<T> pType,
																										final long pWidth,
																										final long pHeight,
																										final long pDepth)
	{
		return new NDArrayJava<T>(pType, 1, pWidth, pHeight, pDepth);
	}

	public static <T> NDArrayJava<T> allocate2DArray(	Class<T> pType,
																										final long pSize,
																										final long pWidth,
																										final long pHeight,
																										final long pDepth)
	{
		return new NDArrayJava<T>(pType, pSize, pWidth, pHeight, pDepth);
	}

	public NDArrayJava(Class<T> pType, long... pDimensions)
	{
		super(pType, getArrayLength(pDimensions));
		dimensions = pDimensions;

		if (pType == Byte.class)
			bytearray = (byte[]) getArray();

		else if (pType == Character.class)
			chararray = (char[]) getArray();

		else if (pType == Short.class)
			shortarray = (short[]) getArray();

		else if (pType == Integer.class)
			intarray = (int[]) getArray();

		else if (pType == Long.class)
			longarray = (long[]) getArray();

		else if (pType == Float.class)
			floatarray = (float[]) getArray();

		else if (pType == Double.class)
			doublearray = (double[]) getArray();
	}

	@Override
	public long getDimension()
	{
		return dimensions.length - 1;
	}

	@Override
	public long getSizeAlongDimension(int pDimensionIndex)
	{
		return dimensions[pDimensionIndex];
	}

	@Override
	public long getVolume()
	{
		return getArrayLength(dimensions);
	}

	public static final long getArrayLength(long[] pDimensions)
	{
		long length = 1;
		final long[] ldim = pDimensions;
		final int l = ldim.length;
		for (int i = 0; i < l; i++)
		{
			length *= ldim[i];
		}

		return length;
	}

	@Override
	public byte getByteAtCursor(NDBoundedCursor pCursor)
	{
		return bytearray[Math.toIntExact(pCursor.getCurrentFlatIndex())];
	}

	@Override
	public char getCharAtCursor(NDBoundedCursor pCursor)
	{
		return chararray[Math.toIntExact(pCursor.getCurrentFlatIndex())];
	}

	@Override
	public short getShortAtCursor(NDBoundedCursor pCursor)
	{
		return shortarray[Math.toIntExact(pCursor.getCurrentFlatIndex())];
	}

	@Override
	public int getIntAtCursor(NDBoundedCursor pCursor)
	{
		return intarray[Math.toIntExact(pCursor.getCurrentFlatIndex())];

	}

	@Override
	public long getLongAtCursor(NDBoundedCursor pCursor)
	{
		return longarray[Math.toIntExact(pCursor.getCurrentFlatIndex())];
	}

	@Override
	public float getFloatAtCursor(NDBoundedCursor pCursor)
	{
		return floatarray[Math.toIntExact(pCursor.getCurrentFlatIndex())];
	}

	@Override
	public double getDoubleAtCursor(NDBoundedCursor pCursor)
	{
		return doublearray[Math.toIntExact(pCursor.getCurrentFlatIndex())];
	}

	@Override
	public void setByteAtCursor(NDBoundedCursor pCursor, byte pByte)
	{
		bytearray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pByte;
	}

	@Override
	public void setCharAtCursor(NDBoundedCursor pCursor, char pChar)
	{
		chararray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pChar;
	}

	@Override
	public void setShortAtCursor(NDBoundedCursor pCursor, short pShort)
	{
		shortarray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pShort;
	}

	@Override
	public void setIntAtCursor(NDBoundedCursor pCursor, int pInt)
	{
		intarray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pInt;
	}

	@Override
	public void setLongAtCursor(NDBoundedCursor pCursor, long pLong)
	{
		longarray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pLong;
	}

	@Override
	public void setFloatAtCursor(NDBoundedCursor pCursor, float pFloat)
	{
		floatarray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pFloat;
	}

	@Override
	public void setDoubleAtCursor(NDBoundedCursor pCursor,
																double pDouble)
	{
		doublearray[Math.toIntExact(pCursor.getCurrentFlatIndex())] = pDouble;
	}

	@Override
	public NDCursor getDefaultCursor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVectorized()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
