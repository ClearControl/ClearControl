package rtlib.cameras;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.ObjectVariable;

public interface CameraDeviceInterface extends
																			OpenCloseDeviceInterface,
																			StartStopDeviceInterface,
																			StateQueueDeviceInterface
{

	ObjectVariable<Double> getLineReadOutTimeInMicrosecondsVariable();

	ObjectVariable<Double> getExposureInMicrosecondsVariable();

	ObjectVariable<Double> getPixelSizeInNanometersVariable();

	ObjectVariable<Boolean> getIsAcquiringVariable();

	void trigger();

}
