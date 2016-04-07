package rtlib.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;

public abstract class CameraDeviceBase extends SignalStartableDevice implements
																	CameraDeviceInterface
{

	protected DoubleVariable mStackBytesPerPixelVariable,
			mStackWidthVariable, mStackHeightVariable,
			mStackDepthVariable, mExposureInMicrosecondsVariable,
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

	public DoubleVariable getStackBytesPerPixelVariable()
	{
		return mStackBytesPerPixelVariable;
	}

	public DoubleVariable getStackWidthVariable()
	{
		return mStackWidthVariable;
	}

	public DoubleVariable getStackHeightVariable()
	{
		return mStackHeightVariable;
	}

	public DoubleVariable getStackDepthVariable()
	{
		return mStackDepthVariable;
	}

	@Override
	public DoubleVariable getExposureInMicrosecondsVariable()
	{
		return mExposureInMicrosecondsVariable;
	}

	@Override
	public DoubleVariable getPixelSizeInNanometersVariable()
	{
		return mPixelSizeinNanometersVariable;
	}

	@Override
	public BooleanVariable getIsAcquiringVariable()
	{
		return mIsAcquiring;
	}

	@Override
	public DoubleVariable getLineReadOutTimeInMicrosecondsVariable()
	{
		return mLineReadOutTimeInMicrosecondsVariable;
	}

}
