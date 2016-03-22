package rtlib.cameras;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.StartStopDeviceInterface;
import rtlib.core.device.queue.StateQueueDeviceInterface;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;

public interface CameraDeviceInterface	extends
										OpenCloseDeviceInterface,
										StartStopDeviceInterface,
										StateQueueDeviceInterface
{

	DoubleVariable getLineReadOutTimeInMicrosecondsVariable();

	DoubleVariable getExposureInMicrosecondsVariable();

	DoubleVariable getPixelSizeInNanometersVariable();

	BooleanVariable getIsAcquiringVariable();

	void trigger();

}
