package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import aptj.APTJExeption;

/**
 * Homing variable
 *
 * @author royer
 */
public class HomingVariable extends TSTBooleanVariableBase
{
  /**
   * Instantiates an homing variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public HomingVariable(String pVariableName, APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Boolean setEventHook(Boolean pOldValue, Boolean pNewValue)
  {
    final Boolean lValue = super.setEventHook(pOldValue, pNewValue);
    try
    {
      if (lValue && !pOldValue)
        mAPTJDevice.home();
    }
    catch (APTJExeption e)
    {
      severe("Error  while homing device: %s", mAPTJDevice);
      e.printStackTrace();
    }
    return lValue;
  }

  @Override
  public Boolean getEventHook(Boolean pCurrentValue)
  {
    return super.getEventHook(pCurrentValue);
  }
}
