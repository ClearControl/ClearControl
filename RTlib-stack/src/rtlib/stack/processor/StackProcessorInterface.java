package rtlib.stack.processor;

import rtlib.stack.Stack;
import rtlib.stack.StackRequest;
import coremem.recycling.Recycler;

public interface StackProcessorInterface<IT, OT>
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public Stack<OT> process(	Stack<IT> pStack,
														Recycler<Stack<OT>, StackRequest<OT>> pStackRecycler);

}
