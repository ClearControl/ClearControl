package clearcontrol.gui.video.video3d;

import java.util.concurrent.TimeUnit;

import clearcontrol.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import clearcontrol.core.variable.Variable;
import clearcontrol.device.VirtualDevice;
import clearcontrol.gui.video.StackDisplayInterface;
import clearcontrol.gui.video.util.WindowControl;
import clearcontrol.stack.EmptyStack;
import clearcontrol.stack.StackInterface;
import cleargl.ClearGLWindow;
import clearvolume.renderer.ClearVolumeRendererInterface;
import clearvolume.renderer.cleargl.ClearGLVolumeRenderer;
import clearvolume.renderer.factory.ClearVolumeRendererFactory;
import coremem.ContiguousMemoryInterface;
import coremem.types.NativeTypeEnum;
import coremem.util.Size;

public class Stack3DDisplay extends VirtualDevice implements
																											StackDisplayInterface
{
	private static final int cDefaultDisplayQueueLength = 2;
	protected static final long cTimeOutForBufferCopy = 5;

	private ClearVolumeRendererInterface mClearVolumeRenderer;

	private final Variable<StackInterface> mInputStackVariable;
	private Variable<StackInterface> mOutputStackVariable;

	private AsynchronousProcessorBase<StackInterface, Object> mAsynchronousDisplayUpdater;

	private volatile Variable<Boolean> mDisplayOn;
	private volatile Variable<Boolean> mWaitForLastChannel;

	public Stack3DDisplay(final String pWindowName)
	{
		this(pWindowName, 512, 512, 1, cDefaultDisplayQueueLength);
	}

	public Stack3DDisplay(final String pWindowName,
												final int pWindowWidth,
												final int pWindowHeight,
												final int pNumberOfLayers,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		NativeTypeEnum lNativeTypeEnum = NativeTypeEnum.UnsignedShort;

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
		mClearVolumeRenderer.disableClose();
		
		if(mClearVolumeRenderer instanceof ClearGLVolumeRenderer)
		{
			ClearGLVolumeRenderer lClearGLVolumeRenderer = (ClearGLVolumeRenderer)mClearVolumeRenderer;
			ClearGLWindow lClearGLWindow = lClearGLVolumeRenderer.getClearGLWindow();
			lClearGLWindow.addWindowListener(new WindowControl(lClearGLWindow));
		}

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

				if (mWaitForLastChannel.get() && ((lChannel + 1) % mClearVolumeRenderer.getNumberOfRenderLayers()) == 0)
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

		mInputStackVariable = new Variable<StackInterface>("VideoFrame")
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

		mDisplayOn = new Variable<Boolean>("DisplayOn", true)
		{
			@Override
			public void set(final Boolean pBoolean)
			{
				final boolean lDisplayOn = pBoolean;
				setDisplayOn(lDisplayOn);
			}
		};

		mWaitForLastChannel = new Variable<Boolean>("WaitForLastChannel",
																								false);

	}
	
	

	@Override
	public Variable<StackInterface> getOutputStackVariable()
	{
		return mOutputStackVariable;
	}

	@Override
	public void setOutputStackVariable(Variable<StackInterface> pOutputStackVariable)
	{
		mOutputStackVariable = pOutputStackVariable;
	}

	public Variable<Boolean> getDisplayOnVariable()
	{
		return mDisplayOn;
	}

	public Variable<StackInterface> getStackInputVariable()
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
		mAsynchronousDisplayUpdater.start();
		return false;
	}

	@Override
	public boolean close()
	{
		try
		{
			mAsynchronousDisplayUpdater.stop();
			mAsynchronousDisplayUpdater.waitToFinish(1, TimeUnit.SECONDS);
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
	
	public void setVisible(boolean pVisible)
	{
		mClearVolumeRenderer.setVisible(pVisible);
	}
	
	public void requestFocus()
	{
		if(mClearVolumeRenderer instanceof ClearGLVolumeRenderer)
		{
			ClearGLVolumeRenderer lClearGLVolumeRenderer = (ClearGLVolumeRenderer)mClearVolumeRenderer;
			ClearGLWindow lClearGLWindow = lClearGLVolumeRenderer.getClearGLWindow();
			lClearGLWindow.requestFocus();
		}
	}

	public void disableClose()
	{
		mClearVolumeRenderer.disableClose();
	}

	public Variable<Boolean> getWaitForLastChannel()
	{
		return mWaitForLastChannel;
	}

	public void setWaitForLastChannel(Variable<Boolean> pWaitForLastChannel)
	{
		mWaitForLastChannel = pWaitForLastChannel;
	}





}
