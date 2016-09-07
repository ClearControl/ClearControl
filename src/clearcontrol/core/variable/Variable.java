package clearcontrol.core.variable;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.concurrent.executors.CompletingThreadPoolExecutor;
import clearcontrol.core.variable.events.EventPropagator;

/**
 * 
 * 
 * @author royer
 *
 * @param <O>
 */
public class Variable<O> extends VariableBase<O> implements
																								VariableSyncInterface<O>,
																								VariableSetInterface<O>,
																								VariableGetInterface<O>

{
	static CompletingThreadPoolExecutor sAsyncExecutor;

	static
	{
		sAsyncExecutor = ClearControlExecutors.getOrCreateThreadPoolExecutor(	Variable.class,
																																					Thread.NORM_PRIORITY,
																																					1,
																																					Runtime.getRuntime()
																																									.availableProcessors() / 2,
																																					Integer.MAX_VALUE);
	}

	protected volatile O mReference;
	protected final CopyOnWriteArrayList<Variable<O>> mVariablesToSendUpdatesTo = new CopyOnWriteArrayList<Variable<O>>();

	public Variable(final String pVariableName)
	{
		super(pVariableName);
		mReference = null;
	}

	public Variable(final String pVariableName, final O pReference)
	{
		super(pVariableName);
		mReference = pReference;
	}

	/**
	 * Sets _again_ the value of the variable, listeners, synced variables and
	 * hooks are called again.
	 */
	public void setCurrent()
	{
		EventPropagator.clear();
		set(mReference);
	}

	protected void setCurrentInternal()
	{
		set(mReference);
	}

	public void setAsync(final O pNewReference)
	{
		sAsyncExecutor.execute(() -> {
			set(pNewReference);
		});
	}

	public void setEdge(O pBeforeEdge, O pAfterEdge)
	{
		set(pBeforeEdge);
		set(pAfterEdge);
	}
	
	public void setEdgeAsync(O pBeforeEdge, O pAfterEdge)
	{
		sAsyncExecutor.execute(() -> {
			setEdge(pBeforeEdge,pAfterEdge);
		});
	}

	@Override
	public void set(final O pNewReference)
	{
		EventPropagator.clear();
		setReferenceInternal(pNewReference);
	}

	@SuppressWarnings("unchecked")
	public void toggle()
	{
		if (mReference instanceof Number)
		{
			set((O) new Double(-(Double) get()));
		}
		else if (mReference instanceof Boolean)
		{
			set((O) new Boolean(!(Boolean) get()));
		}
	}

	public boolean setReferenceInternal(final O pNewReference)
	{
		if (EventPropagator.hasBeenTraversed(this))
		{
			return false;
		}

		final O lNewValueAfterHook = setEventHook(mReference,
																							pNewReference);

		EventPropagator.add(this);
		if (mVariablesToSendUpdatesTo != null)
		{
			for (final Variable<O> lObjectVariable : mVariablesToSendUpdatesTo)
			{
				if (EventPropagator.hasNotBeenTraversed(lObjectVariable))
				{
					lObjectVariable.setReferenceInternal(lNewValueAfterHook);
				}
			}
		}

		final O lOldReference = mReference;
		mReference = lNewValueAfterHook;

		notifyListenersOfSetEvent(lOldReference, lNewValueAfterHook);
		if (lOldReference != null && lNewValueAfterHook != null
				&& !lOldReference.equals(lNewValueAfterHook))
			notifyListenersOfEdgeEvent(lOldReference, lNewValueAfterHook);

		return true;
	}

	public void sync(final O pNewValue, final boolean pClearEventQueue)
	{
		if (pClearEventQueue)
		{
			EventPropagator.clear();
		}

		// We protect ourselves from called code that might clear the Thread
		// traversal list:
		final ArrayList<Object> lCopyOfListOfTraversedObjects = EventPropagator.getCopyOfListOfTraversedObjects();

		if (mVariablesToSendUpdatesTo != null)
		{
			for (final Variable<O> lObjectVariable : mVariablesToSendUpdatesTo)
			{
				EventPropagator.setListOfTraversedObjects(lCopyOfListOfTraversedObjects);
				if (EventPropagator.hasNotBeenTraversed(lObjectVariable))
				{
					lObjectVariable.setReferenceInternal(pNewValue);
				}
			}
		}
		EventPropagator.setListOfTraversedObjects(lCopyOfListOfTraversedObjects);
		EventPropagator.addAllToListOfTraversedObjects(mVariablesToSendUpdatesTo);

	}

	public O setEventHook(final O pOldValue, final O pNewValue)
	{
		return pNewValue;
	}

	public O getEventHook(final O pCurrentReference)
	{
		return pCurrentReference;
	}

	@Override
	public O get()
	{
		final O lNewReferenceAfterHook = getEventHook(mReference);
		notifyListenersOfGetEvent(lNewReferenceAfterHook);
		return lNewReferenceAfterHook;
	}

	@Override
	public void sendUpdatesTo(final Variable<O> pObjectVariable)
	{
		if (!mVariablesToSendUpdatesTo.contains(pObjectVariable))
			mVariablesToSendUpdatesTo.add(pObjectVariable);
	}

	@Override
	public void doNotSendUpdatesTo(final Variable<O> pObjectVariable)
	{
		mVariablesToSendUpdatesTo.remove(pObjectVariable);
	}

	@Override
	public void doNotSendAnyUpdates()
	{
		mVariablesToSendUpdatesTo.clear();
	}

	public Variable<O> sendUpdatesToInstead(Variable<O> pObjectVariable)
	{

		Variable<O> lObjectVariable = null;
		if (mVariablesToSendUpdatesTo.size() == 0)
		{
			if (pObjectVariable == null)
				return null;
			mVariablesToSendUpdatesTo.add(pObjectVariable);
		}
		else if (mVariablesToSendUpdatesTo.size() == 1)
		{
			if (pObjectVariable == null)
				return mVariablesToSendUpdatesTo.get(0);
			lObjectVariable = mVariablesToSendUpdatesTo.get(0);
			mVariablesToSendUpdatesTo.set(0, pObjectVariable);
		}
		else if (mVariablesToSendUpdatesTo.size() > 1)
		{
			if (pObjectVariable == null)
				return mVariablesToSendUpdatesTo.get(0);

			lObjectVariable = mVariablesToSendUpdatesTo.get(0);
			mVariablesToSendUpdatesTo.clear();
			mVariablesToSendUpdatesTo.add(pObjectVariable);
		}

		return lObjectVariable;
	}

	@Override
	public void syncWith(final Variable<O> pObjectVariable)
	{
		this.sendUpdatesTo(pObjectVariable);
		pObjectVariable.sendUpdatesTo(this);
	}

	@Override
	public void doNotSyncWith(final Variable<O> pObjectVariable)
	{
		this.doNotSendUpdatesTo(pObjectVariable);
		pObjectVariable.doNotSendUpdatesTo(this);
	}

	@SuppressWarnings("unchecked")
	public void increment()
	{
		if (mReference instanceof Long)
		{
			Long lLong = (Long) mReference;
			set((O) (new Long(lLong + 1)));
		}
		else if (mReference instanceof Integer)
		{
			Integer lInteger = (Integer) mReference;
			set((O) (new Integer(lInteger + 1)));
		}
		else if (mReference instanceof Short)
		{
			Short lShort = (Short) mReference;
			set((O) (new Short((short) (lShort + 1))));
		}
		else if (mReference instanceof Character)
		{
			Character lCharacter = (Character) mReference;
			set((O) (new Character((char) (lCharacter + 1))));
		}
		else if (mReference instanceof Byte)
		{
			Byte lByte = (Byte) mReference;
			set((O) (new Byte((byte) (lByte + 1))));
		}
		else
			throw new UnsupportedOperationException("Can't increment if not of type char, short, int or long");
	}

	@SuppressWarnings("unchecked")
	public void decrement()
	{
		if (mReference instanceof Long)
		{
			Long lLong = (Long) mReference;
			set((O) (new Long(lLong - 1)));
		}
		else if (mReference instanceof Integer)
		{
			Integer lInteger = (Integer) mReference;
			set((O) (new Integer(lInteger - 1)));
		}
		else if (mReference instanceof Short)
		{
			Short lShort = (Short) mReference;
			set((O) (new Short((short) (lShort - 1))));
		}
		else if (mReference instanceof Character)
		{
			Character lCharacter = (Character) mReference;
			set((O) (new Character((char) (lCharacter - 1))));
		}
		else if (mReference instanceof Byte)
		{
			Byte lByte = (Byte) mReference;
			set((O) (new Byte((byte) (lByte - 1))));
		}
		else
			throw new UnsupportedOperationException("Can't increment if not of type char, short, int or long");
	}

	public boolean isNotNull()
	{
		return mReference != null;
	}

	public boolean isNull()
	{
		return mReference == null;
	}

	@Override
	public String toString()
	{
		try
		{
			return getName() + "="
							+ ((mReference == null)	? "null"
																			: mReference.toString());
		}
		catch (final NullPointerException e)
		{
			return getName() + "=null";
		}
	}

}
