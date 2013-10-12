package stackserver;

import recycling.Recycler;
import stack.Stack;

public interface StackSourceInterface
{

	public boolean update();

	public long getNumberOfStacks();

	public void setStackRecycler(Recycler<Stack> pStackRecycler);

	public Stack getStack(final long pStackIndex);

	public double getStackTimeStampInSeconds(final long pStackIndex);

}
