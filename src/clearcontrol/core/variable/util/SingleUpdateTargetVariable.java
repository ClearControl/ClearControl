package clearcontrol.core.variable.util;

import clearcontrol.core.variable.Variable;

/**
 * Single update target variable.
 * 
 * this class wraps a variable and prevents updates from being sent to more tan
 * one variable.
 *
 * @param <O>
 *          variable value type
 * @author royer
 */
public class SingleUpdateTargetVariable<O> extends Variable<O>
{

  /**
   * Instantiates a single-update-target variable
   * 
   * @param pVariableName
   *          variable name
   */
  public SingleUpdateTargetVariable(final String pVariableName)
  {
    super(pVariableName);
  }

  /**
   * Single update target variable
   * 
   * @param pVariableName
   *          variable name
   * @param pValue
   *          variable value
   */
  public SingleUpdateTargetVariable(final String pVariableName,
                                    final O pValue)
  {
    super(pVariableName, pValue);
  }

  @Override
  public final void sendUpdatesTo(final Variable<O> pObjectVariable)
  {
    if (mVariablesToSendUpdatesTo.size() != 0)
    {
      throw new IllegalArgumentException(this.getClass()
                                             .getSimpleName()
                                         + ": cannot send updates to more than one peer! (sending to one peer registered already)");
    }

    mVariablesToSendUpdatesTo.add(pObjectVariable);
  }

  @Override
  public final Variable<O> sendUpdatesToInstead(final Variable<O> pObjectVariable)
  {
    if (mVariablesToSendUpdatesTo.size() >= 2)
    {
      throw new IllegalArgumentException(this.getClass()
                                             .getSimpleName()
                                         + ": cannot send updates to more than one peer! (more than 1 peer is registered already)");
    }

    mVariablesToSendUpdatesTo.clear();

    if (pObjectVariable == null)
    {
      if (mVariablesToSendUpdatesTo.isEmpty())
      {
        return null;
      }
      else
      {
        final Variable<O> lPreviousObjectVariable =
                                                  mVariablesToSendUpdatesTo.get(0);
        return lPreviousObjectVariable;
      }
    }

    if (mVariablesToSendUpdatesTo.isEmpty())
    {
      mVariablesToSendUpdatesTo.add(pObjectVariable);
      return null;
    }
    else
    {
      final Variable<O> lPreviousObjectVariable =
                                                mVariablesToSendUpdatesTo.get(0);
      mVariablesToSendUpdatesTo.add(pObjectVariable);
      return lPreviousObjectVariable;
    }
  }

}
