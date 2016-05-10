package clearcontrol.microscope.lightsheet.acquisition.interactive;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.change.ChangeListener;
import clearcontrol.device.signal.SignalStartableLoopTaskDevice;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;

public class InteractiveAcquisition	extends
																		SignalStartableLoopTaskDevice
{

	private LightSheetMicroscope mLightSheetMicroscope;

	private volatile InteractiveAcquisitionModes mRequestedAcquisitionMode = InteractiveAcquisitionModes.None;
	private volatile InteractiveAcquisitionModes mCurrentAcquisitionMode = InteractiveAcquisitionModes.None;

	private final BoundedVariable<Double> mExposureVariableInSeconds;
	private final Variable<Boolean> mTriggerOnChangeVariable;

	private volatile boolean mUpdate = true;

	private ChangeListener mChangeListener;

	public InteractiveAcquisition(String pDeviceName,
																LightSheetMicroscope pLightSheetMicroscope)
	{
		super(pDeviceName, false);
		mLightSheetMicroscope = pLightSheetMicroscope;

		mExposureVariableInSeconds = new BoundedVariable<Double>(	pDeviceName + "Exposure",
																															0.0,
																															0.0,
																															Double.POSITIVE_INFINITY,
																															0.0);

		mTriggerOnChangeVariable = new Variable<Boolean>(	pDeviceName + "TriggerOnChange",
																											false);

		getLoopPeriodVariable().set(1.0);
		getExposureVariable().set(0.010);

		mChangeListener = (o) -> {
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Received request to update queue!!");
			mUpdate = true;
		};
	}

	@Override
	public boolean open()
	{
		mLightSheetMicroscope.addChangeListener(mChangeListener);
		return super.open();
	}

	@Override
	public boolean close()
	{
		mLightSheetMicroscope.removeChangeListener(mChangeListener);
		return super.close();
	}

	@Override
	protected boolean loop()
	{

		try
		{
			try
			{

				if (mRequestedAcquisitionMode != mCurrentAcquisitionMode)
				{
					mCurrentAcquisitionMode = mRequestedAcquisitionMode;
				}

				if (mUpdate)
				{

					if (mCurrentAcquisitionMode == InteractiveAcquisitionModes.Acquisition2D)
					{
						mLightSheetMicroscope.useRecycler("2DIntercative",
																							60,
																							60,
																							60);

						mLightSheetMicroscope.clearQueue();
						mLightSheetMicroscope.setC(true);
						mLightSheetMicroscope.setExposure((long) (mExposureVariableInSeconds.get() * 1000000L),
																							TimeUnit.MICROSECONDS);
						mLightSheetMicroscope.addCurrentStateToQueue();
						mLightSheetMicroscope.finalizeQueue();

					}

					mUpdate = false;
				}

				if (mCurrentAcquisitionMode != InteractiveAcquisitionModes.None)
				{
					// play queue
					System.out.println("Playing Queue...");
					mLightSheetMicroscope.playQueueAndWaitForStacks(1,
																													TimeUnit.SECONDS);
				}
			}
			catch (InterruptedException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
		catch (TimeoutException e)
		{
			e.printStackTrace();
		}

		return true;
	}

	public void start2DAcquisition()
	{
		System.out.println("Starting 2D Acquisition...");
		mRequestedAcquisitionMode = InteractiveAcquisitionModes.Acquisition2D;
		start();
	}

	public void start3DAcquisition()
	{
		System.out.println("Starting 3D Acquisition...");
		mRequestedAcquisitionMode = InteractiveAcquisitionModes.Acquisition2D;
		start();
	}

	public void stopAcquisition()
	{
		System.out.println("Stopping Acquisition...");
		mRequestedAcquisitionMode = InteractiveAcquisitionModes.None;
		stop();
	}

	public BoundedVariable<Double> getExposureVariable()
	{
		return mExposureVariableInSeconds;
	}

	public Variable<Boolean> getTriggerOnChangeVariable()
	{
		return mTriggerOnChangeVariable;
	}

}
