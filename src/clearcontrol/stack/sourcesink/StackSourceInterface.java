package clearcontrol.stack.sourcesink;

import java.util.concurrent.TimeUnit;

import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public interface StackSourceInterface
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(RecyclerInterface<StackInterface, StackRequest> pStackRecycler);

	public StackInterface getStack(long pStackIndex);

	public StackInterface getStack(	final long pStackIndex,
																	long pTime,
																	TimeUnit pTimeUnit);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
