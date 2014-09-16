package rtlib.stages.devices.ecc100.variables;

import rtlib.core.variable.doublev.DoubleVariable;
import ecc100.ECC100Axis;

public class MaxPositionVariable extends DoubleVariable
{

	private static final double cEpsilon = 5; // nm
	private ECC100Axis mECC100Axis;

	public MaxPositionVariable(String pVariableName, ECC100Axis pECC100Axis)
	{
		super(pVariableName, 0);
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
		return super.getEventHook(pCurrentValue);
	}
}
