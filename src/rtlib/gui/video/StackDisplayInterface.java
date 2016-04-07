package rtlib.gui.video;

import rtlib.core.variable.Variable;
import rtlib.stack.StackInterface;

public interface StackDisplayInterface
{

	Variable<StackInterface> getOutputStackVariable();

	void setOutputStackVariable(Variable<StackInterface> pOutputStackVariable);

}
