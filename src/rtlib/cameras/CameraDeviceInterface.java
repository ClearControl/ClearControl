package rtlib.cameras;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.Variable;

public interface CameraDeviceInterface extends
																			OpenCloseDeviceInterface,
																			StartStopDeviceInterface,
																			StateQueueDeviceInterface
{

	Variable<Double> getLineReadOutTimeInMicrosecondsVariable();

	Variable<Double> getExposureInMicrosecondsVariable();

	Variable<Double> getPixelSizeInNanometersVariable();

	Variable<Boolean> getIsAcquiringVariable();

	void trigger();

}
