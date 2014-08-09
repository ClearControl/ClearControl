package rtlib.stack.server;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;

public interface StackSourceInterface<O>
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(Recycler<Stack<O>, StackRequest<Stack<O>>> pStackRecycler);

	public Stack<O> getStack(final long pStackIndex);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
