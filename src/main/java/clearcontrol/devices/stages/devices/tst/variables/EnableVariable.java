package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;

/**
 * Enable stage variable
 *
 * @author royer
 */
public class EnableVariable extends TSTBooleanVariableBase
{

  /**
   * Instantiates an enable variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public EnableVariable(String pVariableName, APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
  }

  @Override
  public Boolean setEventHook(Boolean pOldValue, Boolean pNewValue)
  {
    final Boolean lValue = super.setEventHook(pOldValue, pNewValue);
    // nothing to do, stage is always enabld
    return lValue;
  }

  @Override
  public Boolean getEventHook(Boolean pCurrentValue)
  {
    return super.getEventHook(pCurrentValue);
  }
}
