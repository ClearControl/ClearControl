package clearcontrol.stack.processor;

import clearcontrol.core.variable.Variable;

public abstract class StackProcessorBase implements
                                         StackProcessorInterface
{
  private final Variable<Boolean> mIsActiveVariable;
  private final String mProcessorName;

  public StackProcessorBase(final String pProcessorName)
  {
    super();
    mProcessorName = pProcessorName;
    mIsActiveVariable = new Variable<Boolean>(pProcessorName, false);
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

  public Variable<Boolean> getIsActiveBooleanVariable()
  {
    return mIsActiveVariable;
  }

  public String getName()
  {
    return mProcessorName;
  }

  @Override
  public String toString()
  {
    return String.format("StackProcessor [mProcessorName=%s,mIsActiveVariable=%s]",
                         mProcessorName,
                         mIsActiveVariable);
  }

}
