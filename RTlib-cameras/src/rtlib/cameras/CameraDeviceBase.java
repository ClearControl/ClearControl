package rtlib.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;

public abstract class CameraDeviceBase extends SignalStartableDevice implements
																																		CameraDeviceInterface
{

	protected DoubleVariable mFrameBytesPerPixelVariable,
			mFrameWidthVariable, mFrameHeightVariable, mFrameDepthVariable,
			mExposureInMicrosecondsVariable,
			mPixelSizeinNanometersVariable,
			mLineReadOutTimeInMicrosecondsVariable;

	private AtomicBoolean mReOpenDeviceNeeded = new AtomicBoolean(false);

	protected BooleanVariable mIsAcquiring;

	public CameraDeviceBase(final String pDeviceName)
	{
		super(pDeviceName);
	}

	public boolean isReOpenDeviceNeeded()
	{
		return mReOpenDeviceNeeded.get();
	}

	public void requestReOpen()
	{
		mReOpenDeviceNeeded.set(true);
	}

	public void clearReOpen()
	{
		mReOpenDeviceNeeded.set(false);
	}

	public abstract void reopen();

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
		return mExposureInMicrosecondsVariable;
	}

	public DoubleVariable getPixelSizeInNanometersVariable()
	{
		return mPixelSizeinNanometersVariable;
	}

	public BooleanVariable getIsAcquiringVariable()
	{
		return mIsAcquiring;
	}


	public DoubleVariable getLineReadOutTimeInMicrosecondsVariable()
	{
		return mLineReadOutTimeInMicrosecondsVariable;
	}

}
