package stackserver;

import stack.Stack;
import variable.VariableInterface;
import variable.bundle.VariableBundle;
import ndarray.InterfaceNDArray;

public interface StackSinkInterface
{

	public void addMetaDataVariable(final String pPrefix,
													final VariableInterface<?> pVariable);
	
	public void removeAllMetaDataVariables();

	public boolean appendStack(final Stack pStack);

}
