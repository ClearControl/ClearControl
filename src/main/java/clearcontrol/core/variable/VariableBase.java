package clearcontrol.core.variable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Base class for variables.
 *
 * @param <O>
 *          reference type
 * @author royer
 */
public abstract class VariableBase<O>
{

  private final String mVariableName;

  private final CopyOnWriteArrayList<VariableSetListener<O>> mVariableSetListeners =
                                                                                   new CopyOnWriteArrayList<VariableSetListener<O>>();
  private final CopyOnWriteArrayList<VariableEdgeListener<O>> mVariableEdgeListeners =
                                                                                     new CopyOnWriteArrayList<VariableEdgeListener<O>>();
  private final CopyOnWriteArrayList<VariableGetListener<O>> mVariableGetListeners =
                                                                                   new CopyOnWriteArrayList<VariableGetListener<O>>();

  /**
   * Instaciates a variable
   * 
   * @param pVariableName
   *          variable name
   */
  public VariableBase(final String pVariableName)
  {
    super();
    mVariableName = pVariableName;
  }

  /**
   * Adds a variable listener
   * 
   * @param pVariableListener
   *          variable listener
   */
  public void addListener(final VariableListener<O> pVariableListener)
  {
    if (!mVariableSetListeners.contains(pVariableListener))
      mVariableSetListeners.add(pVariableListener);
    if (!mVariableGetListeners.contains(pVariableListener))
      mVariableGetListeners.add(pVariableListener);
  }

  /**
   * Removes a listener
   * 
   * @param pVariableListener
   *          variable listener
   */
  public void removeListener(final VariableListener<O> pVariableListener)
  {
    mVariableSetListeners.remove(pVariableListener);
    mVariableGetListeners.remove(pVariableListener);
  }

  /**
   * Adds a set listener
   * 
   * @param pVariableSetListener
   *          set listener
   */
  public void addSetListener(final VariableSetListener<O> pVariableSetListener)
  {
    if (!mVariableSetListeners.contains(pVariableSetListener))
      mVariableSetListeners.add(pVariableSetListener);
  }

  /**
   * Adds edge listener
   * 
   * @param pVariableEdgeListener
   *          edge listener
   */
  public void addEdgeListener(final VariableEdgeListener<O> pVariableEdgeListener)
  {
    if (!mVariableEdgeListeners.contains(pVariableEdgeListener))
      mVariableEdgeListeners.add(pVariableEdgeListener);
  }

  /**
   * Adds get listener
   * 
   * @param pVariableGetListener
   *          get listener
   */
  public void addGetListener(final VariableGetListener<O> pVariableGetListener)
  {
    if (!mVariableGetListeners.contains(pVariableGetListener))
      mVariableGetListeners.add(pVariableGetListener);
  }

  /**
   * Adds set listener
   * 
   * @param pVariableSetListener
   *          set listener
   */
  public void removeSetListener(final VariableSetListener<O> pVariableSetListener)
  {
    mVariableSetListeners.remove(pVariableSetListener);
  }

  /**
   * Adds get listener
   * 
   * @param pVariableGetListener
   *          get listener
   */
  public void removeGetListener(final VariableGetListener<O> pVariableGetListener)
  {
    mVariableGetListeners.remove(pVariableGetListener);
  }

  /**
   * Adds edge listener
   * 
   * @param pVariableEdgeListener
   *          edge listener
   */
  public void removeEdgeListener(final VariableEdgeListener<O> pVariableEdgeListener)
  {
    mVariableEdgeListeners.remove(pVariableEdgeListener);
  }

  /**
   * Removes all set listeners
   */
  public void removeAllSetListeners()
  {
    mVariableSetListeners.clear();
  }

  /**
   * Removes all get listeners
   */
  public void removeAllGetListeners()
  {
    mVariableGetListeners.clear();
  }

  /**
   * Removes all listeners
   */
  public void removeAllListeners()
  {
    mVariableSetListeners.clear();
    mVariableGetListeners.clear();
    mVariableEdgeListeners.clear();
  }

  /**
   * Returns the internal list of set listeners
   * 
   * @return set listeners list
   */
  protected CopyOnWriteArrayList<VariableSetListener<O>> getVariableSetListeners()
  {
    return mVariableSetListeners;
  }

