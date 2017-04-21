package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;
import aptj.APTJExeption;

/**
 * Current position variable
 *
 * @author royer
 */
public class PositionCurrentVariable extends TSTDoubleVariableBase
{

  /**
   * Instantiates a current position variable
   * @param pVariableName variable name
   * @param pAPTJDevice APTJ device
   */
  public PositionCurrentVariable(String pVariableName,
                                 APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
  }



  @Override
  public Double getEventHook(Double pCurrentValue)
  {
    try
    {
      final double lCurrentPosition = mAPTJDevice.getCurrentPosition();
      return super.getEventHook(lCurrentPosition);
    }
    catch (APTJExeption e)
    {
      severe("Error while querying the current position of device: %s",mAPTJDevice);
      e.printStackTrace();
    }
    return super.getEventHook(pCurrentValue);
  }
}
