package rtlib.signalcond;

import rtlib.core.device.NameableInterface;
import rtlib.core.variable.types.doublev.DoubleVariable;

public interface ScalingAmplifierDeviceInterface extends
																								NameableInterface
{

	public void setGain(double pGain);

	public void setOffset(double pOffset);

	public double getGain();

	public double getOffset();

	public DoubleVariable getGainVariable();

	public DoubleVariable getOffsetVariable();

}
