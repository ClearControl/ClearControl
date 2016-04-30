package clearcontrol.hardware.cameras;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.queue.StateQueueDeviceInterface;
import clearcontrol.device.startstop.StartStopDeviceInterface;

public interface CameraDeviceInterface extends
																			OpenCloseDeviceInterface,
																			StartStopDeviceInterface,
																			StateQueueDeviceInterface
{

	Variable<Double> getLineReadOutTimeInMicrosecondsVariable();

	Variable<Double> getExposureInMicrosecondsVariable();

	Variable<Double> getPixelSizeInNanometersVariable();

	Variable<Boolean> getIsAcquiringVariable();
	
	Variable<Integer> getChannelVariable();

	void trigger();

}
