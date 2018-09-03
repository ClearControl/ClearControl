package clearcontrol.core.device.queue;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.variable.Variable;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * The state of variables register to instances of this class can be recorded
 * into queues.
 *
 * @author royer
 */
public class VariableQueueBase implements QueueInterface, Cloneable
{
  private ConcurrentHashMap<Variable<?>, ArrayList<Object>> mVariablesToQueueListsMap =
                                                                                      new ConcurrentHashMap<>();

  private Object mLock = new Object();

  /**
   * Instanciates.
   */
  public VariableQueueBase()
  {
    super();
  }

  /**
   * Instanciates a copy of this variable state queue object.
   * 
   * @param pVariableStateQueues
   *          state queues object to copy.
   */
  public VariableQueueBase(VariableQueueBase pVariableStateQueues)
  {
    super();

    synchronized (pVariableStateQueues.mLock)
    {
      for (Entry<Variable<?>, ArrayList<Object>> lEntrySet : pVariableStateQueues.mVariablesToQueueListsMap.entrySet())
      {
        Variable<?> lVariable = lEntrySet.getKey();

        ArrayList<Object> lQueueStatesList =
                                           new ArrayList<>(lEntrySet.getValue());

        mVariablesToQueueListsMap.put(lVariable, lQueueStatesList);
      }
    }

  }

  @Override
  public VariableQueueBase clone()
  {
    return new VariableQueueBase(this);
  }

  /**
   * Register a list of variables with normal queueing mode.
   * 
   * @param pVariables
   *          var arg list of variables to register
   */
  public void registerVariables(Variable<?>... pVariables)
  {
    for (Variable<?> lVariable : pVariables)
    {
      registerVariable(lVariable);
    }
  }

  /**
   * Register a variable.
   * 
   * @param pVariable
   *          variable
   */
  public <T> void registerVariable(Variable<T> pVariable)
  {
    synchronized (mLock)
    {
      ArrayList<Object> lPair = new ArrayList<Object>();
      mVariablesToQueueListsMap.put(pVariable, lPair);
    }
  }

  /**
   * Returns the boolean value of a given variable at a given position of the
   * queue.
   * 
   * @param pVariable
   *          variable
   * @param pQueuePositionIndex
   *          position in queue
   * @return boolean value
   */
  public Boolean getQueuedBooleanValue(Variable<Boolean> pVariable,
                                       int pQueuePositionIndex)

  {
    Boolean lValue =
                   (Boolean) mVariablesToQueueListsMap.get(pVariable)
                                                      .get(pQueuePositionIndex);
    return lValue;
  }

  /**
   * Returns the value of a given variable at a given position of the queue.
   * 
   * @param pVariable
   *          variable
   * @param pQueuePositionIndex
   *          position in queue
   * @return value
   */
  public Number getQueuedValue(Variable<Number> pVariable,
                               int pQueuePositionIndex)

  {
    Number lValue =
                  (Number) mVariablesToQueueListsMap.get(pVariable)
                                                    .get(pQueuePositionIndex);
    return lValue;
  }

  /**
   * Returns the value of a given variable at a given position of the queue
   * after transforming with a given univariate function.
   * 
   * @param pFunction
   *          univariate function
   * @param pVariable
   *          variable
   * @param pQueuePositionIndex
   *          position in queue
   * @return value
   */
  public Number getQueuedValue(UnivariateFunction pFunction,
                               Variable<Number> pVariable,
                               int pQueuePositionIndex)

  {
    Number lValue = getQueuedValue(pVariable, pQueuePositionIndex);
    Number lTransformedValue = pFunction.value(lValue.doubleValue());
    return lTransformedValue;
  }

  /**
   * Returns the list of states from the current queue of a given variable. This
   * is an actual copy of the original list of states, this means that it is not
   * altered by subsequent clearing or modifications of the state queue.
   * 
   * @param pVariable
   *          variable
   * @return state queue as list
   */
  public <T> ArrayList<T> getVariableQueue(Variable<T> pVariable)
  {
    @SuppressWarnings("unchecked")
    ArrayList<T> lArrayList =
                            (ArrayList<T>) new ArrayList<>(mVariablesToQueueListsMap.get(pVariable));
    return lArrayList;
  }

  /**
   * Returns the list of states from the current queue of a given variable after
   * transforming these values using the given univariate function. This is an
   * actual copy of the original list of states, this means that it is not
   * altered by subsequent clearing or modifications of the state queue.
   * 
   * @param pFunction
   *          function to apply to each enqueued state value
   * @param pValueVariable
   *          variable
   * @return list of state values transformed using the given function.
   */
  public ArrayList<Number> getVariableQueue(UnivariateFunction pFunction,
                                            Variable<Number> pValueVariable)
  {
    ArrayList<Number> lTransformedValueList = new ArrayList<Number>();

    ArrayList<Object> lStateList =
                                 mVariablesToQueueListsMap.get(pValueVariable);

    if (lStateList.size() == 0)
      return lTransformedValueList;

    if (!(lStateList.get(0) instanceof Number))
      throw new IllegalArgumentException("Should be a variable of type Number");

    for (Object lObject : lStateList)
    {
      Number lNumber = (Number) lObject;
      double lTransformedValue =
                               pFunction.value(lNumber.doubleValue());
      lTransformedValueList.add(lTransformedValue);
    }

    return lTransformedValueList;
  }

  @Override
  public void clearQueue()
  {
    synchronized (mLock)
    {
      for (ArrayList<Object> lStateList : mVariablesToQueueListsMap.values())
      {
        lStateList.clear();
      }
    }
  }

  @Override
  public void addCurrentStateToQueue()
  {
    synchronized (mLock)
    {
      for (Entry<Variable<?>, ArrayList<Object>> lEntrySet : mVariablesToQueueListsMap.entrySet())
      {
        Variable<?> lVariable = lEntrySet.getKey();
        Object lCurrentValue = lVariable.get();

        ArrayList<Object> lStateList = lEntrySet.getValue();

        lStateList.add(lCurrentValue);
      }
    }
  }

  @Override
  public void finalizeQueue()
  {
    // nothing to do here.
  }

  @Override
  public int getQueueLength()
  {
    synchronized (mLock)
    {
      return mVariablesToQueueListsMap.values()
                                      .iterator()
                                      .next()
                                      .size();
    }
  }

}
