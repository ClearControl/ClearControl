package rtlib.stages.devices.ecc100.variables;

import rtlib.core.variable.doublev.DoubleVariable;
import ecc100.ECC100Axis;

public class PositionVariable extends DoubleVariable
{

	private static final double cEpsilon = 5; // nm
	private ECC100Axis mECC100Axis;

	public PositionVariable(String pVariableName, ECC100Axis pECC100Axis)
	{
		super(pVariableName, 0);
		mECC100Axis = pECC100Axis;
	}

	@Override
	public double setEventHook(double pOldValue, double pNewValue)
	{
		double lValue = super.setEventHook(pOldValue, pNewValue);
		mECC100Axis.goToPosition(pNewValue, cEpsilon);
		return lValue;
	}

	@Override
	public double getEventHook(double pCurrentValue)
	{
		int lCurrentPosition = mECC100Axis.getCurrentPosition();
		return super.getEventHook(lCurrentPosition);
	}
}
