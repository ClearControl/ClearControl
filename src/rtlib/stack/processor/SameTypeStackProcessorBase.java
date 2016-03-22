package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public abstract class SameTypeStackProcessorBase<T extends NativeType<T>, A extends ArrayDataAccess<A>> extends
																										StackProcessorBase<T, A, T, A>	implements
																																		SameTypeStackProcessorInterface<T, A>
{

	public SameTypeStackProcessorBase(String pProcessorName)
	{
		super(pProcessorName);
	}

}
