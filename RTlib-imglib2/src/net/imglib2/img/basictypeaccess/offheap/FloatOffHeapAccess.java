package net.imglib2.img.basictypeaccess.offheap;

import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;

public class FloatOffHeapAccess extends AbstractOffHeapAccess	implements
																FloatAccess,
																ArrayDataAccess<FloatOffHeapAccess>
{

	public FloatOffHeapAccess(int numEntities)
	{
		mContiguousMemory = OffHeapMemory.allocateFloats(	"FloatOffHeapAccess",
															numEntities);
	}

	public FloatOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		super(pContiguousMemoryInterface);
	}

	@Override
	public float getValue(final int pIndex)
	{
		return mContiguousMemory.getFloatAligned(pIndex);
	}

	@Override
	public void setValue(final int pIndex, final float pValue)
	{
		mContiguousMemory.setFloatAligned(pIndex, pValue);
	}

	@Override
	public ContiguousMemoryInterface getCurrentStorageArray()
	{
		return mContiguousMemory;
	}

	@Override
	public FloatOffHeapAccess createArray(final int numEntities)
	{
		return new FloatOffHeapAccess(numEntities);
	}

}
