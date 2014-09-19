package rtlib.stages.devices.ecc100.variables;

import rtlib.core.variable.booleanv.BooleanVariable;
import ecc100.ECC100Axis;

public class ReadyVariable extends BooleanVariable
{

	private ECC100Axis mECC100Axis;

	public ReadyVariable(String pVariableName, ECC100Axis pECC100Axis)
	{
		super(pVariableName, false);
		mECC100Axis = pECC100Axis;
	}

	@Override
	public double setEventHook(double pOldValue, double pNewValue)
	{
		double lValue = super.setEventHook(pOldValue, pNewValue);
		return lValue;
	}

	@Override
	public double getEventHook(double pCurrentValue)
	{
		return BooleanVariable.boolean2double(mECC100Axis.isReady() && mECC100Axis.hasArrived());
	}
}
