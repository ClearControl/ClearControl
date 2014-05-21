package rtlib.cameras.hamamatsu.orcaflash4.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import rtlib.cameras.hamamatsu.orcaflash4.OrcaFlash4StackCamera;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorInterface;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorPool;
import rtlib.core.concurrent.asyncprocs.ProcessorInterface;
import rtlib.core.device.SignalStartableDevice;
import rtlib.core.device.VirtualDeviceInterface;
import rtlib.core.recycling.Recycler;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.core.variable.objectv.SingleUpdateTargetObjectVariable;
import rtlib.stack.Stack;
import rtlib.stack.processor.StackProcessing;
import rtlib.stack.processor.StackProcessorInterface;
import dcamj.DcamFrame;

public class DcamJToVideoFrameConverterAndProcessing extends
																										SignalStartableDevice	implements
																																					VirtualDeviceInterface,
																																					StackProcessing
{

	private final ObjectVariable<DcamFrame> mDcamFrameReference;

	private AsynchronousProcessorInterface<DcamFrame, Stack> mAsynchronousConversionProcessor;

	private AsynchronousProcessorBase<Stack, Object> mSendToVariableAsynchronousProcessor;

	private final Recycler<Stack, Long> mVideoFrameRecycler = new Recycler<Stack, Long>(Stack.class);

	private final SingleUpdateTargetObjectVariable<Stack> mStackReference = new SingleUpdateTargetObjectVariable<Stack>("Stack");

	private final DoubleVariable mStackDepthVariable = new DoubleVariable("StackDepth",
																																				1);

	private final DoubleVariable mNumberOfImagesPerPlaneVariable = new DoubleVariable("NumberOfPhases",
																																										1);

	private final ArrayList<StackProcessorInterface> mStackProcessorList = new ArrayList<StackProcessorInterface>();

	public DcamJToVideoFrameConverterAndProcessing(	final ObjectVariable<DcamFrame> pDcamFrameReference,
																									final int pMaxQueueSize)
	{
		super("DcamJToVideoFrameConverter");

		mDcamFrameReference = pDcamFrameReference;

		mDcamFrameReference.sendUpdatesTo(new ObjectVariable<DcamFrame>("DcamFrame")
		{
			@Override
			public DcamFrame setEventHook(final DcamFrame pNewDcamFrame)
			{
				System.out.println("mAsynchronousConversionProcessor.passOrWait(pNewDcamFrame);");
				mAsynchronousConversionProcessor.passOrWait(pNewDcamFrame);
				return super.setEventHook(pNewDcamFrame);
			}
		});

		final ProcessorInterface<DcamFrame, Stack> lProcessor = new ProcessorInterface<DcamFrame, Stack>()
		{
			@Override
			public Stack process(final DcamFrame pInput)
			{
				final Stack lStack = convert(pInput);
				// System.out.println("mAsynchronousConversionProcessor=" +
				// mAsynchronousConversionProcessor.getRemainingCapacity());
				return lStack;
			}

			@Override
			public void close() throws IOException
			{

			}
		};

		mAsynchronousConversionProcessor = new AsynchronousProcessorPool<DcamFrame, Stack>(	"DcamJToVideoFrameConverter",
																																												pMaxQueueSize,
																																												lProcessor);

		mSendToVariableAsynchronousProcessor = new AsynchronousProcessorBase<Stack, Object>("SendToVariableAsynchronousProcessor",
																																												OrcaFlash4StackCamera.cStackProcessorQueueSize)
		{
			@Override
			public Object process(final Stack pStack)
			{
				mStackReference.setReference(pStack);
				// System.out.println("mSendToVariableAsynchronousProcessor=" +
				// mSendToVariableAsynchronousProcessor.getRemainingCapacity());
				return null;
			}
		};

		mAsynchronousConversionProcessor.connectToReceiver(mSendToVariableAsynchronousProcessor);

	}

	protected Stack convert(final DcamFrame pDcamFrame)
	{
		final int lNumberOfImagesPerPlane = (int) mNumberOfImagesPerPlaneVariable.getValue();

		Stack lStack = mVideoFrameRecycler.requestOrWaitRecyclableObject(	1,
																																			TimeUnit.SECONDS,
																																			pDcamFrame.getPixelSizeInBytes(),
																																			pDcamFrame.getWidth(),
																																			pDcamFrame.getHeight(),
																																			(long) mStackDepthVariable.getValue());
		lStack.setStackIndex(pDcamFrame.getIndex());
		lStack.setTimeStampInNanoseconds(pDcamFrame.getFrameTimeStampInNs());
		lStack.setNumberOfImagesPerPlane(lNumberOfImagesPerPlane);

		final Pointer<Byte> lVideoFramePointer = lStack.getPointer();

		final boolean lCopySucceeded = pDcamFrame.copyAllPlanesToSinglePointer(	lVideoFramePointer,
																																						(long) (mStackDepthVariable.getValue() * mNumberOfImagesPerPlaneVariable.getValue()));

		for (final StackProcessorInterface lStackProcessor : mStackProcessorList)
		{
			if (lStackProcessor.isActive())
			{

				lStack = lStackProcessor.process(lStack, mVideoFrameRecycler);
			}
		}

		pDcamFrame.release();
		if (lCopySucceeded)
		{
			return lStack;
		}

		return null;
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

	public SingleUpdateTargetObjectVariable<Stack> getStackReferenceVariable()
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

	@Override
	public void addStackProcessor(final StackProcessorInterface pStackProcessor)
	{
		mStackProcessorList.add(pStackProcessor);
	}

	@Override
	public void removeStackProcessor(StackProcessorInterface pStackProcessor)
	{
		mStackProcessorList.remove(pStackProcessor);
	}

}
