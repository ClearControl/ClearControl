package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;

/**
 * Reset variable
 *
 * @author royer
 */
public class ResetVariable extends TSTBooleanVariableBase
{

  /**
   * Instantiates a reset variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public ResetVariable(String pVariableName, APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
  }

  @Override
  public Boolean setEventHook(Boolean pOldValue, Boolean pNewValue)
  {
    final Boolean lValue = super.setEventHook(pOldValue, pNewValue);
    // does not seem that we have anything to do here
    return lValue;
  }

}
