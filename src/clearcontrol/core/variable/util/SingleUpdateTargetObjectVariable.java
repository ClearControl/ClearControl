package clearcontrol.core.variable.util;

import clearcontrol.core.variable.Variable;

public class SingleUpdateTargetObjectVariable<O> extends Variable<O>
{

	public SingleUpdateTargetObjectVariable(final String pVariableName)
	{
		super(pVariableName);
	}

	public SingleUpdateTargetObjectVariable(final String pVariableName,
																					final O pReference)
	{
		super(pVariableName, pReference);
	}

	@Override
	public final void sendUpdatesTo(final Variable<O> pObjectVariable)
	{
		if (mVariablesToSendUpdatesTo.size() != 0)
		{
			throw new IllegalArgumentException(this.getClass()
																							.getSimpleName() + ": cannot send updates to more  than one peer! (sending to one peer registered already)");
		}

		mVariablesToSendUpdatesTo.add(pObjectVariable);
	}

	@Override
	public final Variable<O> sendUpdatesToInstead(final Variable<O> pObjectVariable)
	{
		if (mVariablesToSendUpdatesTo.size() >= 2)
		{
			throw new IllegalArgumentException(this.getClass()
																							.getSimpleName() + ": cannot send updates to more than one peer! (more than 1 peer is registered already)");
		}

		mVariablesToSendUpdatesTo.clear();

		if (pObjectVariable == null)
		{
			if (mVariablesToSendUpdatesTo.isEmpty())
			{
				return null;
			}
			else
			{
				final Variable<O> lPreviousObjectVariable = mVariablesToSendUpdatesTo.get(0);
				return lPreviousObjectVariable;
			}
		}

		if (mVariablesToSendUpdatesTo.isEmpty())
		{
			mVariablesToSendUpdatesTo.add(pObjectVariable);
			return null;
		}
		else
		{
			final Variable<O> lPreviousObjectVariable = mVariablesToSendUpdatesTo.get(0);
			mVariablesToSendUpdatesTo.add(pObjectVariable);
			return lPreviousObjectVariable;
		}
	}

}
