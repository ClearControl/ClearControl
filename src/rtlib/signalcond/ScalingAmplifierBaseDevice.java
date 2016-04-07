package rtlib.signalcond;

import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.objectv.ObjectVariable;

public class ScalingAmplifierBaseDevice extends NamedVirtualDevice	implements
																	ScalingAmplifierDeviceInterface
{

	protected  ObjectVariable<Double> mGainVariable, mOffsetVariable;

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
	public  ObjectVariable<Double> getGainVariable()
	{
		return mGainVariable;
	}

	@Override
	public  ObjectVariable<Double> getOffsetVariable()
	{
		return mOffsetVariable;
	}

}
