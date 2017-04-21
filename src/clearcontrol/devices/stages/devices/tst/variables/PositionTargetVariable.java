package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import aptj.APTJExeption;

/**
 * Target position variable
 *
 * @author royer
 */
public class PositionTargetVariable extends TSTDoubleVariableBase
{
  /**
   * Instantiates a target position variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public PositionTargetVariable(String pVariableName,
                                APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
  }

  @Override
  public Double setEventHook(Double pOldValue, Double pNewValue)
  {
    try
    {
      final double lValue = super.setEventHook(pOldValue, pNewValue);
      mAPTJDevice.moveTo(lValue);
      return lValue;
    }
    catch (APTJExeption e)
    {
      severe("Error while setting a new target position for device: %s",
             mAPTJDevice);
      e.printStackTrace();
    }
    return pNewValue;
  }

}
