package rtlib.cameras.devices.orcaflash4.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessor;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.core.variable.objectv.SingleUpdateTargetObjectVariable;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;
import rtlib.stack.processor.StackProcessorInterface;
import coremem.recycling.Recycler;
import dcamj.DcamFrame;

public class DcamJToVideoFrameConverter extends SignalStartableDevice	implements
																																			VirtualDeviceInterface
{

	private static final long cMinimalNumberOfAvailableStacks = 10;
	private static final int cMaximalNumberOfAvailableStacks = 20;
	private static final long cWaitForReycledStackTimeInMicroSeconds = 1;

	private final ObjectVariable<DcamFrame> mDcamFrameReference;

	private AsynchronousProcessorInterface<DcamFrame, Stack<Character>> mAsynchronousConversionProcessor;

	private AsynchronousProcessorBase<Stack<Character>, Object> mSendToVariableAsynchronousProcessor;

	private final Recycler<Stack<Character>, StackRequest<Character>> m2DStackRecycler = new Recycler<Stack<Character>, StackRequest<Character>>(	Stack.class,
																																																																								cMaximalNumberOfAvailableStacks);
	private final Recycler<Stack<Character>, StackRequest<Character>> m3DStackRecycler = new Recycler<Stack<Character>, StackRequest<Character>>(	Stack.class,
																																																																								cMaximalNumberOfAvailableStacks);

	private final SingleUpdateTargetObjectVariable<Stack<Character>> mStackReference = new SingleUpdateTargetObjectVariable<Stack<Character>>("Stack");

	private final DoubleVariable mStackDepthVariable = new DoubleVariable("StackDepth",
																																				1);

	private final DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable("NumberOfPhases",
																																										1);

	private final ArrayList<StackProcessorInterface<?, ?>> mStackProcessorList = new ArrayList<StackProcessorInterface<?, ?>>();

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

		@SuppressWarnings("rawtypes")
		final ProcessorInterface<DcamFrame, Stack> lProcessor = new ProcessorInterface<DcamFrame, Stack>()
		{
			@SuppressWarnings("rawtypes")
			@Override
			public Stack process(final DcamFrame pInput)
			{
				// System.out.println("mAsynchronousConversionProcessor.process input.index= ->"
				// + pInput.getIndex());
				final Stack lStack = convert(pInput);
				return lStack;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		mAsynchronousConversionProcessor = new AsynchronousProcessor<DcamFrame, Stack<Character>>("DcamJToVideoFrameConverter",
																																															pMaxQueueSize,
																																															lProcessor);

		mSendToVariableAsynchronousProcessor = new AsynchronousProcessorBase<Stack<Character>, Object>(	"SendToVariableAsynchronousProcessor",
																																																		pMaxQueueSize)
		{
			@Override
			public Object process(final Stack<Character> pStack)
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
	protected Stack convert(final DcamFrame pDcamFrame)
	{

		try
		{
			boolean lCopySucceeded = false;

			final long lNumberOfImagesPerPlane = (long) mNumberOfImagesPerPlaneVariable.getValue();
			final long lStackDepthVariable = (long) mStackDepthVariable.getValue();

			final StackRequest<Character> lStackRequest = StackRequest.build(	Character.class,
																																							1,
																																							pDcamFrame.getWidth(),
																																							pDcamFrame.getHeight(),
																																							lNumberOfImagesPerPlane * lStackDepthVariable);

			Stack lStack = null;
			if (lStackDepthVariable == 1)
			{
				if (m2DStackRecycler.getNumberOfAvailableObjects() < cMinimalNumberOfAvailableStacks)
					m2DStackRecycler.ensurePreallocated(cMinimalNumberOfAvailableStacks,
																							lStackRequest);

				lStack = m2DStackRecycler.waitOrRequestRecyclableObject(cWaitForReycledStackTimeInMicroSeconds,
																																TimeUnit.MICROSECONDS,
																																lStackRequest);
			}
			else
			{
				if (m3DStackRecycler.getNumberOfAvailableObjects() < cMinimalNumberOfAvailableStacks)
					m3DStackRecycler.ensurePreallocated(cMinimalNumberOfAvailableStacks,
																							lStackRequest);

				lStack = m3DStackRecycler.waitOrRequestRecyclableObject(cWaitForReycledStackTimeInMicroSeconds,
																																TimeUnit.MICROSECONDS,
																																lStackRequest);
			}

			if (lStack == null)
			{
				System.err.println("Failed to obtain stack from request");
				return null;
			}

			lStack.setStackIndex(pDcamFrame.getIndex());
			lStack.setTimeStampInNanoseconds(pDcamFrame.getFrameTimeStampInNs());
			lStack.setNumberOfImagesPerPlane(lNumberOfImagesPerPlane);

			final Pointer<Byte> lVideoFramePointer = lStack.getPointer();

			lCopySucceeded = pDcamFrame.copyAllPlanesToSinglePointer(	lVideoFramePointer,
																																lNumberOfImagesPerPlane * lStackDepthVariable);

			if (!lCopySucceeded)
				System.err.println("Copy failed!");

			pDcamFrame.release();
			return lStack;

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

	public SingleUpdateTargetObjectVariable<Stack<Character>> getStackReferenceVariable()
	{
		return mStackReference;
	}

	public DoubleVariable getStackDepthVariable()
	{
		return mStackDepthVariable;
	}

	public DoubleVariable getNumberOfPhasesPerPlaneVariable()
	{
		return mNumberOfImagesPerPlaneVariable;
	}

}
