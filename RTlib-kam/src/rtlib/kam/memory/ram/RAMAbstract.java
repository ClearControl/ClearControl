package rtlib.kam.memory.ram;

import rtlib.core.memory.InvalidNativeMemoryAccessException;
import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.SizeOf;
import rtlib.core.memory.SizedInBytes;
import rtlib.core.rgc.Cleanable;
import rtlib.core.rgc.Freeable;
import rtlib.core.rgc.RessourceGarbageCollector;
import rtlib.kam.memory.Copyable;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MappableMemoryException;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.PointerAccessible;
import rtlib.kam.memory.RangeCopyable;

public abstract class RAMAbstract	implements
																	PointerAccessible,
																	Resizable,
																	SizedInBytes,
																	RAM,
																	Copyable<RAMAbstract>,
																	RangeCopyable<RAMAbstract>,
																	Freeable,
																	Cleanable

{

	protected long mAddressInBytes = 0;
	protected long mLengthInBytes = 0;
	protected boolean mIsFree = false;

	public RAMAbstract()
	{
		RessourceGarbageCollector.register(this);
	}

	@Override
	public abstract MemoryType getMemoryType();

	@Override
	public long getSizeInBytes()
	{
		complainIfFreed();
		return mLengthInBytes;
	}

	@Override
	public void copyTo(RAMAbstract pTo)
	{
		complainIfFreed();
		checkMappableMemory(pTo);
		checkMappableMemory(this);

		if (this.getSizeInBytes() != pTo.getSizeInBytes())
		{
			final String lErrorString = String.format("Attempted to copy memory regions of different sizes: src.len=%d, dst.len=%d",
																								this.getSizeInBytes(),
																								pTo.getSizeInBytes());
			// error("KAM", lErrorString);
			throw new InvalidNativeMemoryAccessException(lErrorString);
		}
		NativeMemoryAccess.copyMemory(this.getAddress(),
																	pTo.getAddress(),
																	pTo.getSizeInBytes());
	}

	@Override
	public void copyRangeTo(long pSourceOffset,
													RAMAbstract pTo,
													long pDestinationOffset,
													long pLengthToCopy)
	{
		complainIfFreed();
		checkMappableMemory(pTo);
		checkMappableMemory(this);

		if (pLengthToCopy == 0)
			return;

		final long lSrcAddress = this.getAddress();
		final long lDstAddress = pTo.getAddress();

		final long lSrcLength = this.getSizeInBytes();
		final long lDstLength = pTo.getSizeInBytes();

		if ((pLengthToCopy < 0))
		{
			final String lErrorString = "Attempted to copy a region of negative length!";
			// error("KAM", lErrorString);
			throw new InvalidNativeMemoryAccessException(lErrorString);
		}

		if ((pLengthToCopy + pSourceOffset > lSrcLength))
		{
			final String lErrorString = "Attempted to read past the end of the source memory region";
			// error("KAM", lErrorString);
			throw new InvalidNativeMemoryAccessException(lErrorString);
		}

		if ((pLengthToCopy + pDestinationOffset > lDstLength))
		{
			final String lErrorString = "Attempted to writing past the end of the destination memory region";
			// error("KAM", lErrorString);
			throw new InvalidNativeMemoryAccessException(lErrorString);
		}

		final long lSrcCopyAddress = lSrcAddress + pSourceOffset;
		final long lDstCopyAddress = lDstAddress + pDestinationOffset;

		NativeMemoryAccess.copyMemory(lSrcCopyAddress,
																	lDstCopyAddress,
																	pLengthToCopy);
	}

	void checkMappableMemory(RAMAbstract pTo)
	{
		if (pTo instanceof MappableMemory)
		{
			MappableMemory lMappableMemory = (MappableMemory) pTo;
			if (!lMappableMemory.isCurrentlyMapped())
				throw new MappableMemoryException("Memory is not mapped!");
		}
	}

	@Override
	public long getAddress()
	{
		complainIfFreed();
		checkMappableMemory(this);
		return mAddressInBytes;
	}

	@Override
	public void free()
	{
		mIsFree = true;
	}

	@Override
	public boolean isFree()
	{
		return mIsFree;
	}

	@Override
	public abstract long resize(long pNewLength);

	@Override
	public void setByteAligned(long pOffset, byte pValue)
	{
		NativeMemoryAccess.setByte(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setCharAligned(long pOffset, char pValue)
	{
		NativeMemoryAccess.setChar(mAddressInBytes + SizeOf.sizeOfByte()
																* pOffset, pValue);
	}

	@Override
	public void setShortAligned(long pOffset, short pValue)
	{
		NativeMemoryAccess.setShort(mAddressInBytes + SizeOf.sizeOfShort()
																		* pOffset,
																pValue);
	}

	@Override
	public void setIntAligned(long pOffset, int pValue)
	{
		NativeMemoryAccess.setInt(mAddressInBytes + SizeOf.sizeOfInt()
															* pOffset, pValue);
	}

	@Override
	public void setLongAligned(long pOffset, long pValue)
	{
		NativeMemoryAccess.setLong(mAddressInBytes + SizeOf.sizeOfLong()
																* pOffset, pValue);
	}

	@Override
	public void setFloatAligned(long pOffset, float pValue)
	{
		NativeMemoryAccess.setFloat(mAddressInBytes + SizeOf.sizeOfFloat()
																		* pOffset,
																pValue);
	}

	@Override
	public void setDoubleAligned(long pOffset, double pValue)
	{
		NativeMemoryAccess.setDouble(	mAddressInBytes + SizeOf.sizeOfDouble()
																			* pOffset,
																	pValue);
	}

	@Override
	public byte getByteAligned(long pOffset)
	{
		return NativeMemoryAccess.getByte(mAddressInBytes + SizeOf.sizeOfByte()
																			* pOffset);
	}

	@Override
	public char getCharAligned(long pOffset)
	{
		return NativeMemoryAccess.getChar(mAddressInBytes + SizeOf.sizeOfChar()
																			* pOffset);
	}

	@Override
	public short getShortAligned(long pOffset)
	{
		return NativeMemoryAccess.getShort(mAddressInBytes + SizeOf.sizeOfShort()
																				* pOffset);
	}

	@Override
	public int getIntAligned(long pOffset)
	{
		return NativeMemoryAccess.getInt(mAddressInBytes + SizeOf.sizeOfInt()
																			* pOffset);
	}

	@Override
	public long getLongAligned(long pOffset)
	{
		return NativeMemoryAccess.getLong(mAddressInBytes + SizeOf.sizeOfLong()
																			* pOffset);
	}

	@Override
	public float getFloatAligned(long pOffset)
	{
		return NativeMemoryAccess.getFloat(mAddressInBytes + SizeOf.sizeOfFloat()
																				* pOffset);
	}

	@Override
	public double getDoubleAligned(long pOffset)
	{
		return NativeMemoryAccess.getDouble(mAddressInBytes + SizeOf.sizeOfDouble()
																				* pOffset);
	}

	@Override
	public void setByte(long pOffset, byte pValue)
	{
		NativeMemoryAccess.setByte(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setChar(long pOffset, char pValue)
	{
		NativeMemoryAccess.setChar(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setShort(long pOffset, short pValue)
	{
		NativeMemoryAccess.setShort(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setInt(long pOffset, int pValue)
	{
		NativeMemoryAccess.setInt(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setLong(long pOffset, long pValue)
	{
		NativeMemoryAccess.setLong(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setFloat(long pOffset, float pValue)
	{
		NativeMemoryAccess.setFloat(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public void setDouble(long pOffset, double pValue)
	{
		NativeMemoryAccess.setDouble(mAddressInBytes + pOffset, pValue);
	}

	@Override
	public byte getByte(long pOffset)
	{
		return NativeMemoryAccess.getByte(mAddressInBytes + pOffset);
	}

	@Override
	public char getChar(long pOffset)
	{
		return NativeMemoryAccess.getChar(mAddressInBytes + pOffset);
	}

	@Override
	public short getShort(long pOffset)
	{
		return NativeMemoryAccess.getShort(mAddressInBytes + pOffset);
	}

	@Override
	public int getInt(long pOffset)
	{
		return NativeMemoryAccess.getInt(mAddressInBytes + pOffset);
	}

	@Override
	public long getLong(long pOffset)
	{
		return NativeMemoryAccess.getLong(mAddressInBytes + pOffset);
	}

	@Override
	public float getFloat(long pOffset)
	{
		return NativeMemoryAccess.getFloat(mAddressInBytes + pOffset);
	}

	@Override
	public double getDouble(long pOffset)
	{
		return NativeMemoryAccess.getDouble(mAddressInBytes + pOffset);
	}

}
