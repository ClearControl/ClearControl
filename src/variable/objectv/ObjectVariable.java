package variable.objectv;

public class ObjectVariable<O>	implements
																ObjectInputOutputVariableInterface<O>
{
	private volatile O mReference;
	private ObjectInputVariableInterface<O> mInputVariable;
	private ObjectOutputVariableInterface<O> mOutputVariable;

	public final void setReference(final O pNewReference)
	{
		setReference(this, pNewReference);
	}

	@Override
	public void setReference(Object pDoubleEventSource, O pNewReference)
	{
		mReference = pNewReference;
		if (mInputVariable != null)
			mInputVariable.setReference(pDoubleEventSource, pNewReference);
	}

	@Override
	public O getReference()
	{
		if (mOutputVariable != null)
			mReference = mOutputVariable.getReference();

		return mReference;
	}

	public final void sendUpdatesTo(ObjectInputVariableInterface pDoubleVariable)
	{
		mInputVariable = pDoubleVariable;
	}

	public final void sendQueriesTo(ObjectOutputVariableInterface pDoubleVariable)
	{
		mOutputVariable = pDoubleVariable;
	}

	public final void syncWith(ObjectInputOutputVariableInterface pDoubleVariable)
	{
		mInputVariable = pDoubleVariable;
		mOutputVariable = pDoubleVariable;
	}

}
