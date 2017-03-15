package net.imglib2.img.basictypeaccess.offheap;

import net.imglib2.img.basictypeaccess.IntAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;

public class IntOffHeapAccess extends AbstractOffHeapAccess implements
                              IntAccess,
                              ArrayDataAccess<IntOffHeapAccess>
{

  public IntOffHeapAccess(int numEntities)
  {
    mContiguousMemory = OffHeapMemory.allocateInts("IntOffHeapAccess",
                                                   numEntities);
  }

  public IntOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface)
  {
    super(pContiguousMemoryInterface);
  }

  @Override
  public int getValue(final int pIndex)
  {
    return mContiguousMemory.getIntAligned(pIndex);
  }

  @Override
  public void setValue(final int pIndex, final int pValue)
  {
    mContiguousMemory.setIntAligned(pIndex, pValue);
  }

  @Override
  public ContiguousMemoryInterface getCurrentStorageArray()
  {
    return mContiguousMemory;
  }

  @Override
  public IntOffHeapAccess createArray(final int numEntities)
  {
    return new IntOffHeapAccess(numEntities);
  }

}
