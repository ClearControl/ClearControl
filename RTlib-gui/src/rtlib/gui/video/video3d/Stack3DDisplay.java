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
import clearvolume.renderer.clearcuda.JCudaClearVolumeRenderer;
import clearvolume.transfertf.TransfertFunctions;

public class Stack3DDisplay<T> extends NamedVirtualDevice	implements
																													StackDisplayInterface<T>
{
	private static final int cDefaultDisplayQueueLength = 2;
	private static final long cWaitToCopyTimeInMilliseconds = 2000;

	private final JCudaClearVolumeRenderer mJCudaClearVolumeRenderer;

	private final ObjectVariable<Stack<T>> mInputObjectVariable;
	private ObjectVariable<Stack<T>> mOutputObjectVariable;

	private AsynchronousProcessorBase<Stack<T>, Object> mAsynchronousDisplayUpdater;

	private final BooleanVariable mDisplayOn;

	public Stack3DDisplay()
	{
		this("3d Video Display", byte.class);
	}

	public Stack3DDisplay(final String pWindowName, final Class<?> pType)
	{
		this(	pWindowName,
 pType,
					cDefaultDisplayQueueLength);
	}

	public Stack3DDisplay(final String pWindowName,
												final Class<?> pType,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	pWindowName,
																															768,
																															768,
																															SizeOf.sizeOf(pType));
		mJCudaClearVolumeRenderer.setTransfertFunction(TransfertFunctions.getGrayLevel());
		mJCudaClearVolumeRenderer.setVolumeSize(1, 1, 1);

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
				final long lBytePerVoxel = mJCudaClearVolumeRenderer.getBytesPerVoxel();

				if (lWidth * lHeight * lDepth * lBytePerVoxel != lByteBuffer.capacity())
				{
					System.err.println(Stack3DDisplay.class.getSimpleName() + ": receiving wrong pointer size!");
					return null;
				}

				mJCudaClearVolumeRenderer.setVolumeDataBuffer(lByteBuffer,
																											lWidth,
																											lHeight,
																											lDepth);
				mJCudaClearVolumeRenderer.setVolumeSize(pStack.getVolumePhysicalDimension(0),
																								pStack.getVolumePhysicalDimension(1),
																								pStack.getVolumePhysicalDimension(2));
				mJCudaClearVolumeRenderer.requestDisplay();
				mJCudaClearVolumeRenderer.waitToFinishDataBufferCopy(	cWaitToCopyTimeInMilliseconds,
																															TimeUnit.MILLISECONDS);

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
		mJCudaClearVolumeRenderer.setVisible(pIsDisplayOn);
	}

	@Override
	public boolean open()
	{
		mJCudaClearVolumeRenderer.setVisible(true);
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
			mJCudaClearVolumeRenderer.close();
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
		return mJCudaClearVolumeRenderer.isShowing();
	}

	public void disableClose()
	{
		mJCudaClearVolumeRenderer.disableClose();
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
