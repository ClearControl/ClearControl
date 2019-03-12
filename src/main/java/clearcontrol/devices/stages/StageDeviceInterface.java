package clearcontrol.devices.stages;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.device.name.NameableInterface;
import clearcontrol.core.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.core.variable.Variable;

/**
 * StageDeviceInterface is the interface for all motorized stages. It offers a
 * standard interface to access different degree of freedoms (DOFs) and set
 * their target position, request the current position, homing, resetting and
 * more.
 * 
 * @author royer
 */
public interface StageDeviceInterface extends
                                      NameableInterface,
                                      OpenCloseDeviceInterface,
                                      WaitingInterface
{
  /**
   * Returns the stage type.
   * 
   * @return stage type.
   */
  StageType getStageType();

  /**
   * Returns the number of DOFs
   * 
   * @return number of DOFs
   */
  int getNumberOfDOFs();

  /**
   * Returns the DOF index for agiven name.
   * 
   * @param pName
   *          DOF name
   * @return corresponding index
   */
  int getDOFIndexByName(String pName);

  /**
   * Returns the DOF name for a given index.
   * 
   * @param pDOFIndex
   *          DOF index
   * @return DOF name
   */
  String getDOFNameByIndex(int pDOFIndex);

  /**
   * Resets a given DOF.
   * 
   * @param pDOFIndex
   *          DOF's index
   */
  public default void reset(int pDOFIndex)
  {
    getResetVariable(pDOFIndex).setEdge(false, true);
  }

  /**
   * Homes a given DOF.
   * 
   * @param pDOFIndex
   *          DOF's index
   */
  public default void home(int pDOFIndex)
  {
    getHomingVariable(pDOFIndex).setEdge(false, true);
  }

  /**
   * Enables a given DOF.
   * 
   * @param pIndex
   *          DOF's index
   */
  public default void enable(int pIndex)
  {
    getEnableVariable(pIndex).setEdge(false, true);
  }

  /**
   * Enables all DOFs
   */
  public default void enable()
  {
    int lNumberOfDOFs = getNumberOfDOFs();
    for (int i = 0; i < lNumberOfDOFs; i++)
      enable(i);
  }

  /**
   * Sets the target position of a DOF.
   * 
   * @param pIndex
   *          DOF's index
   * @param pPosition
   *          DOF's new target position
   */
  public default void setTargetPosition(int pIndex, double pPosition)
  {
    getTargetPositionVariable(pIndex).set(pPosition);
  }

  /**
   * Returns the current DOF's target position.
   * 
   * @param pIndex
   *          DOF's index
   * @return current position
   */
  public default double getTargetPosition(int pIndex)
  {
    return getTargetPositionVariable(pIndex).get();
  }

  /**
   * Returns the current DOF's position
   * 
   * @param pDOFIndex
   *          DOF index
   * @return current position
   */
  public default double getCurrentPosition(int pDOFIndex)
  {
    return getCurrentPositionVariable(pDOFIndex).get();
  }

  /**
   * Waits for DOF to be ready (finish last measure).
   * 
   * @param pDOFIndex
   *          DOF's index
   * @param pTimeOut
   *          timeout time
   * @param pTimeUnit
   *          timeout unit
   * @return true if finished before timeout
   */
  public default Boolean waitToBeReady(int pDOFIndex,
                                       long pTimeOut,
                                       TimeUnit pTimeUnit)
  {
    return waitFor(pTimeOut,
                   pTimeUnit,
                   () -> getReadyVariable(pDOFIndex).get());
  }

  /**
   * Waits for all DOFs to be ready (finish all last measure)
   * 
   * @param pTimeOut
   *          timeout time
   * @param pTimeUnit
   *          timeout unit
   * @return true if ready before timeout
   */
  public default Boolean waitToBeReady(long pTimeOut,
                                       TimeUnit pTimeUnit)
  {
    int lNumberOfDOFs = getNumberOfDOFs();

    Callable<Boolean> lCallable = () -> {
      for (int i = 0; i < lNumberOfDOFs; i++)
        if (!getReadyVariable(i).get())
          return false;
      return true;
    };

    return waitFor(pTimeOut, pTimeUnit, lCallable);
  }

  /**
   * Waits for a specific DOF (index) to arrive at the destination within an
   * epsilon distance radius. The DOF is given a certain time to arrive, if it
   * takes longer there is a timeout.
   * 
   * @param pIndex
   *          DOF index
   * @param pEpsilon
   *          epsilon (radius)
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true of no timeout occurred
   */
  public default Boolean waitToArrive(int pIndex,
                                      double pEpsilon,
                                      long pTimeOut,
                                      TimeUnit pTimeUnit)
  {
    return waitFor(pTimeOut, pTimeUnit, () -> {
      double lError = Math.abs(getCurrentPosition(pIndex)
                               - getTargetPosition(pIndex));
      return lError < pEpsilon;
    });
  }

  /**
   * Waits for all DOFs to arrive at the destination within an epsilon distance
   * radius. The DOFs are given a certain time (per DOF) to arrive, if it takes
   * longer there is a timeout.
   * 
   * @param pEpsilon
   *          epsilon (radius)
   * @param pTimeOut
   *          timeout
   * @param pTimeUnit
   *          timeout unit
   * @return true if no timeout occurred
   */
  public default Boolean waitToArrive(double pEpsilon,
                                      long pTimeOut,
                                      TimeUnit pTimeUnit)
  {
    int lNumberOfDOFs = getNumberOfDOFs();

    boolean lTimeOutFlag = true;

    for (int i = 0; i < lNumberOfDOFs; i++)
      lTimeOutFlag &= waitToArrive(pEpsilon, pTimeOut, pTimeUnit);

    return lTimeOutFlag;
  }

  /**
   * Returns min position variable for a given DOF index.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return min position
   */
  Variable<Double> getMinPositionVariable(int pDOFIndex);

  /**
   * Returns max position variable for a given DOF index.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return max position
   */
  Variable<Double> getMaxPositionVariable(int pDOFIndex);

  /**
   * Returns the granularity variable for a given DOF. The granularity is the
   * step size. If there is no step size defined, the value is zero.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return granularity variable
   */
  Variable<Double> getGranularityPositionVariable(int pDOFIndex);

  /**
   * Returns enable variable for a given DOF's index. When enabled, a DOF is
   * allowed to move when the target position is changed.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return enable variable
   */
  Variable<Boolean> getEnableVariable(int pDOFIndex);

  /**
   * Returns the target position variable for a given DOF's index.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return target position variable
   */
  Variable<Double> getTargetPositionVariable(int pDOFIndex);

  /**
   * Returns the current position variable for a given DOF's index.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return current position variable
   */
  Variable<Double> getCurrentPositionVariable(int pDOFIndex);

  /**
   * Returns the ready variable for a given DOF's index. The 'ready' variable
   * informs on whether the stage is ready to accept new commands, either a new
   * target position or a homing command.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return ready variable
   */
  Variable<Boolean> getReadyVariable(int pDOFIndex);

  /**
   * Returns the homing variable for a given DOF's index. When this variable
   * receives an edge from false to true, the homing procedure for this DOF is
   * started. The 'ready' variable must be checked since the stage will not
   * respond to commands unless it is ready.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return homing variable
   */
  Variable<Boolean> getHomingVariable(int pDOFIndex);

  /**
   * Returns the stop variable for a given DOF's index. Stops all measure on
   * the stage's DOF.
   * 
   * @param pDOFIndex
   *          DOF's index
   * @return stop variable
   */
  Variable<Boolean> getStopVariable(int pDOFIndex);

  /**
   * Returns the reset variable for a given DOF's index. Resets the controller
   * into the a state equivalent to the power-on state (as if the DOFs
   * controller had been power cycled.)
   * 
   * @param pIndex
   * @return reset variable
   */
  Variable<Boolean> getResetVariable(int pIndex);

}
