package rtlib.core.variable.bundle;

import java.util.Collection;
import java.util.HashMap;

import rtlib.core.variable.VariableBase;
import rtlib.core.variable.Variable;

public class VariableBundle extends VariableBase<VariableBundle>
{

	HashMap<String, Variable<?>> mVariableNameToVariableMap = new HashMap<String, Variable<?>>();

	public VariableBundle(final String pBundleName)
	{
		super(pBundleName);
	}

	@Override
	public VariableBundle get()
	{
		return this;
	}

	protected Collection<Variable<?>> getAllVariables()
	{
		return mVariableNameToVariableMap.values();
	}

	public <O> void addVariable(final Variable<O> pVariable)
	{
		mVariableNameToVariableMap.put(pVariable.getName(), pVariable);
	}

	public <O> void removeVariable(final Variable<O> pVariable)
	{
		mVariableNameToVariableMap.remove(pVariable);
	}

	public void removeAllVariables()
	{
		mVariableNameToVariableMap.clear();
	}

	@SuppressWarnings("unchecked")
	public <O> Variable<O> getVariable(final String pVariableName)
	{
		return (Variable<O>) mVariableNameToVariableMap.get(pVariableName);
	}

	public <O> void sendUpdatesTo(final String pVariableName,
																final Variable<O> pToVariable)
	{
		final Variable<O> lFromVariable = getVariable(pVariableName);

		final Variable<O> lFromDoubleVariable = lFromVariable;
		final Variable<O> lToDoubleVariable = pToVariable;

		lFromDoubleVariable.sendUpdatesTo(lToDoubleVariable);

	}

	public <O> void doNotSendUpdatesTo(	final String pVariableName,
																			final Variable<O> pToVariable)
	{
		final Variable<O> lFromVariable = getVariable(pVariableName);

		final Variable<O> lFromDoubleVariable = lFromVariable;
		final Variable<O> lToDoubleVariable = pToVariable;

		lFromDoubleVariable.doNotSendUpdatesTo(lToDoubleVariable);

	}

	public <O> void getUpdatesFrom(	final String pVariableName,
																	final Variable<O> pFromVariable)
	{
		final Variable<O> lToVariable = getVariable(pVariableName);

		final Variable<O> lTo_DoubleVariable = lToVariable;
		final Variable<O> lFrom_DoubleVariable = pFromVariable;

		lFrom_DoubleVariable.sendUpdatesTo(lTo_DoubleVariable);

	}

	public <O> void doNotGetUpdatesFrom(final String pVariableName,
																			final Variable<O> pFromVariable)
	{
		final Variable<O> lToVariable = getVariable(pVariableName);

		final Variable<O> lTo_DoubleVariable = lToVariable;
		final Variable<O> lFrom_DoubleVariable = pFromVariable;

		lFrom_DoubleVariable.doNotSendUpdatesTo(lTo_DoubleVariable);

	}

	public <O> void syncWith(	final String pVariableName,
														final Variable<O> pVariable)
	{
		this.sendUpdatesTo(pVariableName, pVariable);
		this.getUpdatesFrom(pVariableName, pVariable);
	}

	public <O> void doNotSyncWith(final String pVariableName,
																final Variable<O> pVariable)
	{
		this.doNotSendUpdatesTo(pVariableName, pVariable);
		this.doNotGetUpdatesFrom(pVariableName, pVariable);
	}

	@Override
	public String toString()
	{
		return String.format(	"VariableBundle(%s,%s)",
													getName(),
													mVariableNameToVariableMap);
	}

}
