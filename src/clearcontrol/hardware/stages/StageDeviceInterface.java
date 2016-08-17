package clearcontrol.hardware.stages;

import java.util.concurrent.TimeUnit;

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
																			OpenCloseDeviceInterface
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
	void reset(int pDOFIndex);

	/**
	 * Homes a given DOF.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 */
	void home(int pDOFIndex);

	/**
	 * Enables a given DOF.
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 */
	void enable(int pDOFIndex);

	/**
	 * Sets the target position of a DOF.
	 * 
	 * @param pIndex
	 *          DOF's index
	 * @param pPosition
	 *          DOF's new target position
	 */
	void setTargetPosition(int pIndex, double pPosition);

	/**
	 * Returns the current DOF's target position.
	 * 
	 * @param pIndex
	 *          DOF's index
	 * @return current position
	 */
	double getTargetPosition(int pIndex);

	/**
	 * Returns the current DOF's position
	 * 
	 * @param pDOFIndex
	 *          DOF index
	 * @return current position
	 */
	double getCurrentPosition(int pDOFIndex);

	/**
	 * Waits for DOF to be ready (finish last movement).
	 * 
	 * @param pDOFIndex
	 *          DOF's index
	 * @param pTimeOut
	 *          timeout time
	 * @param pTimeUnit
	 *          timeout unit
	 * @return true if fiished before timeout
	 */
	Boolean waitToBeReady(int pDOFIndex,
												long pTimeOut,
												TimeUnit pTimeUnit);

	/**
	 * Waits for all DOFs to be ready (finish all last movement)
	 * 
	 * @param pTimeOut
	 *          timeout time
	 * @param pSeconds
	 *          timeout unit
	 * @return
	 */
	Boolean waitToBeReady(long pTimeOut, TimeUnit pSeconds);

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
	 * @param pDOFIndex DOF's index
	 * @return max position
	 */
	Variable<Double> getMaxPositionVariable(int pDOFIndex);

	/**
	 * Returns enable variable for a given DOF's index.
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Boolean> getEnableVariable(int pDOFIndex);

	/**
	 * Returns the target position variable for a given DOF's index.
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Double> getTargetPositionVariable(int pDOFIndex);

	/**
	 * Returns the current position variable for a given DOF's index.
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Double> getCurrentPositionVariable(int pDOFIndex);

	/**
	 * Returns the ready variable for a given DOF's index.
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Boolean> getReadyVariable(int pDOFIndex);

	/**
	 * Returns the homing variable for a given DOF's index.
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Boolean> getHomingVariable(int pDOFIndex);

	/**
	 * Returns the stop variable for a given DOF's index.
	 * @param pDOFIndex DOF's index
	 * @return
	 */
	Variable<Boolean> getStopVariable(int pDOFIndex);

}
