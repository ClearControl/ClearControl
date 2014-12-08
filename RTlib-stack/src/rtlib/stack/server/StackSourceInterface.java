package rtlib.stack.server;

import rtlib.stack.Stack;
import rtlib.stack.StackRequest;
import coremem.recycling.Recycler;

public interface StackSourceInterface<T>
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(Recycler<Stack<T>, StackRequest<T>> pStackRecycler);

	public Stack<T> getStack(final long pStackIndex);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
