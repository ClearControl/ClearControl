package variable.objectv;

import java.util.concurrent.CopyOnWriteArrayList;

import variable.EventPropagator;
import variable.NamedVariable;
import variable.ObjectVariableInterface;

public class ObjectVariable<O> extends NamedVariable<O>	implements
																												ObjectVariableInterface<O>,
																												ObjectInputOutputVariableInterface<O>
{
	protected volatile O mReference;
	private final CopyOnWriteArrayList<ObjectVariable<O>> mInputVariables = new CopyOnWriteArrayList<ObjectVariable<O>>();

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
			return false;

		final O lNewValueAfterHook = setEventHook(pNewReference);

		EventPropagator.add(this);
		if (mInputVariables != null)
		{
			for (final ObjectVariable lObjectVariable : mInputVariables)
				if (EventPropagator.hasNotBeenTraversed(lObjectVariable))
				{
					lObjectVariable.setReferenceInternal(lNewValueAfterHook);
				}
		}
		mReference = lNewValueAfterHook;
		return true;
	}

	public O setEventHook(final O pNewValue)
	{
		return pNewValue;
	}

	public O getEventHook(final O pCurrentReference)
	{
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
	public final void sendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		mInputVariables.add(pObjectVariable);
	}

	@Override
	public final void doNotSendUpdatesTo(final ObjectVariable<O> pObjectVariable)
	{
		mInputVariables.remove(pObjectVariable);
	}

	@Override
	public final void doNotSendAnyUpdates()
	{
		mInputVariables.clear();
	}

	@Override
	public final void syncWith(final ObjectVariable<O> pObjectVariable)
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
