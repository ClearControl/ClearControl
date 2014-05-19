package rtlib.stack.server;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;

public interface StackSourceInterface
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(Recycler<Stack, Long> pStackRecycler);

	public Stack getStack(final long pStackIndex);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
