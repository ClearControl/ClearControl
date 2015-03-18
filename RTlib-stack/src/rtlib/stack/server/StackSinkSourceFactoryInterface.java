package rtlib.stack.server;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

public interface StackSinkSourceFactoryInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>
{

	public StackSinkInterface<T, A> getStackSink();

}
