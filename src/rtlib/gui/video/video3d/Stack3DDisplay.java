package rtlib.gui.video.video3d;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.gui.video.StackDisplayInterface;
import rtlib.stack.EmptyStack;
import rtlib.stack.StackInterface;
import clearvolume.renderer.ClearVolumeRendererInterface;
import clearvolume.renderer.factory.ClearVolumeRendererFactory;
import coremem.ContiguousMemoryInterface;
import coremem.types.NativeTypeEnum;
import coremem.util.Size;

public class Stack3DDisplay<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																										NamedVirtualDevice implements
																																																			StackDisplayInterface
{
	private static final int cDefaultDisplayQueueLength = 2;
	protected static final long cTimeOutForBufferCopy = 5;

	private ClearVolumeRendererInterface mClearVolumeRenderer;

	private final ObjectVariable<StackInterface> mInputStackVariable;
	private ObjectVariable<StackInterface> mOutputStackVariable;

	private AsynchronousProcessorBase<StackInterface, Object> mAsynchronousDisplayUpdater;

	private volatile BooleanVariable mDisplayOn;
	private volatile BooleanVariable mWaitForLastChannel;

	public Stack3DDisplay(final String pWindowName, final T pType)
	{
		this(pWindowName, pType, 512, 512, 1, cDefaultDisplayQueueLength);
	}

	public Stack3DDisplay(final String pWindowName,
												final T pType,
												final int pWindowWidth,
												final int pWindowHeight,
												final int pNumberOfLayers,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		/*
		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	pWindowName,
																															768,
																															768,
																															Size.sizeOf(pType));
		mJCudaClearVolumeRenderer.setTransfertFunction(TransferFunctions.getCoolWarm());
		mJCudaClearVolumeRenderer.setVolumeSize(1, 1, 1);/**/

		NativeTypeEnum lNativeTypeEnum = NativeTypeEnum.UnsignedByte;
		if (pType instanceof UnsignedByteType)
			lNativeTypeEnum = NativeTypeEnum.UnsignedByte;
		else if (pType instanceof UnsignedShortType)
			lNativeTypeEnum = NativeTypeEnum.UnsignedShort;

		mClearVolumeRenderer = ClearVolumeRendererFactory.newBestRenderer(pWindowName,
																																			pWindowWidth,
																																			pWindowHeight,
																																			lNativeTypeEnum,
																																			2048,
																																			2048,
																																			pNumberOfLayers,
																																			false);

		mClearVolumeRenderer.setVisible(true);
		mClearVolumeRenderer.setAdaptiveLODActive(false);

		mAsynchronousDisplayUpdater = new AsynchronousProcessorBase<StackInterface, Object>("AsynchronousDisplayUpdater-" + pWindowName,
																																												pUpdaterQueueLength)
		{
			@Override
			public Object process(final StackInterface pStack)
			{
				if (pStack instanceof EmptyStack)
					return null;
				// System.out.println(pStack);

				final long lSizeInBytes = pStack.getSizeInBytes();
				final long lWidth = pStack.getWidth();
				final long lHeight = pStack.getHeight();
				final long lDepth = pStack.getDepth();
				final NativeTypeEnum lNativeTypeEnum = mClearVolumeRenderer.getNativeType();
				final int lBytesPerVoxel = Size.of(lNativeTypeEnum);
				final int lChannel = pStack.getChannel() % pNumberOfLayers;

				if (lWidth * lHeight * lDepth * lBytesPerVoxel != lSizeInBytes)
				{
					System.err.println(Stack3DDisplay.class.getSimpleName() + ": receiving wrong pointer size!");
					return null;
				}

				final ContiguousMemoryInterface lContiguousMemory = pStack.getContiguousMemory();

				if (lContiguousMemory.isFree())
				{
					System.err.println(Stack3DDisplay.class.getSimpleName() + ": buffer released!");
					return null;
				}

				mClearVolumeRenderer.setVolumeDataBuffer(	lChannel,
																									lContiguousMemory,
																									lWidth,
																									lHeight,
																									lDepth,
																									1,
																									1,
																									5);

				// FIXME
				/*
				pStack.getVoxelSizeInRealUnits(0),
				pStack.getVoxelSizeInRealUnits(1),
				pStack.getVoxelSizeInRealUnits(2)); /**/

				if (mWaitForLastChannel.getBooleanValue() && ((lChannel + 1) % mClearVolumeRenderer.getNumberOfRenderLayers()) == 0)
				{
					mClearVolumeRenderer.waitToFinishAllDataBufferCopy(	cTimeOutForBufferCopy,
																															TimeUnit.SECONDS);/**/
				}
				else
					mClearVolumeRenderer.waitToFinishDataBufferCopy(lChannel,
																													cTimeOutForBufferCopy,
																													TimeUnit.SECONDS);/**/

				if (mOutputStackVariable != null)
					mOutputStackVariable.set(pStack);
				else if (!pStack.isReleased())
					pStack.release();

				return null;
			}
		};

		mInputStackVariable = new ObjectVariable<StackInterface>("VideoFrame")
		{

			@Override
			public StackInterface setEventHook(	final StackInterface pOldStack,
																					final StackInterface pNewStack)
			{
				if (!mAsynchronousDisplayUpdater.passOrFail(pNewStack))
					pNewStack.release();

				return super.setEventHook(pOldStack, pNewStack);
			}

		};

		mDisplayOn = new BooleanVariable("DisplayOn", true)
		{
			@Override
			public void setValue(final boolean pBoolean)
			{
				final boolean lDisplayOn = pBoolean;
				setDisplayOn(lDisplayOn);
			}
		};

		mWaitForLastChannel = new BooleanVariable("WaitForLastChannel",
																							false);

	}

	@Override
	public ObjectVariable<StackInterface> getOutputStackVariable()
	{
		return mOutputStackVariable;
	}

	@Override
	public void setOutputStackVariable(ObjectVariable<StackInterface> pOutputStackVariable)
	{
		mOutputStackVariable = pOutputStackVariable;
	}

	public BooleanVariable getDisplayOnVariable()
	{
		return mDisplayOn;
	}

	public ObjectVariable<StackInterface> getStackInputVariable()
	{
		return mInputStackVariable;
	}

	private void setDisplayOn(final boolean pIsDisplayOn)
	{
		mClearVolumeRenderer.setVisible(pIsDisplayOn);
	}

	@Override
	public boolean open()
	{
		mClearVolumeRenderer.setVisible(true);
		mAsynchronousDisplayUpdater.start();
		return false;
	}

	@Override
	public boolean close()
	{
		try
		{

			mAsynchronousDisplayUpdater.stop();
			mAsynchronousDisplayUpdater.close();
			mClearVolumeRenderer.waitToFinishAllDataBufferCopy(	1,
																													TimeUnit.SECONDS);
			if (mClearVolumeRenderer != null)
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

	public BooleanVariable getWaitForLastChannel()
	{
		return mWaitForLastChannel;
	}

	public void setWaitForLastChannel(BooleanVariable pWaitForLastChannel)
	{
		mWaitForLastChannel = pWaitForLastChannel;
	}

}
