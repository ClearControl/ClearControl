package rtlib.gui.video;

import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;

public interface StackDisplayInterface<T>
{

	ObjectVariable<Stack<T>> getOutputStackVariable();

	void setOutputStackVariable(ObjectVariable<Stack<T>> pOutputStackVariable);

}
