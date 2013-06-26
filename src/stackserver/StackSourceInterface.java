package stackserver;

import ndarray.InterfaceNDArray;

public interface StackSourceInterface
{

	public long getNumberOfStacks();

	public InterfaceNDArray getStack(	final long pStackIndex,
																		final InterfaceNDArray pStack);

	public long getStackTimeStampInNanoseconds(final long pStackIndex);

}
