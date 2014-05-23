package rtlib.core.memory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import sun.misc.Unsafe;

public final class NativeMemoryAccess
{

	static private Unsafe cUnsafe;

	static
	{
		Field lTheUnsafeField;
		try
		{
			lTheUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			lTheUnsafeField.setAccessible(true);
			cUnsafe = (Unsafe) lTheUnsafeField.get(null);
		}
		catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	static private final AtomicLong cMaximumAllocatableMemory = new AtomicLong(Long.MAX_VALUE);

	static private final ConcurrentHashMap<Long, Long> cAllocatedMemoryPointers = new ConcurrentHashMap<Long, Long>();
	static private final AtomicLong cTotalAllocatedMemory = new AtomicLong(0);

	public static final void registerMemoryRegion(long pAddress,
																								long pLength)
	{
		cTotalAllocatedMemory.addAndGet(pLength);
		cAllocatedMemoryPointers.put(pAddress, pLength);
	}

	public static final void deregisterMemoryRegion(long pAddress)
	{
		cTotalAllocatedMemory.addAndGet(-cAllocatedMemoryPointers.get(pAddress));
		cAllocatedMemoryPointers.remove(pAddress);
	}

	public static long getMaximumAllocatableMemory()
	{
		return cMaximumAllocatableMemory.get();
	}

	public static void setMaximumAllocatableMemory(long pMaximumAllocatableMemory)
	{
		cMaximumAllocatableMemory.set(pMaximumAllocatableMemory);
	}

	public static final long getTotalAllocatedMemory()
	{
		return cTotalAllocatedMemory.get();
	}

	public static final int getPageSize()
	{
		return cUnsafe.pageSize();
	}

	public static final long allocateMemory(final long pLengthInBytes)
	{
		checkMaxAllocatableMemory(pLengthInBytes);

		final long lAddress = cUnsafe.allocateMemory(pLengthInBytes);
		registerMemoryRegion(lAddress, pLengthInBytes);
		return lAddress;
	}

	static void checkMaxAllocatableMemory(final long pLengthInBytes) throws OutOfMemoryError
	{
		if (cTotalAllocatedMemory.get() + pLengthInBytes > cMaximumAllocatableMemory.get())
			throw new OutOfMemoryError(String.format(	"Canot allocate memory region of length: %d without reaching maximum allocatable memory %d (currently %d bytes are allocated )\n",
																								pLengthInBytes,
																								cMaximumAllocatableMemory.get(),
																								cTotalAllocatedMemory.get()));
	}

	public static final long reallocateMemory(final long pAddress,
																						final long pNewLengthInBytes) throws InvalidNativeMemoryAccessException
	{
		if (cAllocatedMemoryPointers.get(pAddress) == null)
			throw new InvalidNativeMemoryAccessException("Cannot free unallocated memory!");

		Long lCurrentlyAllocatedLength = cAllocatedMemoryPointers.get(pAddress);
		checkMaxAllocatableMemory(pNewLengthInBytes - lCurrentlyAllocatedLength);

		long lReallocatedMemoryAddress = cUnsafe.reallocateMemory(pAddress,
																															pNewLengthInBytes);
		if (lReallocatedMemoryAddress != pAddress)
		{
			deregisterMemoryRegion(pAddress);
			registerMemoryRegion(	lReallocatedMemoryAddress,
														pNewLengthInBytes);
		}
		return lReallocatedMemoryAddress;
	}

	public static final boolean isAllocatedMemory(final long pAddress)
	{
		return cAllocatedMemoryPointers.get(pAddress) != null;
	}

	public static final void freeMemory(final long pAddress) throws InvalidNativeMemoryAccessException
	{
		if (cAllocatedMemoryPointers.get(pAddress) == null)
			throw new InvalidNativeMemoryAccessException("Cannot free unallocated memory!");
		cUnsafe.freeMemory(pAddress);
		deregisterMemoryRegion(pAddress);
	}

	public static final void copyMemory(final long pAddressOrg,
																			final long pAddressDest,
																			final long pLengthInBytes) throws InvalidNativeMemoryAccessException
	{
		/*Long lLengthOrg = cAllocatedMemoryPointers.get(pAddressOrg);
		if (lLengthOrg == null)
			throw new InvalidNativeMemoryAccessException("Cannot copy from an unallocated memory region!");

		Long lLengthDest = cAllocatedMemoryPointers.get(pAddressDest);
		if (lLengthDest == null)
			throw new InvalidNativeMemoryAccessException("Cannot copy to an unallocated memory region!");

		if (pLengthInBytes > lLengthOrg)
			throw new InvalidNativeMemoryAccessException(String.format("Cannot copy - source too small! %d < %d)",
																					lLengthOrg,
																												pLengthInBytes));

		if (pLengthInBytes > lLengthDest)
			throw new InvalidNativeMemoryAccessException(String.format("Cannot copy - destination too small! %d < %d)",
																												lLengthDest,
																												pLengthInBytes));/**/

		cUnsafe.copyMemory(pAddressOrg, pAddressDest, pLengthInBytes);
	}

	public static final void setMemory(	final long pAddress,
																			final long pLengthInBytes,
																			final byte pValue) throws InvalidNativeMemoryAccessException
	{
		/*
		Long lLength = cAllocatedMemoryPointers.get(pAddress);
		if (lLength == null)
			throw new InvalidNativeMemoryAccessException("Cannot set unallocated memory region!");

		if (pLengthInBytes > lLength)
			throw new InvalidNativeMemoryAccessException(String.format("Cannot set - memory region too small! %d < %d)",
																												lLength,
																												pLengthInBytes));/**/

		cUnsafe.setMemory(pAddress, pLengthInBytes, pValue);
	}

	public static final void storeReorderingFence()
	{
		cUnsafe.storeFence();
	}

	public static final void loadReorderingFence()
	{
		cUnsafe.loadFence();
	}

	public static final void fullReorderingFence()
	{
		cUnsafe.fullFence();
	}

	public static final byte getByte(final long pAddress)
	{
		return cUnsafe.getByte(pAddress);
	}

	public static final char getChar(final long pAddress)
	{
		return cUnsafe.getChar(pAddress);
	}

	public static final short getShort(final long pAddress)
	{
		return cUnsafe.getShort(pAddress);
	}

	public static final int getInt(final long pAddress)
	{
		return cUnsafe.getInt(pAddress);
	}

	public static final long getLong(final long pAddress)
	{
		return cUnsafe.getLong(pAddress);
	}

	public static final float getFloat(final long pAddress)
	{
		return cUnsafe.getFloat(pAddress);
	}

	public static final double getDouble(final long pAddress)
	{
		return cUnsafe.getDouble(pAddress);
	}

	public static final void setByte(	final long pAddress,
																		final byte pValue)
	{
		cUnsafe.putByte(pAddress, pValue);
	}

	public static final void setChar(	final long pAddress,
																		final char pValue)
	{
		cUnsafe.putChar(pAddress, pValue);
	}

	public static final void setShort(final long pAddress,
																		final short pValue)
	{
		cUnsafe.putShort(pAddress, pValue);
	}

	public static final void setInt(final long pAddress,
																	final int pValue)
	{
		cUnsafe.putInt(pAddress, pValue);
	}

	public static final void setLong(	final long pAddress,
																		final long pValue)
	{
		cUnsafe.putLong(pAddress, pValue);
	}

	public static final void setFloat(final long pAddress,
																		final float pValue)
	{
		cUnsafe.putFloat(pAddress, pValue);
	}

	public static final void setDouble(	final long pAddress,
																			final double pValue)
	{
		cUnsafe.putDouble(pAddress, pValue);
	}

	public static void freeAll()
	{
		for (Map.Entry<Long, Long> lEntry : cAllocatedMemoryPointers.entrySet())
		{
			final long lAddress = lEntry.getKey();
			freeMemory(lAddress);
		}
	}

}
