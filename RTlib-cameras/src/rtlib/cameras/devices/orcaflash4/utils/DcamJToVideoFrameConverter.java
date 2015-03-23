package rtlib.cameras.devices.orcaflash4.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessor;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.core.variable.objectv.SingleUpdateTargetObjectVariable;
import rtlib.stack.ContiguousOffHeapPlanarStackFactory;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessorInterface;
import coremem.ContiguousMemoryInterface;
import coremem.recycling.BasicRecycler;
import dcamj.DcamFrame;

public class DcamJToVideoFrameConverter extends SignalStartableDevice	implements
																																			VirtualDeviceInterface
{

	private static final long cMinimalNumberOfAvailableStacks = 10;
	private static final int cMaximalNumberOfAvailableStacks = 20;
	private static final int cMaximalNumberOfLiveStacks = 20;
	private static final long cWaitForReycledStackTimeInMicroSeconds = 1;

	private final ObjectVariable<DcamFrame> mDcamFrameReference;

	private AsynchronousProcessorInterface<DcamFrame, OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>> mAsynchronousConversionProcessor;

	private AsynchronousProcessorBase<OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>, Object> mSendToVariableAsynchronousProcessor;

	final ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new ContiguousOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

	private final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> m2DStackBasicRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(lOffHeapPlanarStackFactory,
																																																																																																																												cMaximalNumberOfAvailableStacks,
																																																																																																																												cMaximalNumberOfLiveStacks,
																																																																																																																												true);
	private final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> m3DStackBasicRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(lOffHeapPlanarStackFactory,
																																																																																																																												cMaximalNumberOfAvailableStacks,
																																																																																																																												cMaximalNumberOfLiveStacks,
																																																																																																																												true);

	private final SingleUpdateTargetObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>> mStackReference = new SingleUpdateTargetObjectVariable<StackInterface<UnsignedShortType, ShortOffHeapAccess>>("OffHeapPlanarStack");

	private final DoubleVariable mStackDepthVariable = new DoubleVariable("StackDepth",
																																				1);

	private final DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable("NumberOfPhases",
																																										1);

	private final ArrayList<StackProcessorInterface<UnsignedShortType, ShortOffHeapAccess, ?, ?>> mStackProcessorList = new ArrayList<StackProcessorInterface<UnsignedShortType, ShortOffHeapAccess, ?, ?>>();

	public DcamJToVideoFrameConverter(final ObjectVariable<DcamFrame> pDcamFrameReference,
																		final int pMaxQueueSize)
	{
		super("DcamJToVideoFrameConverter");

		mDcamFrameReference = pDcamFrameReference;

		mDcamFrameReference.sendUpdatesTo(new ObjectVariable<DcamFrame>("DcamFrame")
		{
			@Override
			public DcamFrame setEventHook(final DcamFrame pOldDcamFrame,
																		final DcamFrame pNewDcamFrame)
			{
				// System.out.println("DcamJToVideoFrameConverterAndProcessing->" +
				// pNewDcamFrame.toString());
				mAsynchronousConversionProcessor.passOrWait(pNewDcamFrame);
				return super.setEventHook(pOldDcamFrame, pNewDcamFrame);
			}
		});

		final ProcessorInterface<DcamFrame, StackInterface<UnsignedShortType, ShortOffHeapAccess>> lProcessor = new ProcessorInterface<DcamFrame, StackInterface<UnsignedShortType, ShortOffHeapAccess>>()
		{

			@Override
			public StackInterface<UnsignedShortType, ShortOffHeapAccess> process(final DcamFrame pInput)
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

		mAsynchronousConversionProcessor = new AsynchronousProcessor<DcamFrame, OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>>(	"DcamJToVideoFrameConverter",
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
	protected StackInterface<UnsignedShortType, ShortOffHeapAccess> convert(final DcamFrame pDcamFrame)
	{

		try
		{
			// final boolean lCopySucceeded = false;

			final long lNumberOfImagesPerPlane = (long) mNumberOfImagesPerPlaneVariable.getValue();
			final long lStackDepth = (long) mStackDepthVariable.getValue();

			final StackRequest<UnsignedShortType> lStackRequest = StackRequest.build(	new UnsignedShortType(),
																																								pDcamFrame.getWidth(),
																																								pDcamFrame.getHeight(),
																																								lStackDepth);

			StackInterface<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStack = null;
			if (lStackDepth == 1)
			{
				if (m2DStackBasicRecycler.getNumberOfAvailableObjects() < cMinimalNumberOfAvailableStacks)
					m2DStackBasicRecycler.ensurePreallocated(	cMinimalNumberOfAvailableStacks,
																										lStackRequest);

				lOffHeapPlanarStack = m2DStackBasicRecycler.getOrWait(cWaitForReycledStackTimeInMicroSeconds,
																															TimeUnit.MICROSECONDS,
																															lStackRequest);
			}
			else
			{
				if (m3DStackBasicRecycler.getNumberOfAvailableObjects() < cMinimalNumberOfAvailableStacks)
					m3DStackBasicRecycler.ensurePreallocated(	cMinimalNumberOfAvailableStacks,
																										lStackRequest);

				lOffHeapPlanarStack = m3DStackBasicRecycler.getOrWait(cWaitForReycledStackTimeInMicroSeconds,
																															TimeUnit.MICROSECONDS,
																															lStackRequest);
			}

			// System.out.println("m2DStackBasicRecycler.getNumberOfAvailableObjects()="
			// + m2DStackBasicRecycler.getNumberOfAvailableObjects());
			// System.out.println("m2DStackBasicRecycler.getNumberOfLiveObjects()=" +
			// m2DStackBasicRecycler.getNumberOfLiveObjects());

			/*System.out.println("m3DStackBasicRecycler.getNumberOfAvailableObjects()=" + m3DStackBasicRecycler.getNumberOfAvailableObjects());
			System.out.println("m3DStackBasicRecycler.getNumberOfLiveObjects()=" + m3DStackBasicRecycler.getNumberOfLiveObjects());/**/

			if (lOffHeapPlanarStack == null)
			{
				System.err.println("Failed to obtain stack from request");
				return null;
			}

			lOffHeapPlanarStack.setIndex(pDcamFrame.getIndex());
			lOffHeapPlanarStack.setTimeStampInNanoseconds(pDcamFrame.getFrameTimeStampInNs());
			lOffHeapPlanarStack.setNumberOfImagesPerPlane(lNumberOfImagesPerPlane);

			final long lNumberOfImages = pDcamFrame.getDepth();

			for (int i = 0; i < lNumberOfImages; i++)
			{
				final ContiguousMemoryInterface lContiguousMemory = lOffHeapPlanarStack.getContiguousMemory(i);
				pDcamFrame.getPointerForSinglePlane(i)
									.copyTo(lContiguousMemory.getBridJPointer(Short.class),
													lContiguousMemory.getSizeInBytes());
			}

			pDcamFrame.release();

			return lOffHeapPlanarStack;

		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return null;
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

	public DoubleVariable getStackDepthVariable()
	{
		return mStackDepthVariable;
	}

	public DoubleVariable getNumberOfImagesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
	}

}
