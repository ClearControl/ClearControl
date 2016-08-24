package clearcontrol.hardware.cameras.devices.andorzyla.sim;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import clearcontrol.core.log.Loggable;
import clearcontrol.core.variable.Variable;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;
import clearcontrol.stack.sourcesink.StackSourceInterface;

/**
 * @author myersadmin
 *
 */
public class AndorStackCameraDeviceSimulator	extends
																							StackCameraDeviceBase
																							implements
																							Loggable,
																							AsynchronousSchedulerServiceAccess
{
	private StackSourceInterface mStackSource;
	private Variable<Boolean> mTriggerVariable;
	
	
	/**
	 * Constructor to create a simulated AndorStackCameraDevice
	 * @param pCameraDeviceName - device name
	 * @param pStackSource - source of the artificial stack
	 * @param pTriggerVariable - variable to mimic a software trigger
	 */
	
	public AndorStackCameraDeviceSimulator(String pCameraDeviceName, StackSourceInterface pStackSource, Variable<Boolean> pTriggerVariable)
	{
		super(pCameraDeviceName);
		mStackSource = pStackSource;
		mTriggerVariable = pTriggerVariable;
		
		mChannelVariable = new Variable<Integer>("Channel", 0); // what is this?
	}
}
