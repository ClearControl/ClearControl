package net.imglib2.img.basictypeaccess.offheap;

import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;

public class LongOffHeapAccess extends AbstractOffHeapAccess	implements
																													LongAccess,
																													ArrayDataAccess<LongOffHeapAccess>
{

	public LongOffHeapAccess(int numEntities)
	{
		mContiguousMemory = OffHeapMemory.allocateInts(	"LongOffHeapAccess",
																										numEntities);
	}

	public LongOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		super(pContiguousMemoryInterface);
	}

	@Override
	public long getValue(final int pIndex)
	{
		return mContiguousMemory.getLongAligned(pIndex);
	}

	@Override
	public void setValue(final int pIndex, final long pValue)
	{
		mContiguousMemory.setLongAligned(pIndex, pValue);
	}

	@Override
	public ContiguousMemoryInterface getCurrentStorageArray()
	{
		return mContiguousMemory;
	}

	@Override
	public LongOffHeapAccess createArray(final int numEntities)
	{
		return new LongOffHeapAccess(numEntities);
	}

}
