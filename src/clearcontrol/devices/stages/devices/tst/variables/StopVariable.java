package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import aptj.APTJExeption;

/**
 * Stop variable
 *
 * @author royer
 */
public class StopVariable extends TSTBooleanVariableBase
{

  /**
   * Instantiates a stop variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public StopVariable(String pVariableName, APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
  }

  @Override
  public Boolean setEventHook(Boolean pOldValue, Boolean pNewValue)
  {
    final Boolean lValue = super.setEventHook(pOldValue, pNewValue);
    try
    {
      if (lValue && !pOldValue)
        mAPTJDevice.stop();
    }
    catch (APTJExeption e)
    {
      severe("Error while stopping device: %s", mAPTJDevice);
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
