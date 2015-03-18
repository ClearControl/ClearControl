package rtlib.stack.server;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.Recycler;

public interface StackSourceInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(Recycler<StackInterface<T, A>, StackRequest<T>> pStackRecycler);

	public StackInterface<T, A> getStack(final long pStackIndex);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
