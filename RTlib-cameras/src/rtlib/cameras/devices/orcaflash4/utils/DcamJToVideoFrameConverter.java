package rtlib.cameras.devices.orcaflash4.utils;

import gnu.trove.list.array.TByteArrayList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.apache.commons.lang3.tuple.Pair;
import org.bridj.Pointer;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessor;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.device.OpenCloseDeviceInterface;
import rtlib.core.device.SignalStartableDevice;
import rtlib.core.variable.types.doublev.DoubleVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.core.variable.types.objectv.SingleUpdateTargetObjectVariable;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessorInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.BasicRecycler;
import dcamj.DcamFrame;

public class DcamJToVideoFrameConverter extends SignalStartableDevice	implements
																		OpenCloseDeviceInterface
{

	private static final long cMinimalNumberOfAvailableStacks = 10;
	private static final int cMaximalNumberOfAvailableStacks = 20;
	private static final int cMaximalNumberOfLiveStacks = 20;
	private static final long cWaitForReycledStackTimeInMicroSeconds = 1;

	private int mCameraId;

	private final ObjectVariable<Pair<TByteArrayList, DcamFrame>> mDcamFrameReference;

	private AsynchronousProcessorInterface<Pair<TByteArrayList, DcamFrame>, OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>> mAsynchronousConversionProcessor;

	private AsynchronousProcessorBase<OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>, Object> mSendToVariableAsynchronousProcessor;

	final ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

	private final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> m2DStackBasicRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(	lOffHeapPlanarStackFactory,
																																																															cMaximalNumberOfAvailableStacks,
																																																															cMaximalNumberOfLiveStacks,
																																																															true);
	private final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> m3DStackBasicRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(	lOffHeapPlanarStackFactory,
																																																															cMaximalNumberOfAvailableStacks,
																																																															cMaximalNumberOfLiveStacks,
																																																															true);

	private final SingleUpdateTargetObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>> mStackReference = new SingleUpdateTargetObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>>("OffHeapPlanarStack");

	private final DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable(	"NumberOfPhases",
																						1);

	private final ArrayList<StackProcessorInterface<UnsignedShortType, ShortOffHeapAccess, ?, ?>> mStackProcessorList = new ArrayList<StackProcessorInterface<UnsignedShortType, ShortOffHeapAccess, ?, ?>>();
	private boolean mFlipX;

	public DcamJToVideoFrameConverter(	final int pCameraId,
										final ObjectVariable<Pair<TByteArrayList, DcamFrame>> pDcamFrameReference,
										final int pMaxQueueSize,
										final boolean pFlipX)
	{
		super("DcamJToVideoFrameConverter");
		mCameraId = pCameraId;

		mDcamFrameReference = pDcamFrameReference;
		mFlipX = pFlipX;

		mDcamFrameReference.sendUpdatesTo(new ObjectVariable<Pair<TByteArrayList, DcamFrame>>("DcamFrame")
		{
			@Override
			public Pair<TByteArrayList, DcamFrame> setEventHook(final Pair<TByteArrayList, DcamFrame> pOldDcamFrame,
																final Pair<TByteArrayList, DcamFrame> pNewDcamFrame)
			{
				// System.out.println("DcamJToVideoFrameConverterAndProcessing->"
				// +
				// pNewDcamFrame.toString());
				mAsynchronousConversionProcessor.passOrWait(pNewDcamFrame);
				return super.setEventHook(	pOldDcamFrame,
											pNewDcamFrame);
			}
		});

		final ProcessorInterface<Pair<TByteArrayList, DcamFrame>, StackInterface<UnsignedShortType, ShortOffHeapAccess>> lProcessor = new ProcessorInterface<Pair<TByteArrayList, DcamFrame>, StackInterface<UnsignedShortType, ShortOffHeapAccess>>()
		{

			@Override
			public StackInterface<UnsignedShortType, ShortOffHeapAccess> process(final Pair<TByteArrayList, DcamFrame> pInput)
			{
				// System.out.println("mAsynchronousConversionProcessor.process input.index= ->"
				// + pInput.getIndex());
				final StackInterface<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStack = convert(pInput);
				return lOffHeapPlanarStack;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		mAsynchronousConversionProcessor = new AsynchronousProcessor<Pair<TByteArrayList, DcamFrame>, OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>>(	"DcamJToVideoFrameConverter",
																																									pMaxQueueSize,
																																									lProcessor);

		mSendToVariableAsynchronousProcessor = new AsynchronousProcessorBase<OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>, Object>("SendToVariableAsynchronousProcessor",
																																				pMaxQueueSize)
		{
			@Override
			public Object process(final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> pStack)
			{
				/*System.out.println("sendtovar: hashcode=" + pStack.hashCode()
														+ " index="
														+ pStack.getIndex());/**/
				mStackReference.setReference(pStack);
				return null;
			}
		};

		mAsynchronousConversionProcessor.connectToReceiver(mSendToVariableAsynchronousProcessor);

	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	protected StackInterface<UnsignedShortType, ShortOffHeapAccess> convert(final Pair<TByteArrayList, DcamFrame> pDcamFrame)
	{

		try
		{
			// final boolean lCopySucceeded = false;

			final long lNumberOfImagesPerPlane = (long) mNumberOfImagesPerPlaneVariable.getValue();
			// final long lStackDepth = (long) mStackDepthVariable.getValue();

			final long lNumberOfImages = pDcamFrame.getRight()
													.getDepth();

			long lNumberOfImagesKept = 0;

			for (int i = 0; i < lNumberOfImages; i++)
				if (pDcamFrame.getLeft().get(i) > 0)
					lNumberOfImagesKept++;

			if (lNumberOfImagesKept == 0)
				return null;

			final StackRequest<UnsignedShortType> lStackRequest = StackRequest.build(	new UnsignedShortType(),
																						pDcamFrame.getRight()
																									.getWidth(),
																						pDcamFrame.getRight()
																									.getHeight(),
																						lNumberOfImagesKept);

			StackInterface<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStack = null;
			if (lNumberOfImagesKept == 1)
			{
				if (m2DStackBasicRecycler.getNumberOfAvailableObjects() < cMinimalNumberOfAvailableStacks)
					m2DStackBasicRecycler.ensurePreallocated(	cMinimalNumberOfAvailableStacks,
																lStackRequest);

				lOffHeapPlanarStack = m2DStackBasicRecycler.getOrWait(	cWaitForReycledStackTimeInMicroSeconds,
																		TimeUnit.MICROSECONDS,
																		lStackRequest);
			}
			else
			{
				if (m3DStackBasicRecycler.getNumberOfAvailableObjects() < cMinimalNumberOfAvailableStacks)
					m3DStackBasicRecycler.ensurePreallocated(	cMinimalNumberOfAvailableStacks,
																lStackRequest);

				lOffHeapPlanarStack = m3DStackBasicRecycler.getOrWait(	cWaitForReycledStackTimeInMicroSeconds,
																		TimeUnit.MICROSECONDS,
																		lStackRequest);
			}

			// System.out.println("m2DStackBasicRecycler.getNumberOfAvailableObjects()="
			// + m2DStackBasicRecycler.getNumberOfAvailableObjects());
			// System.out.println("m2DStackBasicRecycler.getNumberOfLiveObjects()="
			// +
			// m2DStackBasicRecycler.getNumberOfLiveObjects());

			/*System.out.println("m3DStackBasicRecycler.getNumberOfAvailableObjects()=" + m3DStackBasicRecycler.getNumberOfAvailableObjects());
			System.out.println("m3DStackBasicRecycler.getNumberOfLiveObjects()=" + m3DStackBasicRecycler.getNumberOfLiveObjects());/**/

			if (lOffHeapPlanarStack == null)
			{
				System.err.println("Failed to obtain stack from request");
				return null;
			}

			lOffHeapPlanarStack.setChannel(mCameraId);
			lOffHeapPlanarStack.setIndex(pDcamFrame.getRight()
													.getIndex());
			lOffHeapPlanarStack.setTimeStampInNanoseconds(pDcamFrame.getRight()
																	.getFrameTimeStampInNs());
			lOffHeapPlanarStack.setNumberOfImagesPerPlane(lNumberOfImagesPerPlane);

			for (int i = 0, j = 0; i < lNumberOfImages; i++)
				if (pDcamFrame.getLeft().get(i) > 0)
				{
					final OffHeapMemory lTargetBuffer = (OffHeapMemory) lOffHeapPlanarStack.getContiguousMemory(j++);

					if (mFlipX)
					{
						Pointer<Byte> lPointerForSinglePlane = pDcamFrame.getRight()
																			.getPointerForSinglePlane(i);

						long lWidth = lOffHeapPlanarStack.getWidth();
						long lHeight = lOffHeapPlanarStack.getHeight();

						OffHeapMemory lSourceBuffer = OffHeapMemory.wrapPointer(lPointerForSinglePlane);

						copyAndFlipAlongX(	lTargetBuffer,
											lWidth,
											lHeight,
											lSourceBuffer);
					}
					else
					{
						pDcamFrame.getRight()
									.getPointerForSinglePlane(i)
									.copyTo(lTargetBuffer.getBridJPointer(Short.class),
											lTargetBuffer.getSizeInBytes());
					}
				}

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

	public SingleUpdateTargetObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>> getStackReferenceVariable()
	{
		return mStackReference;
	}

	public DoubleVariable getNumberOfImagesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
	}

}
