package clearcontrol.microscope.lightsheet.interactive;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.log.LoggingInterface;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.change.ChangeListener;
import clearcontrol.device.task.LoopTaskDevice;
import clearcontrol.hardware.cameras.StackCameraDeviceInterface;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.LightSheetMicroscopeInterface;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.state.AcquisitionStateInterface;
import clearcontrol.microscope.state.AcquisitionStateManager;

public class InteractiveAcquisition extends LoopTaskDevice implements
																													LoggingInterface
{

	private static final int cRecyclerMinimumNumberOfAvailableStacks = 60;
	private static final int cRecyclerMaximumNumberOfAvailableStacks = 60;
	private static final int cRecyclerMaximumNumberOfLiveStacks = 60;

	private final LightSheetMicroscopeInterface mLightSheetMicroscope;
	private final AcquisitionStateManager mAcquisitionStateManager;

	private volatile InteractiveAcquisitionModes mCurrentAcquisitionMode = InteractiveAcquisitionModes.None;

	private final BoundedVariable<Double> mExposureVariableInSeconds;
	private final Variable<Boolean> mTriggerOnChangeVariable,
			mUseCurrentAcquisitionStateVariable;
	private final BoundedVariable<Number> m2DAcquisitionZVariable;
	private final Variable<Boolean>[] mActiveCameraVariableArray;
	private final Variable<Long> mAcquisitionCounterVariable;
	
	private volatile boolean mUpdate = true;

	private ChangeListener<VirtualDevice> mChangeListener;


	@SuppressWarnings("unchecked")
	public InteractiveAcquisition(String pDeviceName,
																LightSheetMicroscope pLightSheetMicroscope,
																AcquisitionStateManager pAcquisitionStateManager)
	{
		super(pDeviceName, 1, TimeUnit.SECONDS);
		mLightSheetMicroscope = pLightSheetMicroscope;
		mAcquisitionStateManager = pAcquisitionStateManager;

		@SuppressWarnings("rawtypes")
		VariableSetListener lListener = (o, n) -> {
			if (!o.equals(n))
				mUpdate = true;
		};

		mExposureVariableInSeconds = new BoundedVariable<Double>(	pDeviceName + "Exposure",
																															0.0,
																															0.0,
																															Double.POSITIVE_INFINITY,
																															0.0);

		mTriggerOnChangeVariable = new Variable<Boolean>(	pDeviceName + "TriggerOnChange",
																											false);

		mUseCurrentAcquisitionStateVariable = new Variable<Boolean>(pDeviceName + "UseCurrentAcquisitionState",
																																false);

		Variable<Number> lMinVariable = mLightSheetMicroscope.getDevice(DetectionArmInterface.class,
																																		0)
																													.getZVariable()
																													.getMinVariable();
		Variable<Number> lMaxVariable = mLightSheetMicroscope.getDevice(DetectionArmInterface.class,
																																		0)
																													.getZVariable()
																													.getMaxVariable();

		m2DAcquisitionZVariable = new BoundedVariable<Number>("2DAcquisitionZ",
																													0,
																													lMinVariable.get(),
																													lMaxVariable.get());
		
		mAcquisitionCounterVariable = new Variable<Long>("AcquisitionCounter",0L);

		lMinVariable.sendUpdatesTo(m2DAcquisitionZVariable.getMinVariable());
		lMaxVariable.sendUpdatesTo(m2DAcquisitionZVariable.getMaxVariable());

		mExposureVariableInSeconds.addSetListener(lListener);
		mTriggerOnChangeVariable.addSetListener(lListener);
		getLoopPeriodVariable().addSetListener(lListener);
		m2DAcquisitionZVariable.addSetListener(lListener);

		getLoopPeriodVariable().set(1.0);
		getExposureVariable().set(0.010);

		int lNumberOfCameras = getNumberOfCameras();
		mActiveCameraVariableArray = new Variable[lNumberOfCameras];
		for (int c = 0; c < lNumberOfCameras; c++)
		{
			mActiveCameraVariableArray[c] = new Variable<Boolean>("ActiveCamera" + c,
																														true);

			mActiveCameraVariableArray[c].addSetListener(lListener);
		}

		mChangeListener = (o) -> {
			info("Received request to update queue.");
			mUpdate = true;
		};
	}

	@Override
	public boolean open()
	{
		getLightSheetMicroscope().addChangeListener(mChangeListener);
		return super.open();
	}

	@Override
	public boolean close()
	{
		getLightSheetMicroscope().removeChangeListener(mChangeListener);
		return super.close();
	}

	@Override
	public boolean loop()
	{
		try
		{
			if (mUpdate || getLightSheetMicroscope().getQueueLength() == 0)
			{

				double lCurrentZ = get2DAcquisitionZVariable().get()
																											.doubleValue();

				if (mCurrentAcquisitionMode == InteractiveAcquisitionModes.Acquisition2D)
				{
					info("Building 2D Acquisition queue");
					if (getUseCurrentAcquisitionStateVariable().get())
					{
						info("Building 2D Acquisition queue using the current acquisition state");
						@SuppressWarnings("unchecked")
						InterpolatedAcquisitionState lCurrentState = (InterpolatedAcquisitionState) mAcquisitionStateManager.getCurrentState();

						lCurrentState.applyAcquisitionStateAtZ(	mLightSheetMicroscope,
																										lCurrentZ);
					}
					else
					{
						info("Building 2D Acquisition queue using current devices state");
						getLightSheetMicroscope().useRecycler("2DInteractive",
																									cRecyclerMinimumNumberOfAvailableStacks,
																									cRecyclerMaximumNumberOfAvailableStacks,
																									cRecyclerMaximumNumberOfLiveStacks);

						getLightSheetMicroscope().clearQueue();

						for (int c = 0; c < getNumberOfCameras(); c++)
						{
							getLightSheetMicroscope().setC(	c,
																							mActiveCameraVariableArray[c].get());

							getLightSheetMicroscope().setDZ(c, lCurrentZ);
						}
						getLightSheetMicroscope().setExposure((long) (mExposureVariableInSeconds.get() * 1000000L),
																									TimeUnit.MICROSECONDS);

						for (int l = 0; l < getNumberOfLightsSheets(); l++)
							if (getLightSheetMicroscope().getI(l))
							{
								info("ACTIVATING LIGHTSHEET " + l);
								getLightSheetMicroscope().getDevice(LightSheet.class,
																										l).update();

								getLightSheetMicroscope().setIZ(l, lCurrentZ);

								break;
							}

						getLightSheetMicroscope().addCurrentStateToQueue();

						getLightSheetMicroscope().finalizeQueue();
					}
				}
				else if (mCurrentAcquisitionMode == InteractiveAcquisitionModes.Acquisition3D)
				{
					info("Building Acquisition3D queue");
					getLightSheetMicroscope().useRecycler("3DInteractive",
																								cRecyclerMinimumNumberOfAvailableStacks,
																								cRecyclerMaximumNumberOfAvailableStacks,
																								cRecyclerMaximumNumberOfLiveStacks);

					@SuppressWarnings("unchecked")
					AcquisitionStateInterface<LightSheetMicroscopeInterface> lCurrentState = (AcquisitionStateInterface<LightSheetMicroscopeInterface>) mAcquisitionStateManager.getCurrentState();

					lCurrentState.applyAcquisitionState(mLightSheetMicroscope);
				}

			}

			if (getLightSheetMicroscope().getQueueLength() == 0)
			{
				// this leads to a call to stop() which stops the loop
				warning("Queue empty stopping interactive acquisition loop");
				return false;
			}

			if (mCurrentAcquisitionMode != InteractiveAcquisitionModes.None)
			{
				if (getTriggerOnChangeVariable().get() && !mUpdate)
					return true;

				// play queue
				//info("Playing LightSheetMicroscope Queue...");
				boolean lSuccess = getLightSheetMicroscope().playQueueAndWaitForStacks(	100,
																																								TimeUnit.SECONDS);
				
				if(lSuccess)
					mAcquisitionCounterVariable.set(mAcquisitionCounterVariable.get()+1);

				//info("... done waiting!");
			}

			if (mUpdate)
				mUpdate = false;
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		//info("end of loop");

		return true;
	}

	public void start2DAcquisition()
	{
		info("Starting 2D Acquisition...");
		mCurrentAcquisitionMode = InteractiveAcquisitionModes.Acquisition2D;
		mAcquisitionCounterVariable.set(0L);
		startTask();
	}

	public void start3DAcquisition()
	{
		info("Starting 3D Acquisition...");
		mCurrentAcquisitionMode = InteractiveAcquisitionModes.Acquisition3D;
		mAcquisitionCounterVariable.set(0L);
		startTask();
	}

	public void stopAcquisition()
	{
		info("Stopping Acquisition...");
		mCurrentAcquisitionMode = InteractiveAcquisitionModes.None;
		stopTask();
	}

	public BoundedVariable<Double> getExposureVariable()
	{
		return mExposureVariableInSeconds;
	}

	public Variable<Boolean> getTriggerOnChangeVariable()
	{
		return mTriggerOnChangeVariable;
	}

	public Variable<Boolean> getUseCurrentAcquisitionStateVariable()
	{
		return mUseCurrentAcquisitionStateVariable;
	}

	public LightSheetMicroscopeInterface getLightSheetMicroscope()
	{
		return mLightSheetMicroscope;
	}

	public int getNumberOfCameras()
	{
		int lNumberOfCameras = mLightSheetMicroscope.getNumberOfDevices(StackCameraDeviceInterface.class);
		return lNumberOfCameras;
	}

	private int getNumberOfLightsSheets()
	{
		int lNumberOfLightsSheets = mLightSheetMicroscope.getNumberOfDevices(LightSheet.class);
		return lNumberOfLightsSheets;
	}

	public Variable<Boolean> getActiveCameraVariable(int pCameraIndex)
	{
		return mActiveCameraVariableArray[pCameraIndex];
	}

	public BoundedVariable<Number> get2DAcquisitionZVariable()
	{
		return m2DAcquisitionZVariable;
	}

	public Variable<Long> getAcquisitionCounterVariable()
	{
		return mAcquisitionCounterVariable;
		
	}

}
