package net.imglib2.img.basictypeaccess.offheap;

import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import net.imglib2.img.basictypeaccess.DoubleAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class DoubleOffHeapAccess extends AbstractOffHeapAccess implements
																															DoubleAccess,
																															ArrayDataAccess<DoubleOffHeapAccess>
{

	public DoubleOffHeapAccess(int numEntities)
	{
		mContiguousMemory = OffHeapMemory.allocateDoubles("DoubleOffHeapAccess",
																											numEntities);
	}

	public DoubleOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		super(pContiguousMemoryInterface);
	}

	@Override
	public double getValue(final int pIndex)
	{
		return mContiguousMemory.getDoubleAligned(pIndex);
	}

	@Override
	public void setValue(final int pIndex, final double pValue)
	{
		mContiguousMemory.setDoubleAligned(pIndex, pValue);
	}

	@Override
	public ContiguousMemoryInterface getCurrentStorageArray()
	{
		return mContiguousMemory;
	}

	@Override
	public DoubleOffHeapAccess createArray(final int numEntities)
	{
		return new DoubleOffHeapAccess(numEntities);
	}

}
