package rtlib.stages.devices.ecc100.variables;

import rtlib.core.variable.types.objectv.ObjectVariable;
import ecc100.ECC100Axis;

public class PositionVariable extends ObjectVariable<Double>
{

	private static final double cEpsilon = 5; // nm
	private final ECC100Axis mECC100Axis;

	public PositionVariable(String pVariableName, ECC100Axis pECC100Axis)
	{
		super(pVariableName, 0.0);
		mECC100Axis = pECC100Axis;
	}

	@Override
	public Double setEventHook(Double pOldValue, Double pNewValue)
	{
		final double lValue = super.setEventHook(pOldValue, pNewValue);
		mECC100Axis.goToPosition(pNewValue, cEpsilon);
		return lValue;
	}

	@Override
	public Double getEventHook(Double pCurrentValue)
	{
		final double lCurrentPosition = mECC100Axis.getCurrentPosition();
		return super.getEventHook(lCurrentPosition);
	}
}
