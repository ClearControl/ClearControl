package rtlib.stack.processor;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;

public interface StackProcessorInterface<IT, OT>
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public Stack<OT> process(	Stack<IT> pStack,
														Recycler<Stack<OT>, StackRequest<OT>> pStackRecycler);

}
