package rtlib.hardware.signalcond;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.Variable;

public class ScalingAmplifierBaseDevice extends NamedVirtualDevice implements
																																	ScalingAmplifierDeviceInterface
{

	protected Variable<Double> mGainVariable, mOffsetVariable;

	public ScalingAmplifierBaseDevice(String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public void setGain(double pGain)
	{
		mGainVariable.set(pGain);
	}

	@Override
	public void setOffset(double pOffset)
	{
		mOffsetVariable.set(pOffset);
	}

	@Override
	public double getGain()
	{
		return mGainVariable.get();
	}

	@Override
	public double getOffset()
	{
		return mOffsetVariable.get();
	}

	@Override
	public Variable<Double> getGainVariable()
	{
		return mGainVariable;
	}

	@Override
	public Variable<Double> getOffsetVariable()
	{
		return mOffsetVariable;
	}

}