  /**
   * Returns the internal list of edge listeners
   * 
   * @return edge listeners list
   */
  protected CopyOnWriteArrayList<VariableEdgeListener<O>> getVariableEdgeListeners()
  {
    return mVariableEdgeListeners;
  }

  /**
   * Returns the internal list of get listeners
   * 
   * @return get listeners list
   */
  protected CopyOnWriteArrayList<VariableGetListener<O>> getVariableGetListeners()
  {
    return mVariableGetListeners;
  }

  protected void notifyListenersOfSetEvent(final O pCurentValue,
                                           final O pNewValue)
  {
    for (final VariableSetListener<O> lVariableListener : getVariableSetListeners())
    {
      lVariableListener.setEvent(pCurentValue, pNewValue);
    }
  }

  protected void notifyListenersOfEdgeEvent(final O pCurentValue,
                                            final O pNewValue)
  {
    for (final VariableEdgeListener<O> lVariableListener : getVariableEdgeListeners())
    {
      lVariableListener.fire(pNewValue);
    }
  }

  protected void notifyListenersOfGetEvent(final O pCurrentValue)
  {
    for (final VariableGetListener<O> lVariableListener : getVariableGetListeners())
    {
      lVariableListener.getEvent(pCurrentValue);
    }
  }

  /**
   * Waits for the _exact_ object reference to be set to this variable. This is
   * rarely useful and it's often safer to use instead waitForEqualsTo(...).
   * This method does not poll, and therefore is the best way to wait for
   * events. (no CPU hogging)
   * 
   * @param pValueToWaitFor
   *          value to wait for
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true -> success
   */
  public boolean waitForSameAs(final O pValueToWaitFor,
                               final long pTimeOut,
                               final TimeUnit pTimeUnit)
  {
    CountDownLatch lCountDownLatch = new CountDownLatch(1);

    VariableSetListener<O> lListener = (o, n) -> {
      if (n == pValueToWaitFor)
      {
        lCountDownLatch.countDown();
      }
    };

    addSetListener(lListener);
    try
    {
      try
      {
        return lCountDownLatch.await(pTimeOut, pTimeUnit);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    finally
    {
      removeSetListener(lListener);
    }
    return false;
  }

  /**
   * Waits for the an object that equals the given object. This method does not
   * poll, and therefore is the best way to wait for events. (no CPU hogging)
   * 
   * @param pValueToWaitFor
   *          value to wait for
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true -> success
   */
  public boolean waitForEqualsTo(final O pValueToWaitFor,
                                 final long pTimeOut,
                                 final TimeUnit pTimeUnit)
  {
    CountDownLatch lCountDownLatch = new CountDownLatch(1);

    VariableSetListener<O> lListener = (o, n) -> {
      if (n.equals(pValueToWaitFor))
      {
        lCountDownLatch.countDown();
      }
    };

    addSetListener(lListener);
    try
    {
      return lCountDownLatch.await(pTimeOut, pTimeUnit);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    finally
    {
      removeSetListener(lListener);
    }
    return false;
  }

  /**
   * Waits for the an object that equals the given object. This method does not
   * poll, and therefore is the best way to wait for events. (no CPU hogging)
   * 
   * @param pNewValueToWaitFor
   *          value to wait for
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true -> success
   */
  public boolean waitForEdge(final O pNewValueToWaitFor,
                             final long pTimeOut,
                             final TimeUnit pTimeUnit)
  {
    CountDownLatch lCountDownLatch = new CountDownLatch(1);

    VariableEdgeListener<O> lListener = (n) -> {
      if (n.equals(pNewValueToWaitFor))
      {
        lCountDownLatch.countDown();
      }
    };

    addEdgeListener(lListener);
    try
    {
      return lCountDownLatch.await(pTimeOut, pTimeUnit);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
    finally
    {
      removeEdgeListener(lListener);
    }
    return false;
  }

  /**
   * Returns the variable name
   * 
   * @return variable name
   */
  public String getName()
  {
    return mVariableName;
  }

  /**
   * Returns the current value.
   * 
   * @return current value
   */
  public abstract O get();

}
