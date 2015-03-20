package rtlib.stack;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import coremem.recycling.RecyclableFactory;

public class FragmentedOffHeapPlanarStackFactory<T extends NativeType<T>, A extends ArrayDataAccess<A>> implements
																																															RecyclableFactory<StackInterface<T, A>, StackRequest<T>>
{

	@Override
	public OffHeapPlanarStack<T, A> create(StackRequest<T> pParameters)
	{
		return new OffHeapPlanarStack<T, A>(pParameters);
	}

}
