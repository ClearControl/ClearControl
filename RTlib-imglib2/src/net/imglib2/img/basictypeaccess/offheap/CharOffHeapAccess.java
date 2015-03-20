package net.imglib2.img.basictypeaccess.offheap;

import net.imglib2.img.basictypeaccess.CharAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;

public class CharOffHeapAccess extends AbstractOffHeapAccess	implements
																													CharAccess,
																													ArrayDataAccess<CharOffHeapAccess>
{

	public CharOffHeapAccess(int numEntities)
	{
		mContiguousMemory = OffHeapMemory.allocateChars("CharOffHeapAccess",
																										numEntities);
	}

	public CharOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
	{
		super(pContiguousMemoryInterface);
	}

	@Override
	public char getValue(final int pIndex)
	{
		return mContiguousMemory.getCharAligned(pIndex);
	}

	@Override
	public void setValue(final int pIndex, final char pValue)
	{
		mContiguousMemory.setCharAligned(pIndex, pValue);
	}

	@Override
	public ContiguousMemoryInterface getCurrentStorageArray()
	{
		return mContiguousMemory;
	}

	@Override
	public CharOffHeapAccess createArray(final int numEntities)
	{
		return new CharOffHeapAccess(numEntities);
	}

}
