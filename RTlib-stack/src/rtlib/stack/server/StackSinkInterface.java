package rtlib.stack.server;

import rtlib.core.variable.VariableInterface;
import rtlib.stack.Stack;

public interface StackSinkInterface
{

	public void addMetaDataVariable(final String pPrefix,
																	final VariableInterface<?> pVariable);

	public void removeAllMetaDataVariables();

	public boolean appendStack(final Stack pStack);

	public void removeMetaDataVariable(VariableInterface<?> pVariable);

}
