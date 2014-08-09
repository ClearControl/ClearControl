package rtlib.stack.processor;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;

public interface StackProcessorInterface<I, O>
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public Stack<O> process(Stack<I> pStack,
													Recycler<Stack<O>, StackRequest<Stack<O>>> pStackRecycler);

}
