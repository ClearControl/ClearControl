package clearcontrol.hardware.cameras.devices.orcaflash4.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.bridj.Pointer;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessor;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import clearcontrol.core.concurrent.asyncprocs.ProcessorInterface;
import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.executors.RTlibExecutors;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.util.SingleUpdateTargetObjectVariable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.startstop.StartStopDeviceInterface;
import clearcontrol.hardware.cameras.devices.orcaflash4.OrcaFlash4StackCamera;
import clearcontrol.stack.EmptyStack;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclerInterface;
import dcamj.DcamFrame;
import gnu.trove.list.array.TByteArrayList;

public class DcamJToVideoFrameConverter extends VirtualDevice	implements
																															OpenCloseDeviceInterface,
																															StartStopDeviceInterface,
																															AsynchronousExecutorServiceAccess
{

	private OrcaFlash4StackCamera mOrcaFlash4StackCamera;

	private int mCameraId;

	private boolean mFlipX;
	private long mWaitForRecycledStackTimeInMicroSeconds;
	private int mMinimalNumberOfAvailableStacks;
	private int mProcessingQueueSize;

	private final Variable<Pair<TByteArrayList, DcamFrame>> mDcamFrameReference;

	private AsynchronousProcessorInterface<Pair<TByteArrayList, DcamFrame>, StackInterface> mAsynchronousConversionProcessor;

	private AsynchronousProcessorBase<StackInterface, Object> mSendToVariableAsynchronousProcessor;

	private final SingleUpdateTargetObjectVariable<StackInterface> mStackReference = new SingleUpdateTargetObjectVariable<StackInterface>("OffHeapPlanarStack");

	private final Variable<Long> mNumberOfImagesPerPlaneVariable = new Variable<Long>("NumberOfPhases",
																																										1L);

	public DcamJToVideoFrameConverter(final OrcaFlash4StackCamera pOrcaFlash4StackCamera,
																		Variable<Pair<TByteArrayList, DcamFrame>> pDcamFrameReference,
																		final boolean pFlipX)
	{
		super("DcamJToVideoFrameConverter");
		mOrcaFlash4StackCamera = pOrcaFlash4StackCamera;
		mCameraId = mOrcaFlash4StackCamera.getCameraDeviceIndex();
		mDcamFrameReference = pDcamFrameReference;
		mFlipX = pFlipX;
		mWaitForRecycledStackTimeInMicroSeconds = mOrcaFlash4StackCamera.getWaitForRecycledStackTimeInMicroSeconds();
		mMinimalNumberOfAvailableStacks = mOrcaFlash4StackCamera.getMinimalNumberOfAvailableStacks();
		mProcessingQueueSize = mOrcaFlash4StackCamera.getStackProcessorQueueSize();

		mDcamFrameReference.sendUpdatesTo(new Variable<Pair<TByteArrayList, DcamFrame>>("DcamFrame")
		{
			@Override
			public Pair<TByteArrayList, DcamFrame> setEventHook(final Pair<TByteArrayList, DcamFrame> pOldDcamFrame,
																													final Pair<TByteArrayList, DcamFrame> pNewDcamFrame)
			{
				// System.out.println("DcamJToVideoFrameConverterAndProcessing->"
				// +
				// pNewDcamFrame.toString());
				mAsynchronousConversionProcessor.passOrWait(pNewDcamFrame);
				return super.setEventHook(pOldDcamFrame, pNewDcamFrame);
			}
		});

		final ProcessorInterface<Pair<TByteArrayList, DcamFrame>, StackInterface> lProcessor = new ProcessorInterface<Pair<TByteArrayList, DcamFrame>, StackInterface>()
		{

			@Override
			public StackInterface process(final Pair<TByteArrayList, DcamFrame> pInput)
			{
				// System.out.println("mAsynchronousConversionProcessor.process input.index= ->"
				// + pInput.getIndex());
				final StackInterface lOffHeapPlanarStack = convert(pInput);
				return lOffHeapPlanarStack;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		mAsynchronousConversionProcessor = new AsynchronousProcessor<Pair<TByteArrayList, DcamFrame>, StackInterface>("DcamJToVideoFrameConverter",
																																																									mProcessingQueueSize,
																																																									lProcessor);

		mSendToVariableAsynchronousProcessor = new AsynchronousProcessorBase<StackInterface, Object>(	"SendToVariableAsynchronousProcessor",
																																																	mProcessingQueueSize)
		{
			@Override
			public Object process(final StackInterface pStack)
			{
				/*System.out.println("sendtovar: hashcode=" + pStack.hashCode()
														+ " index="
														+ pStack.getIndex());/**/
				mStackReference.set(pStack);
				return null;
			}
		};

		mAsynchronousConversionProcessor.connectToReceiver(mSendToVariableAsynchronousProcessor);

	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected StackInterface convert(final Pair<TByteArrayList, DcamFrame> pDcamFrame)
	{

		try
		{
			// final boolean lCopySucceeded = false;

			final long lNumberOfImagesPerPlane = mNumberOfImagesPerPlaneVariable.get();
			// final long lStackDepth = (long) mStackDepthVariable.getValue();

			final long lNumberOfImages = pDcamFrame.getRight().getDepth();

			long lNumberOfImagesKept = 0;

			for (int i = 0; i < lNumberOfImages; i++)
				if (pDcamFrame.getLeft().get(i) > 0)
					lNumberOfImagesKept++;

			// if (lNumberOfImagesKept == 0)
			// return null;

			StackInterface lOffHeapPlanarStack = null;

			if (lNumberOfImagesKept == 0)
			{
				lOffHeapPlanarStack = new EmptyStack();
				return lOffHeapPlanarStack;
			}
			else
			{
				RecyclerInterface<StackInterface, StackRequest> lStackRecycler = mOrcaFlash4StackCamera.getStackRecycler();

				final StackRequest lStackRequest = StackRequest.build(pDcamFrame.getRight()
																																				.getWidth(),
																															pDcamFrame.getRight()
																																				.getHeight(),
																															lNumberOfImagesKept);

				if (lStackRecycler.getNumberOfAvailableObjects() < mMinimalNumberOfAvailableStacks)
					lStackRecycler.ensurePreallocated(mMinimalNumberOfAvailableStacks,
																						lStackRequest);

				// System.out.println("before: getNumberOfLiveObjects()=" +
				// lStackRecycler.getNumberOfLiveObjects());
				// System.out.println("before: getNumberOfAvailableObjects()=" +
				// lStackRecycler.getNumberOfAvailableObjects());

				lOffHeapPlanarStack = lStackRecycler.getOrWait(	mWaitForRecycledStackTimeInMicroSeconds,
																												TimeUnit.MICROSECONDS,
																												lStackRequest);

				// System.out.println("after: getNumberOfLiveObjects()=" +
				// lStackRecycler.getNumberOfLiveObjects());
				// System.out.println("after: getNumberOfAvailableObjects()=" +
				// lStackRecycler.getNumberOfAvailableObjects());
			}

			if (lOffHeapPlanarStack == null)
			{
				System.err.println("Failed to obtain stack from request");
				return null;
			}

			lOffHeapPlanarStack.setChannel(mCameraId);
			lOffHeapPlanarStack.setIndex(pDcamFrame.getRight().getIndex());
			lOffHeapPlanarStack.setTimeStampInNanoseconds(pDcamFrame.getRight()
																															.getFrameTimeStampInNs());
			lOffHeapPlanarStack.setNumberOfImagesPerPlane(lNumberOfImagesPerPlane);

			RTlibExecutors.getOrCreateThreadPoolExecutor(	this,
																										Thread.NORM_PRIORITY - 1,
																										Runtime.getRuntime()
																														.availableProcessors(),
																										Runtime.getRuntime()
																														.availableProcessors(),
																										Integer.MAX_VALUE);

			ArrayList<Future> lFutureList = new ArrayList<Future>((int) lNumberOfImages);

			for (int i = 0, j = 0; i < lNumberOfImages; i++)
				if (pDcamFrame.getLeft().get(i) > 0)
				{
					final int fi = i;
					final int fj = j;
					final StackInterface lFinalOffHeapPlanarStack = lOffHeapPlanarStack;

					Runnable lRunnable = () -> {
						final OffHeapMemory lTargetBuffer = (OffHeapMemory) lFinalOffHeapPlanarStack.getContiguousMemory(fj);

						if (mFlipX)
						{
							Pointer<Byte> lPointerForSinglePlane = pDcamFrame.getRight()
																																.getPointerForSinglePlane(fi);

							long lWidth = lFinalOffHeapPlanarStack.getWidth();
							long lHeight = lFinalOffHeapPlanarStack.getHeight();

							OffHeapMemory lSourceBuffer = OffHeapMemory.wrapPointer(lPointerForSinglePlane);

							copyAndFlipAlongX(lTargetBuffer,
																lWidth,
																lHeight,
																lSourceBuffer);
						}
						else
						{
							pDcamFrame.getRight()
												.getPointerForSinglePlane(fi)
												.copyTo(lTargetBuffer.getBridJPointer(Short.class),
																lTargetBuffer.getSizeInBytes());
						}
					};

					Future<?> lFuture = executeAsynchronously(lRunnable);
					lFutureList.add(lFuture);

					j++;
				}

			for (Future<?> lFuture : lFutureList)
				lFuture.get();

			pDcamFrame.getRight().release();

			return lOffHeapPlanarStack;

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private void copyAndFlipAlongX(	final OffHeapMemory lTargetBuffer,
																	long lWidth,
																	long lHeight,
																	OffHeapMemory lSourceBuffer)
	{
		for (int y = 0; y < lHeight; y++)
		{
			long lIndexY = lWidth * y;
			for (int x = 0; x < lWidth; x++)
			{
				long lIndexSourceXY = lIndexY + x;
				long lIndexTargetXY = lIndexY + lWidth - 1 - x;
				lTargetBuffer.setShortAligned(lIndexTargetXY,
																			lSourceBuffer.getShortAligned(lIndexSourceXY));
			}
		}
	}

	@Override
	public boolean open()
	{
		return mSendToVariableAsynchronousProcessor.start() && mAsynchronousConversionProcessor.start();
	}

	@Override
	public boolean start()
	{
		return true;
	}

	@Override
	public boolean stop()
	{
		return true;
	}

	@Override
	public boolean close()
	{
		return mAsynchronousConversionProcessor.stop() && mSendToVariableAsynchronousProcessor.stop();
	}

	public SingleUpdateTargetObjectVariable<StackInterface> getStackReferenceVariable()
	{
		return mStackReference;
	}

	public Variable<Long> getNumberOfImagesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
	}

}
