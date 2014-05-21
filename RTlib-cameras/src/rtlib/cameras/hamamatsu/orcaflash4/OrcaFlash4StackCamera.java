package rtlib.cameras.hamamatsu.orcaflash4;

import rtlib.cameras.StackCameraBase;
import rtlib.cameras.hamamatsu.orcaflash4.utils.DcamJToVideoFrameConverterAndProcessing;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.units.Magnitudes;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.processor.StackProcessing;
import rtlib.stack.processor.StackProcessorInterface;
import dcamj.DcamAcquisition;
import dcamj.DcamAcquisition.TriggerType;
import dcamj.DcamAcquisitionListener;
import dcamj.DcamFrame;
import dcamj.DcamProperties;

public class OrcaFlash4StackCamera extends StackCameraBase	implements
																											VirtualDeviceInterface,
																											StackProcessing
{
	private static final int cStackProcessorQueueSize = 100;

	private final int mCameraDeviceIndex;

	private final DcamAcquisition mDcamAcquisition;

	private final ObjectVariable<DcamFrame> mFrameReference = new ObjectVariable<DcamFrame>("DCamJVideoFrame");

	private final DcamJToVideoFrameConverterAndProcessing mDcamJToStackConverterAndProcessing;

	public OrcaFlash4StackCamera(final int pCameraDeviceIndex)
	{
		this(pCameraDeviceIndex, TriggerType.ExternalFastEdge);
	}

	public OrcaFlash4StackCamera(final int pCameraDeviceIndex,
													final TriggerType pTriggerType)
	{
		super("OrcaFlash4Camera");

		mCameraDeviceIndex = pCameraDeviceIndex;
		mDcamAcquisition = new DcamAcquisition(mCameraDeviceIndex);
		mDcamAcquisition.setTriggerType(pTriggerType);

		// TODO
		mFrameBytesPerPixelVariable.setValue(mDcamAcquisition.getFrameBytesPerPixel());

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

				mFrameReference.setReference(pDcamFrame);
			}

		});

		mFrameWidthVariable = new DoubleVariable("FrameWidth", 2048)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				requestReOpen();
				final double lRoundto4 = DcamProperties.roundto4((int) pNewValue);
				if (lRoundto4 != pNewValue)
				{
					this.setValue(lRoundto4);
				}
				return super.setEventHook(pOldValue, lRoundto4);
			}

		};

		mFrameHeightVariable = new DoubleVariable("FrameHeight", 2048)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				requestReOpen();
				final double lRoundto4 = DcamProperties.roundto4((int) pNewValue);
				if (lRoundto4 != pNewValue)
				{
					this.setValue(lRoundto4);
				}
				return super.setEventHook(pOldValue, lRoundto4);
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

		mPixelSizeinNanometers = new DoubleVariable("PixelSizeInNanometers",
																								160);

		mExposureInMicroseconds = new DoubleVariable(	"ExposureInMicroseconds",
																									5000)
		{
			@Override
			public double setEventHook(	final double pOldExposureInMicroseconds,
																	final double pExposureInMicroseconds)
			{
				final double lEffectiveExposureInSeconds = mDcamAcquisition.setExposureInSeconds(Magnitudes.micro2unit(pExposureInMicroseconds));
				final double lEffectiveExposureInMicroSeconds = Magnitudes.unit2micro(lEffectiveExposureInSeconds);
				return super.setEventHook(pOldExposureInMicroseconds,
																	lEffectiveExposureInMicroSeconds);
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

		mDcamJToStackConverterAndProcessing = new DcamJToVideoFrameConverterAndProcessing(mFrameReference,
																																											cStackProcessorQueueSize);

		mFrameDepthVariable.sendUpdatesTo(mDcamJToStackConverterAndProcessing.getStackDepthVariable());

		mStackReference = mDcamJToStackConverterAndProcessing.getVideoFrameReferenceVariable();

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
	public void addStackProcessor(final StackProcessorInterface pStackProcessor)
	{
		mDcamJToStackConverterAndProcessing.addStackProcessor(pStackProcessor);
	}

	@Override
	public void removeStackProcessor(final StackProcessorInterface pStackProcessor)
	{
		mDcamJToStackConverterAndProcessing.removeStackProcessor(pStackProcessor);
	}

	@Override
	public boolean open()
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
			return false;
		}
	}

	public final void ensureEnough2DFramesAreAvailable(final int pNumberOf2DFramesNeeded)
	{
		DcamFrame.preallocateFrames(pNumberOf2DFramesNeeded,
																(long) getFrameBytesPerPixelVariable().getValue(),
																(long) getFrameWidthVariable().getValue(),
																(long) getFrameHeightVariable().getValue(),
																1);
	}

	public final void ensureEnough3DFramesAreAvailable(final int pNumberOf3DFramesNeeded)
	{
		DcamFrame.preallocateFrames(pNumberOf3DFramesNeeded,
																(long) getFrameBytesPerPixelVariable().getValue(),
																(long) getFrameWidthVariable().getValue(),
																(long) getFrameHeightVariable().getValue(),
																(long) getFrameDepthVariable().getValue());
	}

	private DcamFrame request2DFrame()
	{
		return DcamFrame.requestFrame((long) getFrameBytesPerPixelVariable().getValue(),
																	(long) getFrameWidthVariable().getValue(),
																	(long) getFrameHeightVariable().getValue(),
																	1);
	}

	private DcamFrame request3DFrame()
	{
		return DcamFrame.requestFrame((long) getFrameBytesPerPixelVariable().getValue(),
																	(long) getFrameWidthVariable().getValue(),
																	(long) getFrameHeightVariable().getValue(),
																	(long) getFrameDepthVariable().getValue());
	}

	@Override
	public boolean start()
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

			final boolean lContinuousAcquisition = !mSingleShotModeVariable.getBooleanValue();

			boolean lSuccess;
			if (mStackModeVariable.getBooleanValue())
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
				final DcamFrame lInitialVideoFrame = request2DFrame();
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

	public void reopen()
	{
		final boolean lIsAcquiring = getIsAcquiringVariable().getBooleanValue();
		if (lIsAcquiring)
		{
			stop();
		}

		final int lWidth = (int) mFrameWidthVariable.getValue();
		final int lHeight = (int) mFrameHeightVariable.getValue();
		mFrameWidthVariable.setValue(mDcamAcquisition.setFrameWidth(lWidth));
		mFrameHeightVariable.setValue(mDcamAcquisition.setFrameHeight(lHeight));
		DcamFrame.clearFrames();
		mDcamAcquisition.reopen();

		System.out.println(this.getClass().getSimpleName() + ": reopened() done !!!!");
		clearReOpen();

		if (lIsAcquiring)
		{
			start();
		}
	}

	@Override
	public boolean stop()
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

	@Override
	public boolean close()
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
