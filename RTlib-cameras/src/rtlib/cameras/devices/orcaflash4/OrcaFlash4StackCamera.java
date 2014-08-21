package rtlib.cameras.devices.orcaflash4;

import rtlib.cameras.StackCameraDeviceBase;
import rtlib.cameras.devices.orcaflash4.utils.DcamJToVideoFrameConverter;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.units.Magnitudes;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import dcamj.DcamAcquisition;
import dcamj.DcamAcquisition.TriggerType;
import dcamj.DcamAcquisitionListener;
import dcamj.DcamFrame;
import dcamj.DcamProperties;

public class OrcaFlash4StackCamera extends
																	StackCameraDeviceBase<Short, Short>	implements
																																	VirtualDeviceInterface
{
	public static final int cStackProcessorQueueSize = 100;
	public static final int cDcamJNumberOfBuffers = 1024;

	private final int mCameraDeviceIndex;

	private final DcamAcquisition mDcamAcquisition;

	private final ObjectVariable<DcamFrame> mFrameReference = new ObjectVariable<DcamFrame>("DCamJVideoFrame");

	private final DcamJToVideoFrameConverter mDcamJToStackConverterAndProcessing;

	private Object mLock = new Object();

	public static final OrcaFlash4StackCamera buildWithExternalTriggering(final int pCameraDeviceIndex)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.ExternalFastEdge);
	}

	public static final OrcaFlash4StackCamera buildWithInternalTriggering(final int pCameraDeviceIndex)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.Internal);
	}

	public static final OrcaFlash4StackCamera buildWithSoftwareTriggering(final int pCameraDeviceIndex)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.Software);
	}

	private OrcaFlash4StackCamera(final int pCameraDeviceIndex,
																final TriggerType pTriggerType)
	{
		super("OrcaFlash4Camera");

		mCameraDeviceIndex = pCameraDeviceIndex;
		mDcamAcquisition = new DcamAcquisition(mCameraDeviceIndex);
		mDcamAcquisition.setTriggerType(pTriggerType);

		mDcamAcquisition.addListener(new DcamAcquisitionListener()
		{

			@Override
			public void frameArrived(	final DcamAcquisition pDcamAquisition,
																final long pAbsoluteFrameIndex,
																final long pArrivalTimeStamp,
																final long pFrameIndexInBufferList,
																final DcamFrame pDcamFrame)
			{
				final long lDepth = pDcamFrame.getDepth();
				/*System.out.println("frameArrived: hashcode=" + pDcamFrame.hashCode()
														+ " index="
														+ pDcamFrame.getIndex()
														+ " pFrameIndexInBufferList="
														+ pFrameIndexInBufferList);/**/
				mFrameReference.setReference(pDcamFrame);
			}

		});

		// TODO
		mFrameBytesPerPixelVariable = new DoubleVariable(	"BytesPerPixel",
																											mDcamAcquisition.getFrameBytesPerPixel());

		mFrameWidthVariable = new DoubleVariable("FrameWidth", 2048)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				synchronized (mLock)
				{
					requestReOpen();
					final double lRoundto4 = DcamProperties.roundto4((int) pNewValue);
					if (lRoundto4 != pNewValue)
					{
						this.setValue(lRoundto4);
					}
					return super.setEventHook(pOldValue, lRoundto4);
				}
			}

		};

		mFrameHeightVariable = new DoubleVariable("FrameHeight", 2048)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				synchronized (mLock)
				{
					requestReOpen();
					final double lRoundto4 = DcamProperties.roundto4((int) pNewValue);
					if (lRoundto4 != pNewValue)
					{
						this.setValue(lRoundto4);
					}
					return super.setEventHook(pOldValue, lRoundto4);
				}
			}
		};

		mFrameDepthVariable = new DoubleVariable("FrameDepth", 64)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mPixelSizeinNanometersVariable = new DoubleVariable("PixelSizeInNanometers",
																								160);

		mExposureInMicrosecondsVariable = new DoubleVariable(	"ExposureInMicroseconds",
																									5000)
		{
			@Override
			public double setEventHook(	final double pOldExposureInMicroseconds,
																	final double pExposureInMicroseconds)
			{
				synchronized (mLock)
				{
					final double lEffectiveExposureInSeconds = mDcamAcquisition.setExposureInSeconds(Magnitudes.micro2unit(pExposureInMicroseconds));
					final double lEffectiveExposureInMicroSeconds = Magnitudes.unit2micro(lEffectiveExposureInSeconds);
					return super.setEventHook(pOldExposureInMicroseconds,
																		lEffectiveExposureInMicroSeconds);
				}
			}

			@Override
			public double getValue()
			{
				return Magnitudes.unit2micro(mDcamAcquisition.getExposureInSeconds());
			}
		};

		mIsAcquiring = new BooleanVariable("IsAcquiring", false)
		{

			@Override
			public double getEventHook(double pCurrentValue)
			{
				return BooleanVariable.boolean2double(mDcamAcquisition.isAcquiring());
			}

		};

		mStackModeVariable = new BooleanVariable("StackMode", false);
		mSingleShotModeVariable = new BooleanVariable("SingleShotMode",
																									false);

		mDcamJToStackConverterAndProcessing = new DcamJToVideoFrameConverter(mFrameReference,
																																											cStackProcessorQueueSize);

		mFrameDepthVariable.sendUpdatesTo(mDcamJToStackConverterAndProcessing.getStackDepthVariable());

		mStackReference = mDcamJToStackConverterAndProcessing.getStackReferenceVariable();

	}

	protected ObjectVariable<DcamFrame> getInternalFrameReferenceVariable()
	{
		return mFrameReference;
	}

	public DoubleVariable getNumberOfPhasesVariable()
	{
		return mDcamJToStackConverterAndProcessing.getNumberOfPhasesPerPlaneVariable();
	}

	@Override
	public boolean open()
	{
		synchronized (mLock)
		{
			try
			{
				final boolean lOpenResult = mDcamAcquisition.open();
				mDcamAcquisition.setDefectCorrection(false);
				mDcamJToStackConverterAndProcessing.open();
				return lOpenResult;
			}
			catch (final Throwable e)
			{
				System.err.println("Could not open DCAM!");
				e.printStackTrace();
				return false;
			}
		}
	}

	public final void ensureEnough2DFramesAreAvailable(final int pNumberOf2DFramesNeeded)
	{
		synchronized (mLock)
		{
			DcamFrame.preallocateFrames(pNumberOf2DFramesNeeded,
																	(long) getFrameBytesPerPixelVariable().getValue(),
																	(long) getFrameWidthVariable().getValue(),
																	(long) getFrameHeightVariable().getValue(),
																	1);
		}
	}

	public final void ensureEnough3DFramesAreAvailable(final int pNumberOf3DFramesNeeded)
	{
		synchronized (mLock)
		{
			DcamFrame.preallocateFrames(pNumberOf3DFramesNeeded,
																	(long) getFrameBytesPerPixelVariable().getValue(),
																	(long) getFrameWidthVariable().getValue(),
																	(long) getFrameHeightVariable().getValue(),
																	(long) getFrameDepthVariable().getValue());
		}
	}

	private DcamFrame request2DFrames()
	{
		synchronized (mLock)
		{
			return DcamFrame.requestFrame((long) getFrameBytesPerPixelVariable().getValue(),
																		(long) getFrameWidthVariable().getValue(),
																		(long) getFrameHeightVariable().getValue(),
																		cDcamJNumberOfBuffers);
		}
	}

	private DcamFrame request3DFrame()
	{
		synchronized (mLock)
		{
			return DcamFrame.requestFrame((long) getFrameBytesPerPixelVariable().getValue(),
																		(long) getFrameWidthVariable().getValue(),
																		(long) getFrameHeightVariable().getValue(),
																		(long) getFrameDepthVariable().getValue());
		}
	}

	@Override
	public boolean start()
	{
		synchronized (mLock)
		{
			if (getIsAcquiringVariable().getBooleanValue())
			{
				if (isReOpenDeviceNeeded())
				{
					stop();
				}
				else
				{
					return true;
				}
			}
			try
			{
				System.out.println(this.getClass().getSimpleName() + ": start()");

				if (isReOpenDeviceNeeded())
				{
					reopen();
				}

				mDcamJToStackConverterAndProcessing.start();

				final boolean lContinuousAcquisition = !getSingleShotModeVariable().getBooleanValue();

				boolean lSuccess;
				if (getStackModeVariable().getBooleanValue())
				{
					final DcamFrame lInitialVideoFrame = request3DFrame();
					lSuccess = mDcamAcquisition.startAcquisition(	lContinuousAcquisition,
																												true,
																												true,
																												false,
																												lInitialVideoFrame);
				}
				else
				{
					final DcamFrame lInitialVideoFrame = request2DFrames();
					lSuccess = mDcamAcquisition.startAcquisition(	lContinuousAcquisition,
																												false,
																												true,
																												false,
																												lInitialVideoFrame);
				}

				return lSuccess;
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				return false;
			}
		}
	}

	public void reopen()
	{
		synchronized (mLock)
		{
			final boolean lIsAcquiring = getIsAcquiringVariable().getBooleanValue();
			if (lIsAcquiring)
			{
				stop();
			}

			final int lWidth = (int) getFrameWidthVariable().getValue();
			final int lHeight = (int) getFrameHeightVariable().getValue();
			getFrameWidthVariable().setValue(mDcamAcquisition.setFrameWidth(lWidth));
			getFrameHeightVariable().setValue(mDcamAcquisition.setFrameHeight(lHeight));
			DcamFrame.clearFrames();
			mDcamAcquisition.reopen();

			System.out.println(this.getClass().getSimpleName() + ": reopened() done !!!!");
			clearReOpen();

			if (lIsAcquiring)
			{
				start();
			}
		}
	}

	@Override
	public boolean stop()
	{
		synchronized (mLock)
		{
			try
			{
				System.out.println(this.getClass().getSimpleName() + ": stop()");
				if (mDcamAcquisition.isAcquiring())
				{
					mDcamAcquisition.stopAcquisition();
				}
				mDcamJToStackConverterAndProcessing.stop();
				return true;
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				return false;
			}
		}
	}

	@Override
	public boolean close()
	{
		synchronized (mLock)
		{
			try
			{
				mDcamAcquisition.close();
				mDcamJToStackConverterAndProcessing.close();
				return true;
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				return false;
			}
		}
	}

	public void trigger()
	{
		mDcamAcquisition.trigger();
	}

}
