package rtlib.cameras;

import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;

public interface CameraDeviceInterface extends VirtualDeviceInterface
{

	void requestReOpen();

	boolean isReOpenDeviceNeeded();

	void clearReOpen();

	void reopen();

	DoubleVariable getLineReadOutTimeInMicrosecondsVariable();

	DoubleVariable getFrameBytesPerPixelVariable();

	DoubleVariable getFrameWidthVariable();

	DoubleVariable getFrameHeightVariable();

	DoubleVariable getFrameDepthVariable();

	DoubleVariable getExposureInMicrosecondsVariable();

	DoubleVariable getPixelSizeInNanometersVariable();

	BooleanVariable getIsAcquiringVariable();

	void trigger();

}
