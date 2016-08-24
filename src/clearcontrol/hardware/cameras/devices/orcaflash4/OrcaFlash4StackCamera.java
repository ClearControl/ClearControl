package clearcontrol.hardware.cameras.devices.orcaflash4;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.units.Magnitude;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.hardware.cameras.StackCameraDeviceBase;
import clearcontrol.hardware.cameras.devices.orcaflash4.utils.DcamJToVideoFrameConverter;
import dcamj.DcamAcquisition;
import dcamj.DcamAcquisition.TriggerType;
import dcamj.DcamAcquisitionListener;
import dcamj.DcamFrame;
import dcamj.DcamProperties;
import gnu.trove.list.array.TByteArrayList;

public class OrcaFlash4StackCamera extends StackCameraDeviceBase implements
																																OpenCloseDeviceInterface,
																																AsynchronousExecutorServiceAccess
{
	
	// declaring instance variables
	
	public static final int cDcamJNumberOfBuffers = 1024;

	private final int mCameraDeviceIndex; // camera id index used in the name and passed to the constructor

	private final DcamAcquisition mDcamAcquisition; // creating the environment for the camera

	private final Variable<Pair<TByteArrayList, DcamFrame>> mFrameReference = new Variable<>("DCamJVideoFrame"); // some weird hamamatsu stuff, not sure if I need it with Andor

	private final DcamJToVideoFrameConverter mDcamJToStackConverterAndProcessing; // hamamtsu again

	private final Object mLock = new Object(); //lock object

	private int mStackProcessorQueueSize = 6; // queue size, would be nice to have a better understanding of this number

	private long mWaitForRecycledStackTimeInMicroSeconds = 1 * 1000 * 1000; // whatever

	
	// exotic calls to constructors to ensure proper triggering
	
	// external
	public static final OrcaFlash4StackCamera buildWithExternalTriggering(final int pCameraDeviceIndex,
																																				boolean pFlipX)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.ExternalFastEdge,
																			pFlipX);
	}

	// internal
	public static final OrcaFlash4StackCamera buildWithInternalTriggering(final int pCameraDeviceIndex,
																																				boolean pFlipX)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.Internal,
																			pFlipX);
	}

	// software
	public static final OrcaFlash4StackCamera buildWithSoftwareTriggering(final int pCameraDeviceIndex,
																																				boolean pFlipX)
	{
		return new OrcaFlash4StackCamera(	pCameraDeviceIndex,
																			TriggerType.Software,
																			pFlipX);
	}

	
	// constructor per se
	private OrcaFlash4StackCamera(final int pCameraDeviceIndex,
																final TriggerType pTriggerType,
																boolean pFlipX)
	{
		
		// run the constructors of parents
		super("OrcaFlash4Camera" + pCameraDeviceIndex);
		
		// initialize the index instance variable with the given parameter
		mCameraDeviceIndex = pCameraDeviceIndex;
		// create the environment
		mDcamAcquisition = new DcamAcquisition(getCameraDeviceIndex());
		// set trigger type
		mDcamAcquisition.setTriggerType(pTriggerType);
		// some camera specific listener functional interface with the method frameArrived
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

				TByteArrayList lKeepAcquiredImageArray = null;
				// if (mStackMode.getBooleanValue())
				{
					do
					{
						lKeepAcquiredImageArray = mKeepAcquiredImageArrayQueue.remove();
						// System.out.println("lKeepAcquiredImageArray=" +
						// lKeepAcquiredImageArray);
					}
					while (lKeepAcquiredImageArray.size() != pDcamFrame.getDepth());
				}

				mFrameReference.set(Pair.of(lKeepAcquiredImageArray,
																		pDcamFrame));
			}

		});
		// ----------------------- done with the listener -------- //
		
		// more instance vars
		mLineReadOutTimeInMicrosecondsVariable = new Variable<Double>("LineReadOutTimeInMicroseconds",
																																	9.74);
		// even more
		mStackBytesPerPixelVariable = new Variable<Long>(	"BytesPerPixel",
																											mDcamAcquisition.getFrameBytesPerPixel());
		// again
		mStackWidthVariable = new Variable<Long>("FrameWidth", 2048L)
		{
			@Override
			public Long setEventHook(	final Long pOldValue,
																final Long pNewValue)
			{
				synchronized (mLock)
				{
					requestReOpen();
					final long lNewValue = pNewValue;
					final long lRoundto4 = DcamProperties.roundto4((int) lNewValue);
					if (lRoundto4 != pNewValue)
					{
						this.set(lRoundto4);
					}
					return super.setEventHook(pOldValue, lRoundto4);
				}
			}

		};

		mStackHeightVariable = new Variable<Long>("FrameHeight", 2048L)
		{
			@Override
			public Long setEventHook(	final Long pOldValue,
																final Long pNewValue)
			{
				synchronized (mLock)
				{
					requestReOpen();
					final long lNewValue = pNewValue;
					final long lRoundto4 = DcamProperties.roundto4((int) lNewValue);
					if (lRoundto4 != pNewValue)
					{
						this.set(lRoundto4);
					}
					return super.setEventHook(pOldValue, lRoundto4);
				}
			}
		};

		mStackDepthVariable = new Variable<Long>("FrameDepth", 64L)
		{
			@Override
			public Long setEventHook(	final Long pOldValue,
																final Long pNewValue)
			{
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mStackMaxWidthVariable = new Variable<Long>("FrameMaxWidth",
																								2048L);
		mStackMaxHeightVariable = new Variable<Long>(	"FrameMaxHeight",
																									2048L);

		mPixelSizeinNanometersVariable = new Variable<Double>("PixelSizeInNanometers",
																													160.0);

		mExposureInMicrosecondsVariable = new Variable<Double>(	"ExposureInMicroseconds",
																														5000.0)
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
			public Double get()
			{
				return Magnitude.unit2micro(mDcamAcquisition.getExposureInSeconds());
			}
		};

		mIsAcquiring = new Variable<Boolean>("IsAcquiring", false)
		{

			@Override
			public Boolean getEventHook(Boolean pCurrentValue)
			{
				return mDcamAcquisition.isAcquiring();
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

		mStackVariable = mDcamJToStackConverterAndProcessing.getStackReferenceVariable();

	}

	public void setBinning(int pBinSize)
	{
		mDcamAcquisition.getProperties().setBinning(pBinSize);
	}

	protected Variable<Pair<TByteArrayList, DcamFrame>> getInternalFrameReferenceVariable()
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
																	getStackBytesPerPixelVariable().get(),
																	getStackWidthVariable().get(),
																	getStackHeightVariable().get(),
																	1);
		}
	}

	public final void ensureEnough3DFramesAreAvailable(final int pNumberOf3DFramesNeeded)
	{
		synchronized (mLock)
		{
			DcamFrame.preallocateFrames(pNumberOf3DFramesNeeded,
																	getStackBytesPerPixelVariable().get(),
																	getStackWidthVariable().get(),
																	getStackHeightVariable().get(),
																	getStackDepthVariable().get());
		}
	}

	private DcamFrame request2DFrames()
	{
		synchronized (mLock)
		{
			return DcamFrame.requestFrame(getStackBytesPerPixelVariable().get(),
																		getStackWidthVariable().get(),
																		getStackHeightVariable().get(),
																		cDcamJNumberOfBuffers);
		}
	}

	private DcamFrame request3DFrame()
	{
		synchronized (mLock)
		{
			return DcamFrame.requestFrame(getStackBytesPerPixelVariable().get(),
																		getStackWidthVariable().get(),
																		getStackHeightVariable().get(),
																		getStackDepthVariable().get());
		}
	}

	@Override
	public Future<Boolean> playQueue()
	{
		super.playQueue();

		acquisition(false, getStackModeVariable().get(), false);

		final Future<Boolean> lFuture = executeAsynchronously(new Callable<Boolean>()
		{
			@Override
			public Boolean call() throws Exception
			{
				// System.out.println("mDcamAcquisition.waitAcquisitionFinishedAndStop();");
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
			/*try
			{
				return acquisition(true, false, false);
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
				
			}/**/
			return true;
		}
	}

	@Override
	public boolean stop()
	{
		synchronized (mLock)
		{
			/*try
			{
				mDcamAcquisition.stopAcquisition();
				return true;
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}/**/
		}
		return false;
	}

	@Override
	public void reopen()
	{
		synchronized (mLock)
		{
			final boolean lIsAcquiring = getIsAcquiringVariable().get();
			if (lIsAcquiring)
			{
				stop();
			}

			final int lWidth = getStackWidthVariable().get().intValue();
			final int lHeight = getStackHeightVariable().get().intValue();
			getStackWidthVariable().set(mDcamAcquisition.setFrameWidth(lWidth));
			getStackHeightVariable().set(mDcamAcquisition.setFrameHeight(lHeight));
			DcamFrame.clearFrames();
			mDcamAcquisition.reopen();

			// System.out.println(this.getClass().getSimpleName() +
			// ": reopened() done !!!!");
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

			if (getIsAcquiringVariable().get())
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
	public Variable<Double> getLineReadOutTimeInMicrosecondsVariable()
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
