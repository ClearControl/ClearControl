package net.imglib2.img.basictypeaccess.offheap;

import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;

public class ByteOffHeapAccess extends AbstractOffHeapAccess implements
															ByteAccess,
															ArrayDataAccess<ByteOffHeapAccess>
{

	public ByteOffHeapAccess(int numEntities)
	{
		mContiguousMemory = OffHeapMemory.allocateBytes("ByteOffHeapAccess",
														numEntities);
	}

	public ByteOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		super(pContiguousMemoryInterface);
	}

	@Override
	public byte getValue(final int pIndex)
	{
		return mContiguousMemory.getByteAligned(pIndex);
	}

	@Override
	public void setValue(final int pIndex, final byte pValue)
	{
		mContiguousMemory.setByteAligned(pIndex, pValue);
	}

	@Override
	public ContiguousMemoryInterface getCurrentStorageArray()
	{
		return mContiguousMemory;
	}

	@Override
	public ByteOffHeapAccess createArray(final int numEntities)
	{
		return new ByteOffHeapAccess(numEntities);
	}

}
