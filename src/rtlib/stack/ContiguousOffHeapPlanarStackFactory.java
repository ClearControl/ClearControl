package rtlib.stack;

import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclableFactory;
import coremem.types.NativeTypeEnum;
import coremem.util.Size;

public class ContiguousOffHeapPlanarStackFactory implements
																								RecyclableFactory<StackInterface, StackRequest>
{

	@SuppressWarnings("unchecked")
	@Override
	public OffHeapPlanarStack create(StackRequest pParameters)
	{
		final int lBytesPerVoxel = Size.of(NativeTypeEnum.UnsignedShort);
		final long lVolume = pParameters.getWidth() * pParameters.getHeight()
													* pParameters.getDepth();
		final long lBufferSizeInBytes = lVolume * lBytesPerVoxel;
		final ContiguousMemoryInterface lContiguousMemoryInterface = new OffHeapMemory(	"OffHeapPlanarStack" + pParameters,
																																										lBufferSizeInBytes);
		return OffHeapPlanarStack.createStack(	lContiguousMemoryInterface,
																																pParameters.getWidth(),
																																pParameters.getHeight(),
																																pParameters.getDepth());
	}
}
