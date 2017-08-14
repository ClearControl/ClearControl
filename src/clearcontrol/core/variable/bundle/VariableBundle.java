package clearcontrol.core.variable.bundle;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableBase;

/**
 * Variable bundle
 *
 * @author royer
 */
public class VariableBundle extends VariableBase<VariableBundle>
{

  private ConcurrentHashMap<String, Variable<?>> mVariableNameToVariableMap =
                                                                            new ConcurrentHashMap<String, Variable<?>>();

  /**
   * Variable bundle
   * 
   * @param pBundleName
   *          bundle name
   */
  public VariableBundle(final String pBundleName)
  {
    super(pBundleName);
  }

  @Override
  public VariableBundle get()
  {
    return this;
  }

  protected Collection<Variable<?>> getAllVariables()
  {
    return mVariableNameToVariableMap.values();
  }

  /**
   * Adds given variable to bundle
   * 
   * @param pVariable
   *          variable to add
   */
  public <O> void addVariable(final Variable<O> pVariable)
  {
    mVariableNameToVariableMap.put(pVariable.getName(), pVariable);
  }

  /**
   * Removes variable from bundle
   * 
   * @param pVariable
   *          variable to remove
   */
  public <O> void removeVariable(final Variable<O> pVariable)
  {
    mVariableNameToVariableMap.remove(pVariable);
  }

  /**
   * 
   */
  public void removeAllVariables()
  {
    mVariableNameToVariableMap.clear();
  }

  /**
   * Returns variable for given name
   * 
   * @param pVariableName
   *          variable name
   * @return variable of given name
   */
  @SuppressWarnings("unchecked")
  public <O> Variable<O> getVariable(final String pVariableName)
  {
    return (Variable<O>) mVariableNameToVariableMap.get(pVariableName);
  }

  /**
   * Sends updates from the variable in bundle of given name to teh given
   * variable.
   * 
   * @param pVariableName
   *          variable name
   * @param pToVariable
   *          variable to send updates to
   */
  public <O> void sendUpdatesTo(final String pVariableName,
                                final Variable<O> pToVariable)
  {
    final Variable<O> lFromVariable = getVariable(pVariableName);

    final Variable<O> lFromDoubleVariable = lFromVariable;
    final Variable<O> lToDoubleVariable = pToVariable;

    lFromDoubleVariable.sendUpdatesTo(lToDoubleVariable);

  }

  /**
   * Do not send (anymore) updates from teh variable in bundle of given nae to
   * the given variable.
   * 
   * @param pVariableName
   *          variable name
   * @param pToVariable
   *          variable to _not_ send updates to
   */
  public <O> void doNotSendUpdatesTo(final String pVariableName,
                                     final Variable<O> pToVariable)
  {
    final Variable<O> lFromVariable = getVariable(pVariableName);

    final Variable<O> lFromDoubleVariable = lFromVariable;
    final Variable<O> lToDoubleVariable = pToVariable;

    lFromDoubleVariable.doNotSendUpdatesTo(lToDoubleVariable);

  }

  /**
   * Get updates from a given variable to the variable in the bundle of given
   * name.
   * 
   * @param pVariableName
   *          variable name
   * @param pFromVariable
   *          variable to get updates from.
   */
  public <O> void getUpdatesFrom(final String pVariableName,
                                 final Variable<O> pFromVariable)
  {
    final Variable<O> lToVariable = getVariable(pVariableName);

    final Variable<O> lTo_DoubleVariable = lToVariable;
    final Variable<O> lFrom_DoubleVariable = pFromVariable;

    lFrom_DoubleVariable.sendUpdatesTo(lTo_DoubleVariable);

  }

  /**
   * Do not get (anymore) updates from a given variable to the variable in
   * bundle of given name.
   * 
   * @param pVariableName
   *          variable name
   * @param pFromVariable
   *          variable _not_ to get updates from.
   */
  public <O> void doNotGetUpdatesFrom(final String pVariableName,
                                      final Variable<O> pFromVariable)
  {
    final Variable<O> lToVariable = getVariable(pVariableName);

    final Variable<O> lTo_DoubleVariable = lToVariable;
    final Variable<O> lFrom_DoubleVariable = pFromVariable;

    lFrom_DoubleVariable.doNotSendUpdatesTo(lTo_DoubleVariable);

  }

  /**
   * Sync the variable in bundle of given name to the given variable.
   * 
   * @param pVariableName
   *          variable name
   * @param pVariable
   *          variable
   */
  public <O> void syncWith(final String pVariableName,
                           final Variable<O> pVariable)
  {
    this.sendUpdatesTo(pVariableName, pVariable);
    this.getUpdatesFrom(pVariableName, pVariable);
  }

  /**
   * Do not Sync (anymore) the variable in bundle of given name to the given
   * variable.
   * 
   * @param pVariableName
   *          variable name
   * @param pVariable
   *          variable
   */
  public <O> void doNotSyncWith(final String pVariableName,
                                final Variable<O> pVariable)
  {
    this.doNotSendUpdatesTo(pVariableName, pVariable);
    this.doNotGetUpdatesFrom(pVariableName, pVariable);
  }

  @Override
  public String toString()
  {
    return String.format("VariableBundle(%s,%s)",
                         getName(),
                         mVariableNameToVariableMap);
  }

}
