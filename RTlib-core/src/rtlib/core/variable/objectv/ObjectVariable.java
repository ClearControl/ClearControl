package rtlib.core.variable.objectv;

import java.util.concurrent.CopyOnWriteArrayList;

import rtlib.core.variable.EventPropagator;
import rtlib.core.variable.NamedVariable;
import rtlib.core.variable.ObjectVariableInterface;

public class ObjectVariable<O> extends NamedVariable<O>	implements
																												ObjectVariableInterface<O>,
																												ObjectInputOutputVariableInterface<O>
{
	protected volatile O mReference;
	protected final CopyOnWriteArrayList<ObjectVariable<O>> mVariablesToSendUpdatesTo = new CopyOnWriteArrayList<ObjectVariable<O>>();

	public ObjectVariable(final String pVariableName)
	{
		super(pVariableName);
		mReference = null;
	}

	public ObjectVariable(final String pVariableName, final O pReference)
	{
		super(pVariableName);
		mReference = pReference;
	}

	@Override
	public void setCurrent()
	{
		EventPropagator.clear();
		setReference(mReference);
	}
	
	public void setCurrentInternal()
	{
		setReference(mReference);
	}

	@Override
	public void set(final O pNewReference)
	{
		setReference(pNewReference);
	}

	@Override
	public void setReference(final O pNewReference)
	{
		EventPropagator.clear();
		setReferenceInternal(pNewReference);
	}

	public boolean setReferenceInternal(final O pNewReference)
	{
		if (EventPropagator.hasBeenTraversed(this))
		{
			return false;
		}

		final O lNewValueAfterHook = setEventHook(pNewReference);

		EventPropagator.add(this);
		if (mVariablesToSendUpdatesTo != null)
		{
			for (final ObjectVariable lObjectVariable : mVariablesToSendUpdatesTo)
			{
				if (EventPropagator.hasNotBeenTraversed(lObjectVariable))
				{
					lObjectVariable.setReferenceInternal(lNewValueAfterHook);
				}
			}
		}
		mReference = lNewValueAfterHook;
		return true;
	}

	public O setEventHook(final O pNewValue)
	{
		notifyListenersOfSetEvent(mReference, pNewValue);
		return pNewValue;
	}

	public O getEventHook(final O pCurrentReference)
	{
		notifyListenersOfGetEvent(pCurrentReference);
		return pCurrentReference;
	}

	@Override
	public O getReference()
	{
		return getEventHook(mReference);
	}

	@Override
	public O get()
	{
		return getReference();
	}

	@Override
	public void sendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		mVariablesToSendUpdatesTo.add(pObjectVariable);
	}

	@Override
	public void doNotSendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		mVariablesToSendUpdatesTo.remove(pObjectVariable);
	}

	@Override
	public void doNotSendAnyUpdates()
	{
		mVariablesToSendUpdatesTo.clear();
	}

	@Override
	public void syncWith(final ObjectVariable<O> pObjectVariable)
	{
		this.sendUpdatesTo(pObjectVariable);
		pObjectVariable.sendUpdatesTo(this);
	}

	@Override
	public void doNotSyncWith(final ObjectVariable<O> pObjectVariable)
	{
		this.doNotSendUpdatesTo(pObjectVariable);
		pObjectVariable.doNotSendUpdatesTo(this);
	}

	public boolean isNotNull()
	{
		return mReference != null;
	}

	public boolean isNull()
	{
		return mReference == null;
	}

}
