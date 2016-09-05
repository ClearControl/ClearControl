package clearcontrol.hardware.stages;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.timing.WaitingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;

/**
 * StageDeviceInterface is the interface for all motorized stages. It offers a
 * standard interface to access different degree of freedoms (DOFs) and set
 * their target position, request the current position, homing, resetting and
 * more.
 * 
 * @author royer
 */
public interface StageDeviceInterface	extends
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
	public default void reset(int pIndex)
	{
		getResetVariable(pIndex).setEdge(false, true);
	}

	/**
	 * Homes a given DOF.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 */
	public default void home(int pIndex)
	{
		getHomingVariable(pIndex).setEdge(false, true);
	}

	/**
	 * Enables a given DOF.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 */
	public default void enable(int pIndex)
	{
		getEnableVariable(pIndex).setEdge(false, true);
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
	public default double getCurrentPosition(int pIndex)
	{
		return getCurrentPositionVariable(pIndex).get();
	}

	/**
	 * Waits for DOF to be ready (finish last movement).
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @param pTimeOut
	 *          timeout time
	 * @param pTimeUnit
	 *          timeout unit
	 * @return true if finished before timeout
	 */
	public default Boolean waitToBeReady(	int pIndex,
																				long pTimeOut,
																				TimeUnit pTimeUnit)
	{
		return waitFor(	pTimeOut,
										pTimeUnit,
										() -> getReadyVariable(pIndex).get());
	}

	/**
	 * Waits for all DOFs to be ready (finish all last movement)
	 * 
	 * @param pTimeOut
	 *          timeout time
	 * @param pSeconds
	 *          timeout unit
	 * @return
	 */
	public default Boolean waitToBeReady(	long pTimeOut,
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
		return waitFor(	pTimeOut,
										pTimeUnit,
										() -> {
											double lError = Math.abs(getCurrentPosition(pIndex) - getTargetPosition(pIndex));
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
	 * Returns the Granularity Variable for a given DOF.
	 * 
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Double> getGranularityPositionVariable(int pDOFIndex);

	/**
	 * Returns enable variable for a given DOF's index.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @return
	 */
	Variable<Boolean> getEnableVariable(int pDOFIndex);

	/**
	 * Returns the target position variable for a given DOF's index.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @return
	 */
	Variable<Double> getTargetPositionVariable(int pDOFIndex);

	/**
	 * Returns the current position variable for a given DOF's index.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @return
	 */
	Variable<Double> getCurrentPositionVariable(int pDOFIndex);

	/**
	 * Returns the ready variable for a given DOF's index.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @return
	 */
	Variable<Boolean> getReadyVariable(int pDOFIndex);

	/**
	 * Returns the homing variable for a given DOF's index.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @return
	 */
	Variable<Boolean> getHomingVariable(int pDOFIndex);

	/**
	 * Returns the stop variable for a given DOF's index.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @return
	 */
	Variable<Boolean> getStopVariable(int pDOFIndex);

	/**
	 * Returns the reset variable for a given DOF's index.
	 * 
	 * @param pIndex
	 * @return
	 */
	Variable<Boolean> getResetVariable(int pIndex);



}
