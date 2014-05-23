package rtlib.kam.memory.impl.java;

import rtlib.core.log.Loggable;
import rtlib.core.memory.InvalidNativeMemoryAccessException;
import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.kam.memory.Copyable;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.RangeCopyable;

public class RAMJava<T> implements
												JavaAccessible<T>,
												Copyable<RAMJava<T>>,
												RangeCopyable<RAMJava<T>>,
												SizedInBytes,
												Loggable
{
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 1024;

	private Class<T> mDataType;
	private int mLength;
	protected Object mArray;

	public RAMJava(final Class<T> pType, final long pLength)
	{
		super();
		this.mDataType = pType;
		mLength = checkMaxJavaArraySize(pLength);

		if (pType == Byte.class)
		{
			mArray = new byte[mLength];
		}
		else if (pType == Short.class)
		{
			mArray = new short[mLength];
		}
		else if (pType == Integer.class)
		{
			mArray = new int[mLength];
		}
		else if (pType == Long.class)
		{
			mArray = new long[mLength];
		}
		else if (pType == Float.class)
		{
			mArray = new float[mLength];
		}
		else if (pType == Double.class)
		{
			mArray = new double[mLength];
		}
		else
		{
			final String lErrorMessage = String.format(	"Data type %s not yet implemented for %s",
																									pType.toString(),
																									RAMJava.class.toString());
			error("KAM", lErrorMessage);
			throw new UnsupportedOperationException(lErrorMessage);
		}
	}

	private final int checkMaxJavaArraySize(long pLength)
	{
		if (pLength > MAX_ARRAY_SIZE)
		{
			String lErrorMessage = String.format(	"Too big for Java arrays (len>%d)",
																						pLength);
			error("KAM", lErrorMessage);
			throw new UnsupportedOperationException(lErrorMessage);
		}
		return Math.toIntExact(pLength);
	}

	@Override
	public MemoryType getMemoryType()
	{
		return MemoryType.CPURAMJAVA;
	}

	@Override
	public Class<T> getDataType()
	{
		return mDataType;
	}

	@Override
	public Object getArray()
	{
		return mArray;
	}

	public byte[] getByteArray()
	{
		if (mDataType != Byte.class)
			return null;

		return (byte[]) mArray;
	}

	public short[] getShortArray()
	{
		if (mDataType != Short.class)
			return null;

		return (short[]) mArray;
	}

	public int[] getIntArray()
	{
		if (mDataType != Integer.class)
			return null;

		return (int[]) mArray;
	}

	public short[] getFloatArray()
	{
		if (mDataType != Short.class)
			return null;

		return (short[]) mArray;
	}

	public int[] getDoubleArray()
	{
		if (mDataType != Integer.class)
			return null;

		return (int[]) mArray;
	}

	@Override
	public long getLengthInElements()
	{
		return mLength;
	}

	@Override
	public final long getSizeInBytes()
	{
		return getLengthInElements() * SizeOf.sizeOf(getDataType());
	}

	@Override
	public void copyTo(RAMJava<T> pTo)
	{
		if (this.getLengthInElements() != pTo.getLengthInElements())
		{
			final String lErrorString = String.format("Attempted to copy memory regions (arrays) of different sizes: src.len=%d, dst.len=%d",
																								this.getLengthInElements(),
																								pTo.getLengthInElements());
			error("KAM", lErrorString);
			throw new InvalidNativeMemoryAccessException(lErrorString);
		}
		copyRangeTo(0, pTo, 0, pTo.getLengthInElements());
	}

	@Override
	public void copyRangeTo(long pSourceOffset,
													RAMJava<T> pTo,
													long pDestinationOffset,
													long pLengthToCopy)
	{
		Class<T> lTypeFrom = this.getDataType();
		Class<T> lTypeTo = pTo.getDataType();
		if (lTypeFrom != lTypeTo)
		{
			final String lErrorMessage = String.format(	"Cannot copy from array of type %s to array of type %s !",
																									lTypeFrom.toString(),
																									lTypeTo.toString());
			error("KAM", lErrorMessage);
			throw new UnsupportedOperationException(lErrorMessage);
		}

		System.arraycopy(	this.getArray(),
											Math.toIntExact(pSourceOffset),
											pTo.getArray(),
											Math.toIntExact(pDestinationOffset),
											Math.toIntExact(pLengthToCopy));
	}

}
