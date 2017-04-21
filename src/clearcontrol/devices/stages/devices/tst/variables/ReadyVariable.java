package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import aptj.APTJExeption;

/**
 * Ready variable
 *
 * @author royer
 */
public class ReadyVariable extends TSTBooleanVariableBase
{

  /**
   * Instantiates a ready variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public ReadyVariable(String pVariableName, APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);

  }

  @Override
  public Boolean getEventHook(Boolean pCurrentValue)
  {
    try
    {
      return !mAPTJDevice.isMoving();
    }
    catch (APTJExeption e)
    {
      severe("Error while querying whether this device is ready: %s",
             mAPTJDevice);
      e.printStackTrace();
    }
    return pCurrentValue;
  }
}
