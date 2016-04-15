package rtlib.hardware.signalcond;

import rtlib.core.device.NameableInterface;
import rtlib.core.variable.Variable;

public interface ScalingAmplifierDeviceInterface extends
																								NameableInterface
{

	public void setGain(double pGain);

	public void setOffset(double pOffset);

	public double getGain();

	public double getOffset();

	public Variable<Double> getGainVariable();

	public Variable<Double> getOffsetVariable();

}
