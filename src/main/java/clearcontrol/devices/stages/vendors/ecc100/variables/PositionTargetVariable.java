package clearcontrol.devices.stages.devices.ecc100.variables;

import clearcontrol.core.variable.Variable;
import ecc100.ECC100Axis;

public class PositionTargetVariable extends Variable<Double>
{
  private static final double cEpsilon = 5; // nm

  private final ECC100Axis mECC100Axis;

  public PositionTargetVariable(String pVariableName,
                                ECC100Axis pECC100Axis)
  {
    super(pVariableName, 0.0);
    mECC100Axis = pECC100Axis;
  }

  @Override
  public Double setEventHook(Double pOldValue, Double pNewValue)
  {
    final double lValue = super.setEventHook(pOldValue, pNewValue);
    mECC100Axis.goToPosition(lValue, cEpsilon);
    return lValue;
  }

}
