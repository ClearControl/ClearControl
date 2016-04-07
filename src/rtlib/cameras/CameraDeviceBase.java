package rtlib.cameras;

import java.util.concurrent.atomic.AtomicBoolean;

import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.ObjectVariable;

public abstract class CameraDeviceBase extends SignalStartableDevice implements
																																		CameraDeviceInterface
{

	protected ObjectVariable<Double> mExposureInMicrosecondsVariable,
			mPixelSizeinNanometersVariable,
			mLineReadOutTimeInMicrosecondsVariable;

	protected ObjectVariable<Long> mStackBytesPerPixelVariable,
			mStackWidthVariable, mStackHeightVariable, mStackDepthVariable;

	private AtomicBoolean mReOpenDeviceNeeded = new AtomicBoolean(false);

	protected ObjectVariable<Boolean> mIsAcquiring;

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

	public ObjectVariable<Long> getStackBytesPerPixelVariable()
	{
		return mStackBytesPerPixelVariable;
	}

	public ObjectVariable<Long> getStackWidthVariable()
	{
		return mStackWidthVariable;
	}

	public ObjectVariable<Long> getStackHeightVariable()
	{
		return mStackHeightVariable;
	}

	public ObjectVariable<Long> getStackDepthVariable()
	{
		return mStackDepthVariable;
	}

	@Override
	public ObjectVariable<Double> getExposureInMicrosecondsVariable()
	{
		return mExposureInMicrosecondsVariable;
	}

	@Override
	public ObjectVariable<Double> getPixelSizeInNanometersVariable()
	{
		return mPixelSizeinNanometersVariable;
	}

	@Override
	public ObjectVariable<Boolean> getIsAcquiringVariable()
	{
		return mIsAcquiring;
	}

	@Override
	public ObjectVariable<Double> getLineReadOutTimeInMicrosecondsVariable()
	{
		return mLineReadOutTimeInMicrosecondsVariable;
	}

}
