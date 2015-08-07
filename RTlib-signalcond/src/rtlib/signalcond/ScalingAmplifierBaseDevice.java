package rtlib.signalcond;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.doublev.DoubleVariable;

public class ScalingAmplifierBaseDevice extends NamedVirtualDevice	implements
																	ScalingAmplifierDeviceInterface
{

	protected DoubleVariable mGainVariable, mOffsetVariable;

	public ScalingAmplifierBaseDevice(String pDeviceName)
	{
		super(pDeviceName);
	}

	@Override
	public void setGain(double pGain)
	{
		mGainVariable.setValue(pGain);
	}

	@Override
	public void setOffset(double pOffset)
	{
		mOffsetVariable.setValue(pOffset);
	}

	@Override
	public double getGain()
	{
		return mGainVariable.getValue();
	}

	@Override
	public double getOffset()
	{
		return mOffsetVariable.getValue();
	}

	@Override
	public DoubleVariable getGainVariable()
	{
		return mGainVariable;
	}

	@Override
	public DoubleVariable getOffsetVariable()
	{
		return mOffsetVariable;
	}

}
