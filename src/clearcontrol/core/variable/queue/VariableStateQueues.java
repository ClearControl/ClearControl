package clearcontrol.core.variable.queue;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.device.queue.StateQueueInterface;
import clearcontrol.core.variable.Variable;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * The state of variables register to instances of this class can be recorded
 * into queues. This is practical for implementinig queable devices.
 *
 * @author royer
 */
public class VariableStateQueues implements
                                 StateQueueInterface,
                                 Cloneable
{
  private ConcurrentHashMap<Variable<?>, Pair<QueueingMode, ArrayList<Object>>> mVariablesToQueueListsMap =
                                                                                                          new ConcurrentHashMap<>();

  private Object mLock = new Object();

  /**
   * Instanciates.
   */
  public VariableStateQueues()
  {
    super();
  }

  /**
   * Instanciates a copy of this variable state queues object.
   * 
   * @param pVariableStateQueues
   *          state queues object to copy.
   */
  public VariableStateQueues(VariableStateQueues pVariableStateQueues)
  {
    super();

    synchronized (pVariableStateQueues.mLock)
    {
      for (Entry<Variable<?>, Pair<QueueingMode, ArrayList<Object>>> lEntrySet : pVariableStateQueues.mVariablesToQueueListsMap.entrySet())
      {
        Variable<?> lVariable = lEntrySet.getKey();
        QueueingMode lQueueingMode = lEntrySet.getValue().getLeft();
        ArrayList<Object> lQueueStatesList =
                                           new ArrayList<>(lEntrySet.getValue()
                                                                    .getRight());

        mVariablesToQueueListsMap.put(lVariable,
                                      Pair.of(lQueueingMode,
                                              lQueueStatesList));
      }
    }

  }

  @Override
  public VariableStateQueues clone()
  {
    return new VariableStateQueues(this);
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
   * Register a list of variables with constant queueing mode.
   * 
   * @param pVariables
   *          pVariables var arg list of variables to register
   */
  public void registerConstantVariables(Variable<?>... pVariables)
  {
    for (Variable<?> lVariable : pVariables)
    {
      registerConstantVariable(lVariable);
    }
  }

  /**
   * Register a variable with normal queueing mode.
   * 
   * @param pQueueingMode
   *          queueing mode
   * @param pVariable
   *          variable
   */
  public <T> void registerVariable(QueueingMode pQueueingMode,
                                   Variable<T> pVariable)
  {
    synchronized (mLock)
    {
      Pair<QueueingMode, ArrayList<Object>> lPair =
                                                  Pair.of(pQueueingMode,
                                                          new ArrayList<Object>());
      mVariablesToQueueListsMap.put(pVariable, lPair);
    }
  }

  /**
   * Register a variable with normal queueing mode.
   * 
   * @param pVariable
   *          variable
   */
  public <T> void registerVariable(Variable<T> pVariable)
  {
    registerVariable(QueueingMode.Normal, pVariable);
  }

  /**
   * Register a variable with constant queueing mode.
   * 
   * @param pVariable
   *          variable
   */
  public <T> void registerConstantVariable(Variable<T> pVariable)
  {
    registerVariable(QueueingMode.Constant, pVariable);
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
                                                    .getValue()
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
                            (ArrayList<T>) new ArrayList<>(mVariablesToQueueListsMap.get(pVariable)
                                                                                    .getRight());
    return lArrayList;
  }

  /**
   * Returns the list of states from the current queue of a given variable after
   * transforming these values using the given univariate function. This is an
   * actual copy of the original list of states, this means that it is not
   * altered by subsequent clearing or modifications of the state queue.
   * 
   * @param pFunctions
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
                                 mVariablesToQueueListsMap.get(pValueVariable)
                                                          .getRight();

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
      for (Pair<QueueingMode, ArrayList<Object>> lStateList : mVariablesToQueueListsMap.values())
      {
        lStateList.getRight().clear();
      }
    }
  }

  @Override
  public void addCurrentStateToQueue()
  {
    synchronized (mLock)
    {
      for (Entry<Variable<?>, Pair<QueueingMode, ArrayList<Object>>> lEntrySet : mVariablesToQueueListsMap.entrySet())
      {
        Variable<?> lVariable = lEntrySet.getKey();
        Object lCurrentValue = lVariable.get();
        QueueingMode lQueueingMode = lEntrySet.getValue().getLeft();
        ArrayList<Object> lStateList =
                                     lEntrySet.getValue().getRight();

        if (lQueueingMode == QueueingMode.Constant
            && lStateList.size() >= 1)
        {
          if (lStateList.get(0).equals(lCurrentValue))
            lStateList.add(lCurrentValue);
          else
            throw new InvalidQueueException(String.format("Variable should be constant in queue: %s",
                                                          lVariable));
        }
        else
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
                                      .getRight()
                                      .size();
    }
  }

}
