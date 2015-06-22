package rtlib.core.variable.doublev;

import rtlib.core.variable.objectv.ObjectVariable;

public class DoubleVariable extends ObjectVariable<Double> implements
																													DoubleVariableInterface

{

	public DoubleVariable(final String pVariableName)
	{
		this(pVariableName, 0);
	}

	public DoubleVariable(final String pVariableName,
												final double pDoubleValue)
	{
		super(pVariableName, pDoubleValue);
	}

	@Override
	public void setValue(double pNewValue)
	{
		setReference(pNewValue);
	}

	@Override
	public double getValue()
	{
		return getReference();
	}

	@Override
	public void sendUpdatesTo(DoubleVariable pVariable)
	{
		super.sendUpdatesTo(pVariable);
	}

	@Override
	public void doNotSendUpdatesTo(DoubleVariable pVariable)
	{
		super.doNotSendUpdatesTo(pVariable);
	}

	@Override
	public void syncWith(DoubleVariable pVariable)
	{
		super.syncWith(pVariable);
	}

	@Override
	public void doNotSyncWith(DoubleVariable pVariable)
	{
		super.doNotSyncWith(pVariable);
	}

}
