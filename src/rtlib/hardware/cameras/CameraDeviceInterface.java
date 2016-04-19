package rtlib.hardware.cameras;

import rtlib.core.variable.Variable;
import rtlib.device.openclose.OpenCloseDeviceInterface;
import rtlib.device.queue.StateQueueDeviceInterface;
import rtlib.device.startstop.StartStopDeviceInterface;

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
