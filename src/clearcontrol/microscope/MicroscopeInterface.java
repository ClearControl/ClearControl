package clearcontrol.microscope;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.queue.StateQueueDeviceInterface;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

public interface MicroscopeInterface extends
																		NameableInterface,
																		StateQueueDeviceInterface
{

	/**
	 * Returns the microscopes name.
	 * 
	 * @return microscope's name.
	 */
	public String getName();

	/**
	 * Returns the device list object from which all devices can be queried.
	 * 
	 * @return device list object
	 */
	MicroscopeDeviceLists getDeviceLists();

	/**
	 * Sets the recycler that should be used by the stack camera device of given
	 * id.
	 * 
	 * @param pStackCameraDeviceIndex
	 */
	void setRecycler(	int pStackCameraDeviceIndex,
										RecyclerInterface<StackInterface, StackRequest> pRecycler);

	/**
	 * Sets the recycler that should be used by _all_ stack camera devices.
	 * 
	 * @param pRecycler
	 */
	void setRecycler(RecyclerInterface<StackInterface, StackRequest> pRecycler);

	/**
	 * Returns the recycler currently b the stack camera device of given id.
	 * 
	 * @param pStackCameraDeviceIndex
	 *          stack camera index id.
	 * @return recycler.
	 */
	RecyclerInterface<StackInterface, StackRequest> getRecycler(int pStackCameraDeviceIndex);

	/**
	 * Uses a recycler with given parameters. This recycler will be used for all
	 * subsequent plays. if teh recycler does not exist yet, it is created.
	 * 
	 * @param pName
	 *          recycler name
	 * @param pMaximumNumberOfAvailableStacks
	 *          maximum number of available stacks
	 * @param pMaximumNumberOfLiveStacks
	 *          maximum number of live stacks
	 */
	void useRecycler(	String pName,
										int pMinimumNumberOfAvailableStacks,
										int pMaximumNumberOfAvailableStacks,
										int pMaximumNumberOfLiveStacks);

	/**
	 * Clears a given recycler.
	 * 
	 * @param pName
	 */
	void clearRecycler(String pName);

	/**
	 * Clears all recyclers.
	 */
	void clearAllRecyclers();

	/**
	 * Plays queue for all devices, and waits for playback to finish.
	 * 
	 * @param pTimeOut
	 *          timeout
	 * @param pTimeUnit
	 *          time unit for timeout
	 * @return true if successful
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	Boolean playQueueAndWait(long pTimeOut, TimeUnit pTimeUnit)	throws InterruptedException,
																															ExecutionException,
																															TimeoutException;

	/**
	 * Plays queue for all devices, waits for playback to finish as well as waits
	 * for stacks to be delivered.
	 * 
	 * @param pTimeOut
	 * @param pTimeUnit
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	Boolean playQueueAndWaitForStacks(long pTimeOut, TimeUnit pTimeUnit) throws InterruptedException,
																																			ExecutionException,
																																			TimeoutException;

	/**
	 * Returns the average timestamp for all stacks acquired during for last
	 * played queue.
	 * 
	 * @return timestamp in nanoseconds
	 */
	long lastAcquiredStacksTimeStampInNS();

	Variable<StackInterface> getStackVariable(int pIndex);


}
