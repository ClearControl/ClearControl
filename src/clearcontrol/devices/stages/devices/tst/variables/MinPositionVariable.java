package clearcontrol.devices.stages.devices.tst.variables;

import aptj.APTJDevice;

/**
 * Min position variable
 *
 * @author royer
 */
public class MinPositionVariable extends TSTDoubleVariableBase
{
  /**
   * Instantiates a min position variable
   * 
   * @param pVariableName
   *          variable name
   * @param pAPTJDevice
   *          APYJ device
   */
  public MinPositionVariable(String pVariableName,
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
    final double lCurrentPosition = mAPTJDevice.getMinPosition();
    return super.getEventHook(lCurrentPosition);
  }
}
