package variable;

public class DoubleInputVariable extends AbstractInputVariable<DoubleInputVariableListener>
{
	private volatile double mValue;

	public final void setValue(final double pNewValue)
	{
		mValue = pNewValue;
		for(DoubleInputVariableListener lDoubleInputVariableListener : getListenerList())
		{
			lDoubleInputVariableListener.setValue(this,pNewValue);
		}
	}

	
}
