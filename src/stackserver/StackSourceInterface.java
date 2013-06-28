package stackserver;

import stack.Stack;
import ndarray.InterfaceNDArray;

public interface StackSourceInterface
{

	public long getNumberOfStacks();

	public Stack getStack(	final long pStackIndex,
															final Stack pStack);

	public long getStackTimeStampInNanoseconds(final long pStackIndex);

}
