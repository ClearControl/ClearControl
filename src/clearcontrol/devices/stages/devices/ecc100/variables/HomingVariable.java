package clearcontrol.devices.stages.devices.ecc100.variables;

import clearcontrol.core.variable.Variable;
import ecc100.ECC100Axis;

public class HomingVariable extends Variable<Boolean>
{

  private final ECC100Axis mECC100Axis;

  public HomingVariable(String pVariableName, ECC100Axis pECC100Axis)
  {
    super(pVariableName, false);
    mECC100Axis = pECC100Axis;
  }

  @Override
  public Boolean setEventHook(Boolean pOldValue, Boolean pNewValue)
  {
    final Boolean lValue = super.setEventHook(pOldValue, pNewValue);
    mECC100Axis.home();
    return lValue;
  }

  @Override
  public Boolean getEventHook(Boolean pCurrentValue)
  {
    return super.getEventHook(pCurrentValue);
  }
}
