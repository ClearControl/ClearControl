package rtlib.stack;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import coremem.ContiguousMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclableFactory;
import coremem.util.Size;

public class ContiguousOffHeapPlanarStackFactory<T extends NativeType<T>, A extends ArrayDataAccess<A>> implements
																																																				RecyclableFactory<StackInterface<T, A>, StackRequest<T>>
{

	@SuppressWarnings("unchecked")
	@Override
	public OffHeapPlanarStack<T, A> create(StackRequest<T> pParameters)
	{
		final int lBytesPerVoxel = Size.of(pParameters.getType()
																									.getClass()
																									.getName());
		final long lVolume = pParameters.getWidth() * pParameters.getHeight()
													* pParameters.getDepth();
		final long lBufferSizeInBytes = lVolume * lBytesPerVoxel;
		final ContiguousMemoryInterface lContiguousMemoryInterface = new OffHeapMemory(	"OffHeapPlanarStack" + pParameters,
																																										lBufferSizeInBytes);
		return (OffHeapPlanarStack<T, A>) OffHeapPlanarStack.createStack(	lContiguousMemoryInterface,
																																			pParameters.getType(),
																																			pParameters.getWidth(),
																																			pParameters.getHeight(),
																																			pParameters.getDepth());
	}
}
