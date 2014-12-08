package rtlib.kam.memory.ndarray;

import org.bridj.Pointer;

import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.cursor.NDCursorAccessible;
import coremem.MemoryRegionInterface;
import coremem.interfaces.MemoryType;
import coremem.interfaces.ReadAtAligned;
import coremem.interfaces.SizedInBytes;
import coremem.interfaces.Typed;
import coremem.interfaces.WriteAtAligned;
import coremem.util.SizeOf;

public class NDArrayTyped<T> extends NDArray implements
																						NDStructured,
																						NDCursorAccessible,
																						NDDefaultCursorAccessible,
																						WriteAtAligned,
																						ReadAtAligned,
																						Typed<T>,
																						SizedInBytes

{

	private final Class<T> mType;
	private final int mElementSize;

	public static <T> NDArrayTyped<T> allocateNDArray(MemoryRegionInterface<T> pMemoryRegionInterface,
																										Class<T> pType,
																										final NDBoundedCursor pCursor)
	{
		return new NDArrayTyped<T>(pMemoryRegionInterface, pType, pCursor);
	}

	public NDArrayTyped(MemoryRegionInterface<T> pMemoryRegionInterface,
											Class<T> pType,
											NDBoundedCursor pCursor)
	{
		super(pMemoryRegionInterface, SizeOf.sizeOf(pType), pCursor);
		mType = pType;
		mElementSize = SizeOf.sizeOf(pType);
	}

	@Override
	public long getSizeInBytes()
	{
		return getLengthInElements() * mElementSize;
	}

	@Override
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
		return mMemoryRegionInterface.getByte(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor()
	{
		return mMemoryRegionInterface.getChar(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor()
	{
		return mMemoryRegionInterface.getShort(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor()
	{
		return mMemoryRegionInterface.getInt(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor()
	{
		return mMemoryRegionInterface.getLong(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor()
	{
		return mMemoryRegionInterface.getFloat(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor()
	{
		return mMemoryRegionInterface.getDouble(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(byte pValue)
	{
		mMemoryRegionInterface.setByte(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setCharAtCursor(char pValue)
	{
		mMemoryRegionInterface.setChar(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setShortAtCursor(short pValue)
	{
		mMemoryRegionInterface.setShort(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setIntAtCursor(int pValue)
	{
		mMemoryRegionInterface.setInt(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
								pValue);
	}

	@Override
	public void setLongAtCursor(long pValue)
	{
		mMemoryRegionInterface.setLong(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setFloatAtCursor(float pValue)
	{
		mMemoryRegionInterface.setFloat(mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setDoubleAtCursor(double pValue)
	{
		mMemoryRegionInterface.setDouble(	mElementSize * mDefaultBoundedCursor.getCurrentFlatIndex(),
										pValue);
	}

	@Override
	public byte getByteAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getByte(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getChar(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getShort(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getInt(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getLong(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getFloat(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getDouble(mElementSize * pCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(NDBoundedCursor pCursor, byte pValue)
	{
		mMemoryRegionInterface.setByte(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setCharAtCursor(NDBoundedCursor pCursor, char pValue)
	{
		mMemoryRegionInterface.setChar(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setShortAtCursor(NDBoundedCursor pCursor, short pValue)
	{
		mMemoryRegionInterface.setShort(mElementSize * pCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setIntAtCursor(NDBoundedCursor pCursor, int pValue)
	{
		mMemoryRegionInterface.setInt(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setLongAtCursor(NDBoundedCursor pCursor, long pValue)
	{
		mMemoryRegionInterface.setLong(mElementSize * pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setFloatAtCursor(NDBoundedCursor pCursor, float pValue)
	{
		mMemoryRegionInterface.setFloat(mElementSize * pCursor.getCurrentFlatIndex(),
									pValue);
	}

	@Override
	public void setDoubleAtCursor(NDBoundedCursor pCursor, double pValue)
	{
		mMemoryRegionInterface.setDouble(	mElementSize * pCursor.getCurrentFlatIndex(),
										pValue);
	}

	@Override
	public MemoryType getMemoryType()
	{
		return mMemoryRegionInterface.getMemoryType();
	}

	@Override
	public void setByteAligned(long pOffset, byte pValue)
	{
		mMemoryRegionInterface.setByteAligned(pOffset, pValue);
	}

	@Override
	public void setCharAligned(long pOffset, char pValue)
	{
		mMemoryRegionInterface.setCharAligned(pOffset, pValue);
	}

	@Override
	public void setShortAligned(long pOffset, short pValue)
	{
		mMemoryRegionInterface.setShortAligned(pOffset, pValue);
	}

	@Override
	public void setIntAligned(long pOffset, int pValue)
	{
		mMemoryRegionInterface.setIntAligned(pOffset, pValue);
	}

	@Override
	public void setLongAligned(long pOffset, long pValue)
	{
		mMemoryRegionInterface.setLongAligned(pOffset, pValue);
	}

	@Override
	public void setFloatAligned(long pOffset, float pValue)
	{
		mMemoryRegionInterface.setFloatAligned(pOffset, pValue);
	}

	@Override
	public void setDoubleAligned(long pOffset, double pValue)
	{
		mMemoryRegionInterface.setDoubleAligned(pOffset, pValue);
	}

	@Override
	public byte getByteAligned(long pOffset)
	{
		return mMemoryRegionInterface.getByteAligned(pOffset);
	}

	@Override
	public char getCharAligned(long pOffset)
	{
		return mMemoryRegionInterface.getCharAligned(pOffset);
	}

	@Override
	public short getShortAligned(long pOffset)
	{
		return mMemoryRegionInterface.getShortAligned(pOffset);
	}

	@Override
	public int getIntAligned(long pOffset)
	{
		return mMemoryRegionInterface.getIntAligned(pOffset);
	}

	@Override
	public long getLongAligned(long pOffset)
	{
		return mMemoryRegionInterface.getLongAligned(pOffset);
	}

	@Override
	public float getFloatAligned(long pOffset)
	{
		return mMemoryRegionInterface.getFloatAligned(pOffset);
	}

	@Override
	public double getDoubleAligned(long pOffset)
	{
		return mMemoryRegionInterface.getDoubleAligned(pOffset);
	}

	@SuppressWarnings(
	{ "unchecked" })
	public Pointer<T> getBridJPointer(Class<T> pTargetClass)
	{
		return ((MemoryRegionInterface<T>) mMemoryRegionInterface).getBridJPointer(pTargetClass);
	}


}
