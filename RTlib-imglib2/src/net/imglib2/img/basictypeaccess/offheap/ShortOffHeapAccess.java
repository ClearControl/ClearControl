package net.imglib2.img.basictypeaccess.offheap;

import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import net.imglib2.img.basictypeaccess.ShortAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class ShortOffHeapAccess extends AbstractOffHeapAccess	implements
																ShortAccess,
																ArrayDataAccess<ShortOffHeapAccess>
{

	public ShortOffHeapAccess(int numEntities)
	{
		mContiguousMemory = OffHeapMemory.allocateShorts(	"ShortOffHeapAccess",
															numEntities);
	}

	public ShortOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		super(pContiguousMemoryInterface);
	}

	@Override
	public short getValue(final int pIndex)
	{
		return mContiguousMemory.getShortAligned(pIndex);
	}

	@Override
	public void setValue(final int pIndex, final short pValue)
	{
		mContiguousMemory.setShortAligned(pIndex, pValue);
	}

	@Override
	public ContiguousMemoryInterface getCurrentStorageArray()
	{
		return mContiguousMemory;
	}

	@Override
	public ShortOffHeapAccess createArray(final int numEntities)
	{
		return new ShortOffHeapAccess(numEntities);
	}

}
