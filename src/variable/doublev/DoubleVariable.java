package variable.doublev;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class DoubleVariable	implements
														DoubleInputOutputVariableInterface

{
	protected volatile double mValue;
	private DoubleInputVariableInterface mInputVariable;
	private CopyOnWriteArrayList<DoubleInputVariableInterface> mInputVariables;
	private DoubleOutputVariableInterface mOutputVariable;

	public DoubleVariable(double pDoubleValue)
	{
		mValue = pDoubleValue;
	}
	
	public void setCurrentValue(Object pDoubleEventSource)
	{
		setValue(pDoubleEventSource,mValue);
	}

	public final void setValue(final double pNewValue)
	{
		setValue(this, pNewValue);
	}

	@Override
	public void setValue(Object pDoubleEventSource, double pNewValue)
	{

		if (mInputVariable != null)
			mInputVariable.setValue(pDoubleEventSource, pNewValue);
		else if (mInputVariables != null)
		{
			for (DoubleInputVariableInterface lDoubleInputVariableInterface : mInputVariables)
			{
				lDoubleInputVariableInterface.setValue(	pDoubleEventSource,
																								pNewValue);
			}
		}
		mValue = pNewValue;
	}

	@Override
	public double getValue()
	{
		if (mOutputVariable != null)
			mValue = mOutputVariable.getValue();

		return mValue;
	}

	public final void sendUpdatesTo(DoubleInputVariableInterface pDoubleVariable)
	{
		synchronized (this)
		{
			if (mInputVariable == null && mInputVariables == null)
			{
				mInputVariable = pDoubleVariable;
			}
			else if (mInputVariable != null && mInputVariables == null)
			{
				mInputVariables = new CopyOnWriteArrayList<DoubleInputVariableInterface>();
				mInputVariables.add(mInputVariable);
				mInputVariables.add(pDoubleVariable);
				mInputVariable = null;
			}
			else if (mInputVariable == null && mInputVariables != null)
			{
				mInputVariables.add(pDoubleVariable);
			}
		}
	}

	public final void sendQueriesTo(DoubleOutputVariableInterface pDoubleVariable)
	{
		mOutputVariable = pDoubleVariable;
	}

	public final void syncWith(DoubleInputOutputVariableInterface pDoubleVariable)
	{
		sendUpdatesTo(pDoubleVariable);
		sendQueriesTo(pDoubleVariable);
	}



}
