package net.imglib2.img.basictypeaccess.offheap;

import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;

public abstract class AbstractOffHeapAccess {
    protected ContiguousMemoryInterface mContiguousMemory;

    public AbstractOffHeapAccess(Object pParent,
                                 long pAddress,
                                 long pLengthInBytes) {
        mContiguousMemory = OffHeapMemory.wrapPointer(pParent,
                pAddress,
                pLengthInBytes);
    }

    public AbstractOffHeapAccess(ContiguousMemoryInterface pContiguousMemoryInterface) {
        mContiguousMemory = pContiguousMemoryInterface;
    }

    public AbstractOffHeapAccess() {
    }

    public ContiguousMemoryInterface getContiguousMemory() {
        return mContiguousMemory;
    }

}
