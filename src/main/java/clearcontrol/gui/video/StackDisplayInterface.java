package clearcontrol.gui.video;

import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;

public interface StackDisplayInterface
{

  Variable<StackInterface> getInputStackVariable();

  Variable<StackInterface> getOutputStackVariable();

  void setOutputStackVariable(Variable<StackInterface> pOutputStackVariable);

}
