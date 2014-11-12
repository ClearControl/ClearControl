package rtlib.gui.video.video3d;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.memory.SizeOf;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.StackDisplayInterface;
import rtlib.stack.Stack;
import clearvolume.renderer.ClearVolumeRendererInterface;
import clearvolume.renderer.factory.ClearVolumeRendererFactory;
import clearvolume.transferf.TransferFunctions;
import clearvolume.volume.VolumeManager;
import clearvolume.volume.sink.NullVolumeSink;
import clearvolume.volume.sink.VolumeSinkInterface;
import clearvolume.volume.sink.filter.ChannelFilterSink;
import clearvolume.volume.sink.filter.gui.ChannelFilterSinkJFrame;
import clearvolume.volume.sink.renderer.ClearVolumeRendererSink;
import clearvolume.volume.sink.timeshift.TimeShiftingSink;
import clearvolume.volume.sink.timeshift.gui.TimeShiftingSinkJFrame;

public class Stack3DDisplay<T> extends NamedVirtualDevice	implements
																													StackDisplayInterface<T>
{
	private static final int cDefaultDisplayQueueLength = 2;
	private static final long cWaitToCopyTimeInMilliseconds = 2000;
	private static final long cTimeShiftSoftHoryzon = 20;
	private static final long cTimeShiftHardHoryzon = 60;
	private static ClearVolumeRendererInterface mClearVolumeRenderer;
	private static VolumeManager mVolumeManager;

	private final ObjectVariable<Stack<T>> mInputObjectVariable;
	private ObjectVariable<Stack<T>> mOutputObjectVariable;

	private AsynchronousProcessorBase<Stack<T>, Object> mAsynchronousDisplayUpdater;

	private final BooleanVariable mDisplayOn;
	private TimeShiftingSinkJFrame mTimeShiftingSinkJFrame;
	private ChannelFilterSinkJFrame mChannelFilterSinkJFrame;
	private ChannelFilterSink mChannelFilterSink;
	private TimeShiftingSink mTimeShiftingSink;
	private CopyVolumeSink mVolumeSinkInterface;

	public Stack3DDisplay()
	{
		this("3d Video Display", byte.class);
	}

	public Stack3DDisplay(final String pWindowName, final Class<?> pType)
	{
		this(pWindowName, pType, cDefaultDisplayQueueLength);
	}

	public Stack3DDisplay(final String pWindowName,
												final Class<?> pType,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		/*
		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	pWindowName,
																															768,
																															768,
																															SizeOf.sizeOf(pType));
		mJCudaClearVolumeRenderer.setTransfertFunction(TransferFunctions.getCoolWarm());
		mJCudaClearVolumeRenderer.setVolumeSize(1, 1, 1);/**/

		VolumeSinkInterface lVolumeSink = createRenderer(	pUpdaterQueueLength,
																											pWindowName,
																											768,
																											768,
																											SizeOf.sizeOf(pType),
																											768,
																											768,
																											false,
																											false);

		mVolumeSinkInterface = new CopyVolumeSink(lVolumeSink, pType);

		mAsynchronousDisplayUpdater = new AsynchronousProcessorBase<Stack<T>, Object>("AsynchronousDisplayUpdater-" + pWindowName,
																																									pUpdaterQueueLength)
		{
			@Override
			public Object process(final Stack<T> pStack)
			{
				// System.out.println(pNewFrameReference.buffer);

				final ByteBuffer lByteBuffer = pStack.getNDArray()
																							.getRAM()
																							.passNativePointerToByteBuffer(Character.class);
				final long lWidth = pStack.getWidth();
				final long lHeight = pStack.getHeight();
				final long lDepth = pStack.getDepth();
				final long lBytePerVoxel = mClearVolumeRenderer.getBytesPerVoxel();

				if (lWidth * lHeight * lDepth * lBytePerVoxel != lByteBuffer.capacity())
				{
					System.err.println(Stack3DDisplay.class.getSimpleName() + ": receiving wrong pointer size!");
					return null;
				}

				System.out.format("%g %g %g \n",
													pStack.getVoxelSizeInRealUnits(0),
													pStack.getVoxelSizeInRealUnits(1),
													pStack.getVoxelSizeInRealUnits(2));

				mVolumeSinkInterface.sendVolume(pStack);

				/*
				mClearVolumeRenderer.setVolumeDataBuffer(	lByteBuffer,
																									lWidth,
																									lHeight,
																									lDepth,
																									pStack.getVoxelSizeInRealUnits(0),
																									pStack.getVoxelSizeInRealUnits(1),
																									pStack.getVoxelSizeInRealUnits(2));

				mClearVolumeRenderer.requestDisplay();
				mClearVolumeRenderer.waitToFinishDataBufferCopy(cWaitToCopyTimeInMilliseconds,
																												TimeUnit.MILLISECONDS);/**/

				if (mOutputObjectVariable != null)
					mOutputObjectVariable.set(pStack);
				else if (!pStack.isReleased())
					pStack.releaseStack();

				return null;
			}
		};

		mAsynchronousDisplayUpdater.start();

		mInputObjectVariable = new ObjectVariable<Stack<T>>("VideoFrame")
		{

			@Override
			public Stack<T> setEventHook(	final Stack<T> pOldStack,
																		final Stack<T> pNewStack)
			{
				if (!mAsynchronousDisplayUpdater.passOrFail(pNewStack))
					if (!pNewStack.isReleased())
					{
						pNewStack.releaseStack();
					}
				return super.setEventHook(pOldStack, pNewStack);
			}

		};

		mDisplayOn = new BooleanVariable("DisplayOn", true)
		{
			@Override
			public void setValue(final double pBoolean)
			{
				final boolean lDisplayOn = BooleanVariable.double2boolean(pBoolean);
				setDisplayOn(lDisplayOn);
			}
		};

	}

	public VolumeSinkInterface createRenderer(final int pMaxQueueLength,
																						final String pWindowName,
																						final int pWindowWidth,
																						final int pWindowHeight,
																						final int pBytesPerVoxel,
																						final int pMaxTextureWidth,
																						final int pMaxTextureHeight,
																						final boolean pTimeShift,
																						final boolean pChannelSelector)
	{
		mClearVolumeRenderer = ClearVolumeRendererFactory.newBestRenderer(pWindowName,
																																			pWindowWidth,
																																			pWindowHeight,
																																			pBytesPerVoxel,
																																			pMaxTextureWidth,
																																			pMaxTextureHeight);
		mClearVolumeRenderer.setTransfertFunction(TransferFunctions.getGrayLevel());

		mVolumeManager = mClearVolumeRenderer.createCompatibleVolumeManager(pMaxQueueLength);

		mClearVolumeRenderer.setVisible(true);

		ClearVolumeRendererSink lClearVolumeRendererSink = new ClearVolumeRendererSink(	mClearVolumeRenderer,
																																										mVolumeManager,
																																										1,
																																										TimeUnit.MILLISECONDS);
		VolumeSinkInterface lFirstSink = lClearVolumeRendererSink;

		mTimeShiftingSink = null;
		mTimeShiftingSinkJFrame = null;
		if (pTimeShift)
		{
			mTimeShiftingSink = new TimeShiftingSink(	cTimeShiftSoftHoryzon,
																								cTimeShiftHardHoryzon);

			mTimeShiftingSinkJFrame = new TimeShiftingSinkJFrame(mTimeShiftingSink);
			mTimeShiftingSinkJFrame.setVisible(true);

			mTimeShiftingSink.setRelaySink(lFirstSink);

			lClearVolumeRendererSink.setRelaySink(new NullVolumeSink());

			lFirstSink = mTimeShiftingSink;
		}

		mChannelFilterSink = null;
		mChannelFilterSinkJFrame = null;
		if (pChannelSelector)
		{
			mChannelFilterSink = new ChannelFilterSink(new NullVolumeSink());

			mChannelFilterSinkJFrame = new ChannelFilterSinkJFrame(mChannelFilterSink);
			mChannelFilterSinkJFrame.setVisible(true);

			mChannelFilterSink.setRelaySink(lFirstSink);

			lFirstSink = mChannelFilterSink;
		}

		return lFirstSink;

	}

	@Override
	public ObjectVariable<Stack<T>> getOutputStackVariable()
	{
		return mOutputObjectVariable;
	}

	@Override
	public void setOutputStackVariable(ObjectVariable<Stack<T>> pOutputStackVariable)
	{
		mOutputObjectVariable = pOutputStackVariable;
	}

	public BooleanVariable getDisplayOnVariable()
	{
		return mDisplayOn;
	}

	public ObjectVariable<Stack<T>> getStackReferenceVariable()
	{
		return mInputObjectVariable;
	}

	public void setDisplayOn(final boolean pIsDisplayOn)
	{
		mClearVolumeRenderer.setVisible(pIsDisplayOn);
	}

	@Override
	public boolean open()
	{
		mClearVolumeRenderer.setVisible(true);
		return false;
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
		try
		{
			mChannelFilterSinkJFrame.dispose();
			mTimeShiftingSinkJFrame.dispose();
			mChannelFilterSink.close();
			mTimeShiftingSink.close();
			mClearVolumeRenderer.close();
			return true;
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean isShowing()
	{
		return mClearVolumeRenderer.isShowing();
	}

	public void disableClose()
	{
		mClearVolumeRenderer.disableClose();
	}

	public ObjectVariable<Stack<T>> getOutputObjectVariable()
	{
		return mOutputObjectVariable;
	}

	public void setOutputObjectVariable(ObjectVariable<Stack<T>> pOutputObjectVariable)
	{
		mOutputObjectVariable = pOutputObjectVariable;
	}

}
