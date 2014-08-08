package rtlib.stack.server;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;

public interface StackSourceInterface<O>
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(Recycler<Stack<O>, Long> pStackRecycler);

	public Stack<O> getStack(final long pStackIndex);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
