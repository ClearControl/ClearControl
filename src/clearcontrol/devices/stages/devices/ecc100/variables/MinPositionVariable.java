package clearcontrol.devices.stages.devices.ecc100.variables;

import clearcontrol.core.variable.Variable;
import ecc100.ECC100Axis;

public class MinPositionVariable extends Variable<Double>
{

  private static final double cEpsilon = 5; // nm
  private final ECC100Axis mECC100Axis;

  public MinPositionVariable(String pVariableName,
                             ECC100Axis pECC100Axis)
  {
    super(pVariableName, 0.0);
    mECC100Axis = pECC100Axis;
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
    return super.getEventHook(pCurrentValue);
  }
}
