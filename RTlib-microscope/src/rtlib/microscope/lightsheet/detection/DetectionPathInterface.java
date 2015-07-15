package rtlib.microscope.lightsheet.detection;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;

public interface DetectionPathInterface	extends
																				OpenCloseDeviceInterface
{
	public DoubleVariable getDetectionFocusZInMicronsVariable();
}
