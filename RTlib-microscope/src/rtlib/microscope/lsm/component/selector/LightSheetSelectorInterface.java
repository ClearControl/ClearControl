package rtlib.microscope.lsm.component.selector;

import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.math.functions.UnivariateAffineComposableFunction;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;

public interface LightSheetSelectorInterface	extends
										OpenCloseDeviceInterface
{
	public DoubleVariable getSelectorVariable();

	public void reset();
}
