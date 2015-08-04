package rtlib.microscope.lightsheet.detection;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;

public interface DetectionArmInterface	extends
																				OpenCloseDeviceInterface
{
	public DoubleVariable getDetectionFocusZInMicronsVariable();

	@SuppressWarnings("rawtypes")
	public ObjectVariable<UnivariateAffineComposableFunction> getDetectionFocusZFunction();
}
