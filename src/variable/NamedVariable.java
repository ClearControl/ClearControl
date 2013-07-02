package variable;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class NamedVariable<O>
{

	private String mVariableName;

	private final CopyOnWriteArrayList<VariableListener<O>> mVariableListeners = new CopyOnWriteArrayList<VariableListener<O>>();

	public NamedVariable(final String pVariableName)
	{
		super();
		mVariableName = pVariableName;
	}

	public void addListener(VariableListener<O> pDoubleVariableListener)
	{
		mVariableListeners.add(pDoubleVariableListener);
	}

	public void removeListener(VariableListener<O> pDoubleVariableListener)
	{
		mVariableListeners.remove(pDoubleVariableListener);
	}

	public void removeAllListener()
	{
		mVariableListeners.clear();
	}

	public CopyOnWriteArrayList<VariableListener<O>> getVariableListeners()
	{
		return mVariableListeners;
	}

	public void notifyListenersOfSetEvent(final O pCurentValue, O pNewValue)
	{
		for (VariableListener<O> lDoubleVariableListener : getVariableListeners())
		{
			lDoubleVariableListener.setEvent(pCurentValue,pNewValue);
		}
	}

	public void notifyListenersOfGetEvent(final O pCurrentValue)
	{
		for (VariableListener<O> lDoubleVariableListener : getVariableListeners())
		{
			lDoubleVariableListener.getEvent(pCurrentValue);
		}
	}

	public String getName()
	{
		return mVariableName;
	}

	public void setVariableName(final String variableName)
	{
		mVariableName = variableName;
	}

	public abstract O get();

	@Override
	public String toString()
	{
		return String.format(	"NamedVariable [mVariableName=%s]",
													getName(),
													get().toString());
	}

}
