package rtlib.gui.video.video2d;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.objectv.ObjectVariable;
import rtlib.gui.video.StackDisplayInterface;
import rtlib.gui.video.video2d.videowindow.VideoWindow;
import rtlib.stack.EmptyStack;
import rtlib.stack.StackInterface;
import rtlib.stack.imglib2.ImageJStackDisplay;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

import coremem.ContiguousMemoryInterface;
import coremem.types.NativeTypeEnum;

public class Stack2DDisplay extends NamedVirtualDevice implements
																											StackDisplayInterface,
																											AsynchronousSchedulerServiceAccess
{
	private final VideoWindow mVideoWindow;

	private final ObjectVariable<StackInterface> mInputStackVariable;
	private ObjectVariable<StackInterface> mOutputStackVariable;

	private volatile StackInterface mReceivedStackCopy;

	private final BooleanVariable mDisplayOn;
	private final BooleanVariable mManualMinMaxIntensity;
	private final ObjectVariable<Double> mMinimumIntensity;
	private final ObjectVariable<Double> mMaximumIntensity;

	private final ObjectVariable<Double> mStackSliceNormalizedIndex;

	private AsynchronousProcessorBase<StackInterface, Object> mAsynchronousDisplayUpdater;

	private final ReentrantLock mDisplayLock = new ReentrantLock();

	public Stack2DDisplay()
	{
		this("2D Video Display", 512, 512, 1);
	}

	public Stack2DDisplay(final int pVideoWidth, final int pVideoHeight)
	{
		this("2D Video Display", pVideoWidth, pVideoHeight, 10);
	}

	public Stack2DDisplay(final String pWindowName,
												final int pVideoWidth,
												final int pVideoHeight)
	{
		this(pWindowName, pVideoWidth, pVideoHeight, 10);
	}

	public Stack2DDisplay(final String pWindowName,
												final int pWindowWidth,
												final int pWindowHeight,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		mVideoWindow = new VideoWindow(	pWindowName,
																		NativeTypeEnum.UnsignedByte,
																		pWindowWidth,
																		pWindowHeight);

		mVideoWindow.setVisible(true);

		final MouseAdapter lMouseAdapter = new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent pMouseEvent)
			{
				if (pMouseEvent.isAltDown() && pMouseEvent.isButtonDown(1))
				{
					final double nx = ((double) pMouseEvent.getX()) / mVideoWindow.getWindowWidth();
					mStackSliceNormalizedIndex.set(nx);
					displayStack(mReceivedStackCopy, true);
				}

				super.mouseDragged(pMouseEvent);
			}
		};

		mVideoWindow.getGLWindow().addMouseListener(lMouseAdapter);

		KeyListener lKeyAdapter = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent pE)
			{
				switch (pE.getKeyCode())
				{
				case KeyEvent.VK_I:
					try
					{
						mDisplayLock.lock();
						ImageJStackDisplay.show(mReceivedStackCopy);
						mDisplayLock.unlock();
					}
					catch (Throwable e)
					{
						e.printStackTrace();
					}

					break;
				}

			}
		};

		mVideoWindow.getGLWindow().addKeyListener(lKeyAdapter);

		mAsynchronousDisplayUpdater = new AsynchronousProcessorBase<StackInterface, Object>("AsynchronousDisplayUpdater",
																																												pUpdaterQueueLength)
		{

			/**
			 * Interface method implementation
			 * 
			 * @see rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase#process(java.lang.Object)
			 */
			@Override
			public Object process(final StackInterface pStack)
			{
				if (pStack instanceof EmptyStack)
					return null;

				try
				{

					if (mReceivedStackCopy == null || mReceivedStackCopy.getWidth() != pStack.getWidth()
							|| mReceivedStackCopy.getHeight() != pStack.getHeight()
							|| mReceivedStackCopy.getDepth() != pStack.getDepth()
							|| mReceivedStackCopy.getSizeInBytes() != pStack.getSizeInBytes())
					{
						if (mReceivedStackCopy != null)
						{
							mDisplayLock.lock();
							final StackInterface lStackToFree = mReceivedStackCopy;
							mReceivedStackCopy = pStack.allocateSameSize();
							lStackToFree.free();
							mDisplayLock.unlock();

						}
						else
							mReceivedStackCopy = pStack.allocateSameSize();
					}

					if (!mReceivedStackCopy.isFree())
					{
						mDisplayLock.lock();
						mReceivedStackCopy.getContiguousMemory()
															.copyFrom(pStack.getContiguousMemory());
						mDisplayLock.unlock();

					}

					displayStack(mReceivedStackCopy, false);

					if (mOutputStackVariable != null)
					{
						mOutputStackVariable.set(pStack);
					}
					else
						pStack.release();

				}
				catch (coremem.rgc.FreedException e)
				{
					System.err.println(this.getClass().getSimpleName() + ": Underlying ressource has been freed while processing last stack");
				}

				return null;
			}
		};

		mAsynchronousDisplayUpdater.start();

		mInputStackVariable = new ObjectVariable<StackInterface>(pWindowName + "StackInput")
		{

			@Override
			public StackInterface setEventHook(	final StackInterface pOldStack,
																					final StackInterface pNewStack)
			{
				if (!mAsynchronousDisplayUpdater.passOrFail(pNewStack))
				{
					pNewStack.release();
				}
				return super.setEventHook(pOldStack, pNewStack);
			}

		};

		mDisplayOn = new BooleanVariable("DisplayOn", true)
		{
			@Override
			public Boolean setEventHook(final Boolean pOldValue,
																	final Boolean pNewValue)
			{
				final boolean lDisplayOn = pNewValue;
				mVideoWindow.setDisplayOn(lDisplayOn);
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mManualMinMaxIntensity = new BooleanVariable(	"ManualMinMaxIntensity",
																									false)
		{
			@Override
			public Boolean setEventHook(final Boolean pOldValue,
																	final Boolean pNewValue)
			{
				final boolean lManualMinMax = pNewValue;
				mVideoWindow.setManualMinMax(lManualMinMax);
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mMinimumIntensity = new ObjectVariable<Double>(	"MinimumIntensity",
																										0.0)
		{
			@Override
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewMinIntensity)
			{
				final double lMinIntensity = Math.pow(pNewMinIntensity, 6);
				mVideoWindow.setMinIntensity(lMinIntensity);
				return super.setEventHook(pOldValue, pNewMinIntensity);
			}
		};

		mMaximumIntensity = new ObjectVariable<Double>(	"MaximumIntensity",
																										1.0)
		{
			@Override
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewMaxIntensity)
			{
				final double lMaxIntensity = Math.pow(pNewMaxIntensity, 6);
				mVideoWindow.setMaxIntensity(lMaxIntensity);
				return super.setEventHook(pOldValue, pNewMaxIntensity);
			}
		};

		mStackSliceNormalizedIndex = new ObjectVariable<Double>("StackSliceNormalizedIndex",
																														Double.NaN);

		Runnable lAutoRescaleRunnable = () -> {
			boolean lTryLock = false;
			try
			{
				lTryLock = mDisplayLock.tryLock(1, TimeUnit.MILLISECONDS);
				if (lTryLock && mReceivedStackCopy != null)
				{
					int lStackZIndex = getCurrentStackPlaneIndex(mReceivedStackCopy);
					ContiguousMemoryInterface lContiguousMemory = mReceivedStackCopy.getContiguousMemory(lStackZIndex);

					if (mVideoWindow != null)
						mVideoWindow.fastMinMaxSampling(lContiguousMemory);

				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (lTryLock)
					mDisplayLock.unlock();
			}

		};

		scheduleAtFixedRate(lAutoRescaleRunnable,
												10,
												TimeUnit.MILLISECONDS);
		/**/

	}

	private void displayStack(final StackInterface pStack,
														boolean pNonBlockingLock)
	{
		boolean lTryLock = false;
		if (pNonBlockingLock)
			try
			{
				lTryLock = mDisplayLock.tryLock(0, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
			}
		else
		{
			mDisplayLock.lock();
			lTryLock = true;
		}

		if (lTryLock)
		{
			if (pStack != null)
			{

				final int lStackWidth = (int) pStack.getWidth();
				final int lStackHeight = (int) pStack.getHeight();
				final int lStackDepth = (int) pStack.getDepth();
				if (lStackDepth > 1)
				{

					int lStackZIndex = getCurrentStackPlaneIndex(pStack);

					final ContiguousMemoryInterface lContiguousMemory = pStack.getContiguousMemory(lStackZIndex);
					mVideoWindow.sendBuffer(lContiguousMemory,
																	lStackWidth,
																	lStackHeight);
					mVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
				}
				else
				{
					final ContiguousMemoryInterface lContiguousMemory = pStack.getContiguousMemory(0);
					mVideoWindow.sendBuffer(lContiguousMemory,
																	lStackWidth,
																	lStackHeight);
					mVideoWindow.waitForBufferCopy(1, TimeUnit.SECONDS);
				}
				mVideoWindow.setWidth(lStackWidth);
				mVideoWindow.setHeight(lStackHeight);
			}

			mDisplayLock.unlock();
		}

	}

	public int getCurrentStackPlaneIndex(StackInterface pStack)
	{
		long lStackDepth = pStack.getDepth();

		int lStackZIndex = (int) (mStackSliceNormalizedIndex.get() * lStackDepth);
		if (lStackZIndex < 0)
			lStackZIndex = 0;
		else if (lStackZIndex >= lStackDepth)
			lStackZIndex = (int) (lStackDepth - 1);
		else if (Double.isNaN(lStackZIndex))
			lStackZIndex = (int) Math.round(lStackDepth / 2.0);
		return lStackZIndex;
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

	public BooleanVariable getManualMinMaxIntensityOnVariable()
	{
		return mManualMinMaxIntensity;
	}

	public ObjectVariable<Double> getMinimumIntensityVariable()
	{
		return mMinimumIntensity;
	}

	public ObjectVariable<Double> getMaximumIntensityVariable()
	{
		return mMaximumIntensity;
	}

	public ObjectVariable<StackInterface> getInputStackVariable()
	{
		return mInputStackVariable;
	}

	public void setVisible(final boolean pIsVisible)
	{
		mVideoWindow.setVisible(pIsVisible);
	}

	@Override
	public boolean open()
	{
		mDisplayOn.setValue(true);
		setVisible(true);
		mVideoWindow.start();
		return true;
	}

	@Override
	public boolean close()
	{
		setVisible(false);
		try
		{
			mVideoWindow.stop();
			mDisplayOn.setValue(false);
			mVideoWindow.close();
			return true;
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void disableClose()
	{
		mVideoWindow.disableClose();
	}

}
