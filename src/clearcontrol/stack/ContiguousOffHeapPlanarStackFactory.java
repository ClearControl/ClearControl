package clearcontrol.stack;

import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclableFactoryInterface;
import coremem.util.Size;

public class ContiguousOffHeapPlanarStackFactory implements
        RecyclableFactoryInterface<StackInterface, StackRequest> {

    @SuppressWarnings("unchecked")
    @Override
    public OffHeapPlanarStack create(StackRequest pStackRequest) {
        final long lBytesPerVoxel = Size.of(NativeTypeEnum.UnsignedShort);
        final long lVolume = pStackRequest.getWidth()
                * pStackRequest.getHeight()
                * pStackRequest.getDepth();
        final long lBufferSizeInBytesWithMetaData =
                lVolume * lBytesPerVoxel
                        + pStackRequest.getMetadataSizeInBytes();
        final ContiguousMemoryInterface lContiguousMemoryInterface = OffHeapMemory.allocateAlignedBytes("OffHeapPlanarStack"
                        + pStackRequest,
                lBufferSizeInBytesWithMetaData,
                pStackRequest.getAlignment());

        return OffHeapPlanarStack.createStack(lContiguousMemoryInterface,
                pStackRequest.getWidth(),
                pStackRequest.getHeight(),
                pStackRequest.getDepth());
    }
}
