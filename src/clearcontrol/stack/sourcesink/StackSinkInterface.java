package clearcontrol.stack.sourcesink;

import clearcontrol.core.variable.Variable;
import clearcontrol.stack.StackInterface;

public interface StackSinkInterface
{

  public boolean appendStack(final StackInterface pStack);

  public void addMetaData(String pPrefix, double pValue);

  public void addMetaDataVariable(final String pPrefix,
                                  final Variable<?> pVariable);

  public void removeAllMetaDataVariables();

  public void removeMetaDataVariable(Variable<?> pVariable);

}
