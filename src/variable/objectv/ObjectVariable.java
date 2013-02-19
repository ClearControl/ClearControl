package variable.objectv;

import java.util.ArrayList;

import frames.Frame;

import variable.doublev.DoubleInputVariableInterface;

public class ObjectVariable<O>	implements
																ObjectInputOutputVariableInterface<O>
{
	private volatile O mReference;
	private ObjectInputVariableInterface<O> mInputVariable;
	private ArrayList<ObjectInputVariableInterface<O>> mInputVariables;

	private ObjectOutputVariableInterface<O> mOutputVariable;

	public final void setReference(final O pNewReference)
	{
		setReference(this, pNewReference);
	}

	@Override
	public void setReference(Object pObjectEventSource, O pNewReference)
	{

		if (mInputVariable != null)
			mInputVariable.setReference(pObjectEventSource, pNewReference);
		else if (mInputVariables != null)
		{
			for (ObjectInputVariableInterface<O> lObjectInputVariableInterface : mInputVariables)
			{
				lObjectInputVariableInterface.setReference(	pObjectEventSource,
																										pNewReference);
			}
		}

		mReference = pNewReference;
	}

	@Override
	public O getReference()
	{
		if (mOutputVariable != null)
			mReference = mOutputVariable.getReference();

		return mReference;
	}

	public final void sendUpdatesTo(ObjectInputVariableInterface<O> pObjectVariable)
	{
		synchronized (this)
		{
			if (mInputVariable == null && mInputVariables == null)
			{
				mInputVariable = pObjectVariable;
			}
			else if (mInputVariable != null && mInputVariables == null)
			{
				mInputVariables = new ArrayList<ObjectInputVariableInterface<O>>();
				mInputVariables.add(mInputVariable);
				mInputVariables.add(pObjectVariable);
				mInputVariable = null;
			}
			else if (mInputVariable == null && mInputVariables != null)
			{
				mInputVariables.add(pObjectVariable);
			}
		}
	}
	
	public void stopSendUpdatesTo(ObjectInputVariableInterface<O> pObjectVariable)
	{
		synchronized (this)
		{
			if (mInputVariable != null && mInputVariable==pObjectVariable && mInputVariables == null)
			{
				mInputVariable = null;
			}
			else if (mInputVariable == null && mInputVariables != null)
			{
				mInputVariables.remove(pObjectVariable);
			}
		}
	}

	public final void sendQueriesTo(ObjectOutputVariableInterface pObjectVariable)
	{
		mOutputVariable = pObjectVariable;
	}

	public final void syncWith(ObjectInputOutputVariableInterface pObjectVariable)
	{
		sendUpdatesTo(pObjectVariable);
		sendQueriesTo(pObjectVariable);
	}



}
