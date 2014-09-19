package rtlib.kam.memory.ndarray;

import org.bridj.Pointer;

import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.kam.memory.BridJPointerWrappable;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.Typed;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.cursor.NDCursorAccessible;
import rtlib.kam.memory.ram.RAM;
import rtlib.kam.memory.ram.ReadAtAligned;
import rtlib.kam.memory.ram.WriteAtAligned;

public class NDArrayTyped<T> extends NDArray implements
																						NDStructured,
																						NDCursorAccessible,
																						NDDefaultCursorAccessible,
																						WriteAtAligned,
																						ReadAtAligned,
																						Typed<T>,
																						SizedInBytes,
																						BridJPointerWrappable<T>

{

	private final Class<T> mType;
	private final int mElementSize;

	public static <T> NDArrayTyped<T> allocateNDArray(RAM pRAM,
																										Class<T> pType,
																										final NDBoundedCursor pCursor)
	{
		return new NDArrayTyped<T>(pRAM, pType, pCursor);
	}

	public NDArrayTyped(RAM pRAM,
											Class<T> pType,
											NDBoundedCursor pCursor)
	{
		super(pRAM, SizeOf.sizeOf(pType), pCursor);
		mType = pType;
		mElementSize = SizeOf.sizeOf(pType);
	}

	@Override
	public long getSizeInBytes()
	{
		return getLengthInElements() * mElementSize;
	}

	public Class<T> getType()
	{
		return mType;
	}

	public boolean isFloatingPointType()
	{
		return mType == float.class || mType == double.class;
	}

	public boolean isIntegralNumberType()
	{
		return mType == byte.class || mType == char.class
						|| mType == short.class
						|| mType == int.class
						|| mType == long.class;
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
	public MemoryType getMemoryType()
	{
		return mRAM.getMemoryType();
	}

	@Override
	public void setByteAligned(long pOffset, byte pValue)
	{
		mRAM.setByteAligned(pOffset, pValue);
	}

	@Override
	public void setCharAligned(long pOffset, char pValue)
	{
		mRAM.setCharAligned(pOffset, pValue);
	}

	@Override
	public void setShortAligned(long pOffset, short pValue)
	{
		mRAM.setShortAligned(pOffset, pValue);
	}

	@Override
	public void setIntAligned(long pOffset, int pValue)
	{
		mRAM.setIntAligned(pOffset, pValue);
	}

	@Override
	public void setLongAligned(long pOffset, long pValue)
	{
		mRAM.setLongAligned(pOffset, pValue);
	}

	@Override
	public void setFloatAligned(long pOffset, float pValue)
	{
		mRAM.setFloatAligned(pOffset, pValue);
	}

	@Override
	public void setDoubleAligned(long pOffset, double pValue)
	{
		mRAM.setDoubleAligned(pOffset, pValue);
	}

	@Override
	public byte getByteAligned(long pOffset)
	{
		return mRAM.getByteAligned(pOffset);
	}

	@Override
	public char getCharAligned(long pOffset)
	{
		return mRAM.getCharAligned(pOffset);
	}

	@Override
	public short getShortAligned(long pOffset)
	{
		return mRAM.getShortAligned(pOffset);
	}

	@Override
	public int getIntAligned(long pOffset)
	{
		return mRAM.getIntAligned(pOffset);
	}

	@Override
	public long getLongAligned(long pOffset)
	{
		return mRAM.getLongAligned(pOffset);
	}

	@Override
	public float getFloatAligned(long pOffset)
	{
		return mRAM.getFloatAligned(pOffset);
	}

	@Override
	public double getDoubleAligned(long pOffset)
	{
		return mRAM.getDoubleAligned(pOffset);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Pointer<T> getBridJPointer(Class<T> pTargetClass)
	{
		return (Pointer<T>) mRAM.getBridJPointer(pTargetClass);
	}


}
