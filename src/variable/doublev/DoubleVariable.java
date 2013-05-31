package variable.doublev;

import java.util.concurrent.CopyOnWriteArrayList;

import variable.EventPropagator;

public class DoubleVariable	implements
														DoubleInputOutputVariableInterface

{
	protected volatile double mValue;
	private final CopyOnWriteArrayList<DoubleVariable> mInputVariables = new CopyOnWriteArrayList<DoubleVariable>();

	public DoubleVariable(final double pDoubleValue)
	{
		mValue = pDoubleValue;
	}

	public void setCurrentValue(final Object pDoubleEventSource)
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

	@Override
	public double getValue()
	{
		return mValue;
	}

	public final void sendUpdatesTo(final DoubleVariable pDoubleVariable)
	{
		synchronized (this)
		{
			mInputVariables.add(pDoubleVariable);
		}
	}

	public final void syncWith(final DoubleVariable pDoubleVariable)
	{
		this.sendUpdatesTo(pDoubleVariable);
		pDoubleVariable.sendUpdatesTo(this);
	}

}
