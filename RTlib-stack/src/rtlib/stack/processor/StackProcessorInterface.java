package rtlib.stack.processor;

import rtlib.core.recycling.Recycler;
import rtlib.stack.Stack;

public interface StackProcessorInterface
{

	public void setActive(boolean pIsActive);

	public boolean isActive();

	public Stack process(	Stack pStack,
												Recycler<Stack, Long> pStackRecycler);

}
