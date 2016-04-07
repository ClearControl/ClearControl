package rtlib.stages.devices.ecc100.variables;

import rtlib.core.variable.types.booleanv.BooleanVariable;
import ecc100.ECC100Axis;

public class ReadyVariable extends BooleanVariable
{

	private final ECC100Axis mECC100Axis;

	public ReadyVariable(String pVariableName, ECC100Axis pECC100Axis)
	{
		super(pVariableName, false);
		mECC100Axis = pECC100Axis;
	}

	@Override
	public Boolean setEventHook(Boolean pOldValue, Boolean pNewValue)
	{
		final Boolean lValue = super.setEventHook(pOldValue, pNewValue);
		return lValue;
	}

	@Override
	public Boolean getEventHook(Boolean pCurrentValue)
	{
		return mECC100Axis.isReady() && mECC100Axis.hasArrived();
	}
}
