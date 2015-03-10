package rtlib.kam.memory.ndarray;

import org.bridj.Pointer;

import rtlib.kam.memory.NDStructured;
import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.cursor.NDCursor;
import rtlib.kam.memory.cursor.NDCursorAccessible;
import coremem.MemoryRegionInterface;

public class NDArray implements
										NDStructured,
										NDCursorAccessible,
										NDDefaultCursorAccessible

{

	protected final NDBoundedCursor mDefaultBoundedCursor;
	protected final MemoryRegionInterface<?> mMemoryRegionInterface;

	public static NDArray wrap(	MemoryRegionInterface<?> pMemoryRegionInterface,
															final NDBoundedCursor pCursor)
	{
		return new NDArray(pMemoryRegionInterface, pCursor);
	}

	public NDArray(	MemoryRegionInterface<?> pMemoryRegionInterface,
									NDBoundedCursor pCursor)
	{
		this(pMemoryRegionInterface, 1, pCursor);
	}

	public NDArray(	MemoryRegionInterface<?> pMemoryRegionInterface,
									long pElementSizeInBytes,
									NDBoundedCursor pCursor)
	{
		super();
		if (pMemoryRegionInterface.getSizeInBytes() != pCursor.getLengthInElements() * pElementSizeInBytes)
		{
			final String lErrorMessage = String.format(	"Can't create NDArray, wrong dimensions (ram.length=%d, cursor.length=%d )   ",
																						pMemoryRegionInterface.getSizeInBytes(),
																						pCursor.getLengthInElements());
			throw new InvalidNDArrayDefinitionException(lErrorMessage);
		}
		mMemoryRegionInterface = pMemoryRegionInterface;
		mDefaultBoundedCursor = pCursor;
	}

	public MemoryRegionInterface<?> getMemoryRegionInterface()
	{
		return mMemoryRegionInterface;
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
		return mMemoryRegionInterface.getByte(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor()
	{
		return mMemoryRegionInterface.getChar(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor()
	{
		return mMemoryRegionInterface.getShort(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor()
	{
		return mMemoryRegionInterface.getInt(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor()
	{
		return mMemoryRegionInterface.getLong(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor()
	{
		return mMemoryRegionInterface.getFloat(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor()
	{
		return mMemoryRegionInterface.getDouble(mDefaultBoundedCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(byte pValue)
	{
		mMemoryRegionInterface.setByte(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setCharAtCursor(char pValue)
	{
		mMemoryRegionInterface.setChar(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setShortAtCursor(short pValue)
	{
		mMemoryRegionInterface.setShort(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setIntAtCursor(int pValue)
	{
		mMemoryRegionInterface.setInt(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setLongAtCursor(long pValue)
	{
		mMemoryRegionInterface.setLong(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setFloatAtCursor(float pValue)
	{
		mMemoryRegionInterface.setFloat(mDefaultBoundedCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setDoubleAtCursor(double pValue)
	{
		mMemoryRegionInterface.setDouble(	mDefaultBoundedCursor.getCurrentFlatIndex(),
										pValue);
	}

	@Override
	public byte getByteAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getByte(pCursor.getCurrentFlatIndex());
	}

	@Override
	public char getCharAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getChar(pCursor.getCurrentFlatIndex());
	}

	@Override
	public short getShortAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getShort(pCursor.getCurrentFlatIndex());
	}

	@Override
	public int getIntAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getInt(pCursor.getCurrentFlatIndex());
	}

	@Override
	public long getLongAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getLong(pCursor.getCurrentFlatIndex());
	}

	@Override
	public float getFloatAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getFloat(pCursor.getCurrentFlatIndex());
	}

	@Override
	public double getDoubleAtCursor(NDBoundedCursor pCursor)
	{
		return mMemoryRegionInterface.getDouble(pCursor.getCurrentFlatIndex());
	}

	@Override
	public void setByteAtCursor(NDBoundedCursor pCursor, byte pValue)
	{
		mMemoryRegionInterface.setByte(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setCharAtCursor(NDBoundedCursor pCursor, char pValue)
	{
		mMemoryRegionInterface.setChar(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setShortAtCursor(NDBoundedCursor pCursor, short pValue)
	{
		mMemoryRegionInterface.setShort(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setIntAtCursor(NDBoundedCursor pCursor, int pValue)
	{
		mMemoryRegionInterface.setInt(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setLongAtCursor(NDBoundedCursor pCursor, long pValue)
	{
		mMemoryRegionInterface.setLong(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setFloatAtCursor(NDBoundedCursor pCursor, float pValue)
	{
		mMemoryRegionInterface.setFloat(pCursor.getCurrentFlatIndex(), pValue);
	}

	@Override
	public void setDoubleAtCursor(NDBoundedCursor pCursor, double pValue)
	{
		mMemoryRegionInterface.setDouble(pCursor.getCurrentFlatIndex(), pValue);
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

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Pointer getBridJPointer()
	{
		return mMemoryRegionInterface.getBridJPointer((Class) Byte.class);
	}


}
