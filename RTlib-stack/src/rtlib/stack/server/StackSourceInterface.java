package rtlib.stack.server;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import coremem.recycling.BasicRecycler;

public interface StackSourceInterface<T extends NativeType<T>, A extends ArrayDataAccess<A>>
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(BasicRecycler<StackInterface<T, A>, StackRequest<T>> pStackRecycler);

	public StackInterface<T, A> getStack(long pStackIndex);

	public StackInterface<T, A> getStack(	final long pStackIndex,
																				long pTime,
																				TimeUnit pTimeUnit);

	public double getStackTimeStampInSeconds(final long pStackIndex);



}
