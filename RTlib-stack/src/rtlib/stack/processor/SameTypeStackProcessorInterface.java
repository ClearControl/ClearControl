package rtlib.stack.processor;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public interface SameTypeStackProcessorInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>> extends
																																																				StackProcessorInterface<T, A, T, A>
{

}
