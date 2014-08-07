package rtlib.cameras;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;

public abstract class CameraDevice extends SignalStartableDevice implements
																																VirtualDeviceInterface
{

	protected DoubleVariable mFrameBytesPerPixelVariable,
			mFrameWidthVariable, mFrameHeightVariable, mFrameDepthVariable,
			mExposureInMicroseconds, mPixelSizeinNanometers;

	protected BooleanVariable mIsAcquiring;

	public CameraDevice(final String pDeviceName)
	{
		super(pDeviceName);
	}

	public DoubleVariable getFrameBytesPerPixelVariable()
	{
		return mFrameBytesPerPixelVariable;
	}

	public DoubleVariable getFrameWidthVariable()
	{
		return mFrameWidthVariable;
	}

	public DoubleVariable getFrameHeightVariable()
	{
		return mFrameHeightVariable;
	}

	public DoubleVariable getFrameDepthVariable()
	{
		return mFrameDepthVariable;
	}

	public DoubleVariable getExposureInMicrosecondsVariable()
	{
		return mExposureInMicroseconds;
	}

	public DoubleVariable getPixelSizeInNanometersVariable()
	{
		return mPixelSizeinNanometers;
	}


	public BooleanVariable getIsAcquiringVariable()
	{
		return mIsAcquiring;
	}

}
