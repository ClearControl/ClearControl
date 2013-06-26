package variable.doublev;

import java.util.concurrent.CopyOnWriteArrayList;

import variable.DoubleVariableInterface;
import variable.EventPropagator;
import variable.NamedVariable;

public class DoubleVariable extends NamedVariable	implements
																									DoubleVariableInterface,
																									DoubleInputOutputVariableInterface

{
	protected volatile double mValue;
	private final CopyOnWriteArrayList<DoubleVariable> mInputVariables = new CopyOnWriteArrayList<DoubleVariable>();

	public DoubleVariable(final String pVariableName)
	{
		this(pVariableName, 0);
	}

	public DoubleVariable(final String pVariableName,
												final double pDoubleValue)
	{
		super(pVariableName);
		mValue = pDoubleValue;
	}

	@Override
	public void setCurrent()
	{
		EventPropagator.clear();
		setValue(mValue);
	}

	@Override
	public void setValue(final double pNewValue)
	{
		EventPropagator.clear();
		setValueInternal(pNewValue);
	}

	public void setLongValue(final long pNewValue)
	{
		setValue(Double.longBitsToDouble(pNewValue));
	}

	@Override
	public void set(final Double pNewValue)
	{
		setValue(pNewValue);
	}

	public boolean setValueInternal(final double pNewValue)
	{
		if (EventPropagator.hasBeenTraversed(this))
			return false;

		final double lNewValueAfterHook = setEventHook(pNewValue);

		EventPropagator.add(this);
		if (mInputVariables != null)
		{
			for (final DoubleVariable lDoubleVariable : mInputVariables)
				if (EventPropagator.hasNotBeenTraversed(lDoubleVariable))
				{
					lDoubleVariable.setValueInternal(lNewValueAfterHook);
				}
		}
		mValue = lNewValueAfterHook;
		return true;
	}

	public double setEventHook(final double pNewValue)
	{
		return pNewValue;
	}

	public double getEventHook(final double pCurrentValue)
	{
		return pCurrentValue;
	}

	@Override
	public Double get()
	{
		return getEventHook(getValue());
	}

	@Override
	public double getValue()
	{
		return getEventHook(mValue);
	}

	public long getLongValue()
	{
		return Double.doubleToRawLongBits(mValue);
	}

	@Override
	public final void sendUpdatesTo(final DoubleVariable pDoubleVariable)
	{
		mInputVariables.add(pDoubleVariable);
	}

	@Override
	public final void doNotSendUpdatesTo(final DoubleVariable pDoubleVariable)
	{
		mInputVariables.remove(pDoubleVariable);
	}

	@Override
	public final void doNotSendAnyUpdates()
	{
		mInputVariables.clear();
	}

	@Override
	public final void syncWith(final DoubleVariable pDoubleVariable)
	{
		this.sendUpdatesTo(pDoubleVariable);
		pDoubleVariable.sendUpdatesTo(this);
	}

	@Override
	public void doNotSyncWith(final DoubleVariable pDoubleVariable)
	{
		this.doNotSendUpdatesTo(pDoubleVariable);
		pDoubleVariable.doNotSendUpdatesTo(this);
	}

}
