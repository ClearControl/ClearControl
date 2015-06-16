package rtlib.cameras.devices.orcaflash4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.cameras.StackCameraDeviceBase;
import rtlib.cameras.devices.orcaflash4.utils.DcamJToVideoFrameConverter;
import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.device.OpenCloseDeviceInterface;
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
																	StackCameraDeviceBase<UnsignedShortType, ShortOffHeapAccess> implements
																																															OpenCloseDeviceInterface,
																																															AsynchronousExecutorServiceAccess
{
	public static final int cStackProcessorQueueSize = 100;
	public static final int cDcamJNumberOfBuffers = 1024;

	private final int mCameraDeviceIndex;

	private final DcamAcquisition mDcamAcquisition;

	private final ObjectVariable<DcamFrame> mFrameReference = new ObjectVariable<DcamFrame>("DCamJVideoFrame");

	private final DcamJToVideoFrameConverter mDcamJToStackConverterAndProcessing;

	private final Object mLock = new Object();


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
				/*	System.out.println("frameArrived: hashcode=" + pDcamFrame.hashCode()
															+ " index="
															+ pDcamFrame.getIndex()
															+ " pFrameIndexInBufferList="
															+ pFrameIndexInBufferList);/**/
				mFrameReference.setReference(pDcamFrame);
			}

		});

		mLineReadOutTimeInMicrosecondsVariable = new DoubleVariable("LineReadOutTimeInMicroseconds",
																																9.74);

		mStackBytesPerPixelVariable = new DoubleVariable(	"BytesPerPixel",
																											mDcamAcquisition.getFrameBytesPerPixel());

		mStackWidthVariable = new DoubleVariable("FrameWidth", 2048)
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

		mStackHeightVariable = new DoubleVariable("FrameHeight", 2048)
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

		mStackDepthVariable = new DoubleVariable("FrameDepth", 64)
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


		mDcamJToStackConverterAndProcessing = new DcamJToVideoFrameConverter(	mFrameReference,
																																					cStackProcessorQueueSize);

		getNumberOfImagesPerPlaneVariable().sendUpdatesTo(mDcamJToStackConverterAndProcessing.getNumberOfImagesPerPlaneVariable());

		mStackDepthVariable.sendUpdatesTo(mDcamJToStackConverterAndProcessing.getStackDepthVariable());

		mStackReference = mDcamJToStackConverterAndProcessing.getStackReferenceVariable();

	}

	protected ObjectVariable<DcamFrame> getInternalFrameReferenceVariable()
	{
		return mFrameReference;
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
				mDcamJToStackConverterAndProcessing.start();
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
																	(long) getStackBytesPerPixelVariable().getValue(),
																	(long) getStackWidthVariable().getValue(),
																	(long) getStackHeightVariable().getValue(),
																	1);
		}
	}

	public final void ensureEnough3DFramesAreAvailable(final int pNumberOf3DFramesNeeded)
	{
		synchronized (mLock)
		{
			DcamFrame.preallocateFrames(pNumberOf3DFramesNeeded,
																	(long) getStackBytesPerPixelVariable().getValue(),
																	(long) getStackWidthVariable().getValue(),
																	(long) getStackHeightVariable().getValue(),
																	(long) getStackDepthVariable().getValue());
		}
	}

	private DcamFrame request2DFrames()
	{
		synchronized (mLock)
		{
			return DcamFrame.requestFrame((long) getStackBytesPerPixelVariable().getValue(),
																		(long) getStackWidthVariable().getValue(),
																		(long) getStackHeightVariable().getValue(),
																		cDcamJNumberOfBuffers);
		}
	}

	private DcamFrame request3DFrame()
	{
		synchronized (mLock)
		{
			return DcamFrame.requestFrame((long) getStackBytesPerPixelVariable().getValue(),
																		(long) getStackWidthVariable().getValue(),
																		(long) getStackHeightVariable().getValue(),
																		(long) getStackDepthVariable().getValue());
		}
	}

	@Override
	public Future<Boolean> playQueue()
	{
		super.playQueue();
		
		acquisition(false,
								getStackModeVariable().getBooleanValue(),
								false);

		final Future<Boolean> lFuture = executeAsynchronously(new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception
			{
				mDcamAcquisition.waitAcquisitionFinishedAndStop();
				return true;
			}
		});

		return lFuture;
	}

	@Override
	public boolean start()
	{
		synchronized (mLock)
		{
			try
			{
				return acquisition(true, false, false);
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				return false;
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
				mDcamAcquisition.stopAcquisition();
				return true;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void reopen()
	{
		synchronized (mLock)
		{
			final boolean lIsAcquiring = getIsAcquiringVariable().getBooleanValue();
			if (lIsAcquiring)
			{
				stop();
			}

			final int lWidth = (int) getStackWidthVariable().getValue();
			final int lHeight = (int) getStackHeightVariable().getValue();
			getStackWidthVariable().setValue(mDcamAcquisition.setFrameWidth(lWidth));
			getStackHeightVariable().setValue(mDcamAcquisition.setFrameHeight(lHeight));
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

	public Boolean acquisition(	boolean pContinuous,
															boolean pStackMode,
															boolean pWaitToFinish)
	{
		synchronized (mLock)
		{
			System.out.println(this.getClass().getSimpleName() + ": acquisition() begin");

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

				if (isReOpenDeviceNeeded())
				{
					reopen();
				}

				boolean lSuccess = false;

				if (pStackMode)
				{
					final DcamFrame lInitialVideoFrame = request3DFrame();
					lSuccess = mDcamAcquisition.startAcquisition(	pContinuous,
																												true,
																												true,
																												pWaitToFinish,
																												lInitialVideoFrame);
				}
				else
				{
					final DcamFrame lInitialVideoFrame = request2DFrames();
					lSuccess = mDcamAcquisition.startAcquisition(	pContinuous,
																												false,
																												true,
																												pWaitToFinish,
																												lInitialVideoFrame);

				}

				System.out.println(this.getClass().getSimpleName() + ": acquisition() end");

				return lSuccess;
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
				mDcamJToStackConverterAndProcessing.stop();
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

	@Override
	public void trigger()
	{
		mDcamAcquisition.trigger();
	}

	@Override
	public DoubleVariable getLineReadOutTimeInMicrosecondsVariable()
	{
		return mLineReadOutTimeInMicrosecondsVariable;
	}

}
