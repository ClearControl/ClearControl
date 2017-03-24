package clearcontrol.devices.stages.devices.ecc100.variables;

import clearcontrol.core.variable.Variable;
import ecc100.ECC100Axis;

public class PositionCurrentVariable extends Variable<Double>
{

  private static final double cEpsilon = 5; // nm
  private final ECC100Axis mECC100Axis;

  public PositionCurrentVariable(String pVariableName,
                                 ECC100Axis pECC100Axis)
  {
    super(pVariableName, 0.0);
    mECC100Axis = pECC100Axis;
  }

  @Override
  public Double getEventHook(Double pCurrentValue)
  {
    final double lCurrentPosition = mECC100Axis.getCurrentPosition();
    return super.getEventHook(lCurrentPosition);
  }
}
