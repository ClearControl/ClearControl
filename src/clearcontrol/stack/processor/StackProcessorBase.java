package clearcontrol.stack.processor;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;

/**
 * Base class for stack processor implementations
 *
 * @author royer
 */
public abstract class StackProcessorBase extends VirtualDevice
                                         implements
                                         StackProcessorInterface
{
  private final Variable<Boolean> mIsActiveVariable;

  /**
   * Instanciates a stack processor of given name
   * 
   * @param pProcessorName
   *          processor name
   */
  public StackProcessorBase(final String pProcessorName)
  {
    super(pProcessorName);
    mIsActiveVariable = new Variable<Boolean>(pProcessorName, true);
  }

  @Override
  public boolean isActive()
  {
    return mIsActiveVariable.get();
  }

  @Override
  public void setActive(final boolean pIsActive)
  {
    mIsActiveVariable.set(pIsActive);
  }

  /**
   * Returns the is-active boolean variable. This variable decides whether this
   * processor is active
   * 
   * @return true -> processor is acctive
   */
  public Variable<Boolean> getIsActiveBooleanVariable()
  {
    return mIsActiveVariable;
  }

  @Override
  public String toString()
  {
    return String.format("StackProcessor [mProcessorName=%s,mIsActiveVariable=%s]",
                         getName(),
                         mIsActiveVariable);
  }

}
