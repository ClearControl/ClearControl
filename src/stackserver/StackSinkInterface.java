package stackserver;

import ndarray.InterfaceNDArray;

public interface StackSinkInterface
{

	public boolean appendStack(	final long pTmeStampInNanoseconds,
															final InterfaceNDArray pStack);

}
