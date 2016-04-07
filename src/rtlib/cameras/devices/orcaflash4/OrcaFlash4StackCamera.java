package rtlib.cameras.devices.orcaflash4;

import gnu.trove.list.array.TByteArrayList;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;

import rtlib.cameras.StackCameraDeviceBase;
import rtlib.cameras.devices.orcaflash4.utils.DcamJToVideoFrameConverter;
import rtlib.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.units.Magnitude;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import dcamj.DcamAcquisition;
import dcamj.DcamAcquisition.TriggerType;
import dcamj.DcamAcquisitionListener;
import dcamj.DcamFrame;
import dcamj.DcamProperties;

public class OrcaFlash4StackCamera extends StackCameraDeviceBase implements
																																OpenCloseDeviceInterface,
																																AsynchronousExecutorServiceAccess
{
	public static final int cDcamJNumberOfBuffers = 1024;

	private final int mCameraDeviceIndex;

	private final DcamAcquisition mDcamAcquisition;

	private final ObjectVariable<Pair<TByteArrayList, DcamFrame>> mFrameReference = new ObjectVariable<>("DCamJVideoFrame");

	private final DcamJToVideoFrameConverter mDcamJToStackConverterAndProcessing;

	private final Object mLock = new Object();

	private int mStackProcessorQueueSize = 6;

	private long mWaitForRecycledStackTimeInMicroSeconds = 1 * 1000 * 1000;

	public static final OrcaFlash4StackCamera buildWithExternalTriggering(final int pCameraDeviceIndex,
																																				boolean pFlipX)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.ExternalFastEdge,
																			pFlipX);
	}

	public static final OrcaFlash4StackCamera buildWithInternalTriggering(final int pCameraDeviceIndex,
																																				boolean pFlipX)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.Internal,
																			pFlipX);
	}

	public static final OrcaFlash4StackCamera buildWithSoftwareTriggering(final int pCameraDeviceIndex,
																																				boolean pFlipX)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.Software,
																			pFlipX);
	}

	private OrcaFlash4StackCamera(final int pCameraDeviceIndex,
																final TriggerType pTriggerType,
																boolean pFlipX)
	{
		super("OrcaFlash4Camera" + pCameraDeviceIndex);

		mCameraDeviceIndex = pCameraDeviceIndex;
		mDcamAcquisition = new DcamAcquisition(getCameraDeviceIndex());
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
				System.out.println("frameArrived: hashcode=" + pDcamFrame.hashCode()
														+ " index="
														+ pDcamFrame.getIndex()
														+ " pFrameIndexInBufferList="
														+ pFrameIndexInBufferList);/**/

				TByteArrayList lKeepAcquiredImageArray = null;
				// if (mStackMode.getBooleanValue())
				{
					do
					{
						lKeepAcquiredImageArray = mKeepAcquiredImageArrayQueue.remove();
						System.out.println("lKeepAcquiredImageArray=" + lKeepAcquiredImageArray);
					}
					while (lKeepAcquiredImageArray.size() != pDcamFrame.getDepth());
				}

				mFrameReference.setReference(Pair.of(	lKeepAcquiredImageArray,
																							pDcamFrame));
			}

		});

		mLineReadOutTimeInMicrosecondsVariable = new DoubleVariable("LineReadOutTimeInMicroseconds",
																																9.74);

		mStackBytesPerPixelVariable = new DoubleVariable(	"BytesPerPixel",
																											mDcamAcquisition.getFrameBytesPerPixel());

		mStackWidthVariable = new DoubleVariable("FrameWidth", 2048)
		{
			@Override
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewValue)
			{
				synchronized (mLock)
				{
					requestReOpen();
					final double lNewValue = pNewValue;
					final double lRoundto4 = DcamProperties.roundto4((int) lNewValue);
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
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewValue)
			{
				synchronized (mLock)
				{
					requestReOpen();
					final double lNewValue = pNewValue;
					final double lRoundto4 = DcamProperties.roundto4((int) lNewValue);
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
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewValue)
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
			public Double setEventHook(	final Double pOldExposureInMicroseconds,
																	final Double pExposureInMicroseconds)
			{
				synchronized (mLock)
				{
					final double lEffectiveExposureInSeconds = mDcamAcquisition.setExposureInSeconds(Magnitude.micro2unit(pExposureInMicroseconds));
					final double lEffectiveExposureInMicroSeconds = Magnitude.unit2micro(lEffectiveExposureInSeconds);
					return super.setEventHook(pOldExposureInMicroseconds,
																		lEffectiveExposureInMicroSeconds);
				}
			}

			@Override
			public double getValue()
			{
				return Magnitude.unit2micro(mDcamAcquisition.getExposureInSeconds());
			}
		};

		mIsAcquiring = new BooleanVariable("IsAcquiring", false)
		{

			@Override
			public Double getEventHook(Double pCurrentValue)
			{
				return BooleanVariable.boolean2double(mDcamAcquisition.isAcquiring());
			}

		};

		/*
		 * DcamJToVideoFrameConverter(final int pCameraId,
																		final ObjectVariable<Pair<TByteArrayList, DcamFrame>> pDcamFrameReference,
																		final int pMaxQueueSize,
																		final int pMinimalNumberOfAvailableStacks,
																		final int pMaximalNumberOfAvailableStacks,
																		final int pMaximalNumberOfLiveStacks,
																		long pWaitForReycledStackTimeInMicroSeconds,
																		final boolean pFlipX)
		 */
		mDcamJToStackConverterAndProcessing = new DcamJToVideoFrameConverter(	this,
																																					mFrameReference,
																																					pFlipX);

		getNumberOfImagesPerPlaneVariable().sendUpdatesTo(mDcamJToStackConverterAndProcessing.getNumberOfImagesPerPlaneVariable());

		mStackReference = mDcamJToStackConverterAndProcessing.getStackReferenceVariable();

	}

	public void setBinning(int pBinSize)
	{
		mDcamAcquisition.getProperties().setBinning(pBinSize);
	}

	protected ObjectVariable<Pair<TByteArrayList, DcamFrame>> getInternalFrameReferenceVariable()
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
				mDcamAcquisition.getProperties().setOutputTriggerToExposure();
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

	public int getCameraDeviceIndex()
	{
		return mCameraDeviceIndex;
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
				System.out.println("mDcamAcquisition.waitAcquisitionFinishedAndStop();");
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
			catch (final Throwable e)
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
			// System.out.println(this.getClass().getSimpleName() +
			// ": acquisition() begin");

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

				// System.out.println(this.getClass().getSimpleName() +
				// ": acquisition() end");

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

	public int getStackProcessorQueueSize()
	{
		return mStackProcessorQueueSize;
	}

	public void setStackProcessorQueueSize(int pStackProcessorQueueSize)
	{
		mStackProcessorQueueSize = pStackProcessorQueueSize;
	}

	public long getWaitForRecycledStackTimeInMicroSeconds()
	{
		return mWaitForRecycledStackTimeInMicroSeconds;
	}

	public void setWaitForReycledStackTimeInMicroSeconds(long pWaitForReycledStackTimeInMicroSeconds)
	{
		mWaitForRecycledStackTimeInMicroSeconds = pWaitForReycledStackTimeInMicroSeconds;
	}

}
