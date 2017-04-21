package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;

/**
 * Max position variable
 *
 * @author royer
 */
public class MaxPositionVariable extends TSTDoubleVariableBase
{

  /**
   * Instantiates a max position variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APTJ device
   */
  public MaxPositionVariable(String pVariableName,
                             APTJDevice pAPTJDevice)
  {
    super(pVariableName, pAPTJDevice);
  }

  @Override
  public Double setEventHook(Double pOldValue, Double pNewValue)
  {
    final double lValue = super.setEventHook(pOldValue, pNewValue);
    return lValue;
  }

  @Override
  public Double getEventHook(Double pCurrentValue)
  {
    final double lCurrentPosition = mAPTJDevice.getMaxPosition();
    return super.getEventHook(lCurrentPosition);
  }
}
