package rtlib.microscope.lightsheet.detection;

import org.apache.commons.math3.analysis.UnivariateFunction;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;

public interface DetectionArmInterface	extends
																				OpenCloseDeviceInterface
{
	public DoubleVariable getDetectionFocusZInMicronsVariable();

	public ObjectVariable<UnivariateFunction> getDetectionFocusZFunction();
}
