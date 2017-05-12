package clearcontrol.core.variable;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.core.concurrent.executors.ClearControlExecutors;
import clearcontrol.core.concurrent.executors.CompletingThreadPoolExecutor;
import clearcontrol.core.variable.events.EventPropagator;

/**
 * Variable. variable can be used to hold references, listen to changes of these
 * values, and can be synced with each other among other things. equals and
 * hascode are determined solely based on the variable name, not the reference
 * it holds.
 * 
 * @author royer
 *
 * @param <O>
 *          value type
 */
public class Variable<O> extends VariableBase<O> implements
                     VariableSyncInterface<O>,
                     VariableSetInterface<O>,
                     VariableGetInterface<O>

{

  // The following async executor is used for the async set functions:
  static CompletingThreadPoolExecutor sAsyncExecutor;
  static
  {
    sAsyncExecutor =
                   ClearControlExecutors.getOrCreateThreadPoolExecutor(Variable.class,
                                                                       Thread.NORM_PRIORITY,
                                                                       1,
                                                                       Runtime.getRuntime()
                                                                              .availableProcessors()
                                                                          / 2,
                                                                       Integer.MAX_VALUE);
  }

  // That where we store the value:
  protected volatile O mReference;

  // list of variable to send updates to:
  protected final CopyOnWriteArrayList<Variable<O>> mVariablesToSendUpdatesTo =
                                                                              new CopyOnWriteArrayList<Variable<O>>();

  /**
   * Instanciates a variabke witha given name and null reference
   * 
   * @param pVariableName
   *          variale name
   */
  public Variable(final String pVariableName)
  {
    super(pVariableName);
    mReference = null;
  }

  /**
   * Instanciates a variable with given name and initial value.
   * 
   * @param pVariableName
   *          variable name
   * @param pReference
   *          initial value
   */
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

  /**
   * Sets the reference value asynchronously
   * 
   * @param pNewReference
   *          new value
   */
  public void setAsync(final O pNewReference)
  {
    sAsyncExecutor.execute(() -> {
      set(pNewReference);
    });
  }

  /**
   * Sets a edge: first the before-edge value , then immediately the after-edge
   * value.
   * 
   * @param pBeforeEdge
   *          value before edge
   * @param pAfterEdge
   *          value after edge
   */
  public void setEdge(O pBeforeEdge, O pAfterEdge)
  {
    set(pBeforeEdge);
    set(pAfterEdge);
  }

  /**
   * Sets a edge asynchronously: first the before-edge value , then immediately
   * the after-edge value.
   * 
   * @param pBeforeEdge
   *          value before edge
   * @param pAfterEdge
   *          value after edge
   */
  public void setEdgeAsync(O pBeforeEdge, O pAfterEdge)
  {
    sAsyncExecutor.execute(() -> {
      setEdge(pBeforeEdge, pAfterEdge);
    });
  }

  @Override
  public void set(final O pNewReference)
  {
    EventPropagator.clear();
    setReferenceInternal(pNewReference);
    EventPropagator.clear();
  }

  /**
   * Convenience method that sets the value of the given variable to this
   * variable
   * 
   * @param pVariable
   *          variable
   */
  public void set(Variable<O> pVariable)
  {
    set(pVariable.get());
  }

  /**
   * Toggles the value, fou double it simply changes the sign, for boolean
   * values it toggles between false and true.
   */
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

  private boolean setReferenceInternal(final O pNewReference)
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

  /**
   * Sends a new value to the variables synced to this variable - this is
   * normally called internally when setting the value. This should be only used
   * if you know what you are doing...
   * 
   * @param pNewValue
   *          new value to send
   * @param pClearEventQueue
   *          true -> clears event queue
   */
  public void sync(final O pNewValue, final boolean pClearEventQueue)
  {
    if (pClearEventQueue)
    {
      EventPropagator.clear();
    }

    // We protect ourselves from called code that might clear the Thread
    // traversal list:
    final ArrayList<Object> lCopyOfListOfTraversedObjects =
                                                          EventPropagator.getCopyOfListOfTraversedObjects();

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

  /**
   * Set event hook, can be overridden by a derived class to intercept set
   * eventsand modify teh set value before it is propagated.
   * 
   * @param pOldReference
   *          old value
   * @param pNewReference
   *          new value
   * @return possibly modified new value
   */
  public O setEventHook(final O pOldReference, final O pNewReference)
  {
    return pNewReference;
  }

  /**
   * Get event hook, can be overridden by a derived class to intercept set
   * eventsand modify the set value before it is returned by a call to get.
   * 
   * @param pCurrentReference
   *          current value
   * @return possibly modified current value
   */
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

  /**
   * Removes all synced variable from this variable and sets a new sigle synced
   * variable
   * 
   * @param pObjectVariable
   *          sole vaiable to sync to.
   * @return first variable that used to be synced
   */
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

  /**
   * Increments the value by 1 (only works for numeric types)
   */
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

  /**
   * Decrements the value by 1 (only works for numeric types)
   */
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

  /**
   * Returns true if the reference is not null.
   * 
   * @return true if not null
   */
  public boolean isNotNull()
  {
    return mReference != null;
  }

  /**
   * Returns true if the reference is null
   * 
   * @return true if null
   */
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
             + ((mReference == null) ? "null"
                                     : mReference.toString());
    }
    catch (final NullPointerException e)
    {
      return getName() + "=null";
    }
  }

}
