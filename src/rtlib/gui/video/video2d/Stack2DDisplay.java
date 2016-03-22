package rtlib.gui.video.video2d;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.concurrent.executors.AsynchronousSchedulerServiceAccess;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.types.booleanv.BooleanVariable;
import rtlib.core.variable.types.doublev.DoubleVariable;
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

public class Stack2DDisplay<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																										NamedVirtualDevice implements
																																																			StackDisplayInterface<T, A>,
																																																			AsynchronousSchedulerServiceAccess
{
	private final VideoWindow<T> mVideoWindow;

	private final ObjectVariable<StackInterface<T, A>> mInputStackVariable;
	private ObjectVariable<StackInterface<T, A>> mOutputStackVariable;

	private volatile StackInterface<T, A> mReceivedStackCopy;

	private final BooleanVariable mDisplayOn;
	private final BooleanVariable mManualMinMaxIntensity;
	private final DoubleVariable mMinimumIntensity;
	private final DoubleVariable mMaximumIntensity;

	private final DoubleVariable mStackSliceNormalizedIndex;

	private AsynchronousProcessorBase<StackInterface<T, A>, Object> mAsynchronousDisplayUpdater;

	private final ReentrantLock mDisplayLock = new ReentrantLock();

	public Stack2DDisplay(T pType)
	{
		this("2D Video Display", pType, 512, 512, 1);
	}

	public Stack2DDisplay(T pType,
												final int pVideoWidth,
												final int pVideoHeight)
	{
		this("2D Video Display", pType, pVideoWidth, pVideoHeight, 10);
	}

	public Stack2DDisplay(final String pWindowName,
												T pType,
												final int pVideoWidth,
												final int pVideoHeight)
	{
		this(pWindowName, pType, pVideoWidth, pVideoHeight, 10);
	}

	public Stack2DDisplay(final String pWindowName,
												T pType,
												final int pWindowWidth,
												final int pWindowHeight,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		mVideoWindow = new VideoWindow<T>(pWindowName,
																			pType,
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
					mStackSliceNormalizedIndex.setValue(nx);
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
						ImageJStackDisplay.show((StackInterface<UnsignedShortType, ShortOffHeapAccess>) mReceivedStackCopy);
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

		mAsynchronousDisplayUpdater = new AsynchronousProcessorBase<StackInterface<T, A>, Object>("AsynchronousDisplayUpdater",
																																															pUpdaterQueueLength)
		{

			/**
			 * Interface method implementation
			 * 
			 * @see rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase#process(java.lang.Object)
			 */
			@Override
			public Object process(final StackInterface<T, A> pStack)
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
							final StackInterface<T, A> lStackToFree = mReceivedStackCopy;
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
						mOutputStackVariable.setReference(pStack);
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

		mInputStackVariable = new ObjectVariable<StackInterface<T, A>>(pWindowName + "StackInput")
		{

			@Override
			public StackInterface<T, A> setEventHook(	final StackInterface<T, A> pOldStack,
																								final StackInterface<T, A> pNewStack)
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
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewValue)
			{
				final boolean lDisplayOn = BooleanVariable.double2boolean(pNewValue);
				mVideoWindow.setDisplayOn(lDisplayOn);
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mManualMinMaxIntensity = new BooleanVariable(	"ManualMinMaxIntensity",
																									false)
		{
			@Override
			public Double setEventHook(	final Double pOldValue,
																	final Double pNewValue)
			{
				final boolean lManualMinMax = BooleanVariable.double2boolean(pNewValue);
				mVideoWindow.setManualMinMax(lManualMinMax);
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mMinimumIntensity = new DoubleVariable("MinimumIntensity", 0)
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

		mMaximumIntensity = new DoubleVariable("MaximumIntensity", 1)
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

		mStackSliceNormalizedIndex = new DoubleVariable("StackSliceNormalizedIndex",
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

	private void displayStack(final StackInterface<T, A> pStack,
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

	public int getCurrentStackPlaneIndex(StackInterface<T, A> pStack)
	{
		long lStackDepth = pStack.getDepth();

		int lStackZIndex = (int) (mStackSliceNormalizedIndex.getValue() * lStackDepth);
		if (lStackZIndex < 0)
			lStackZIndex = 0;
		else if (lStackZIndex >= lStackDepth)
			lStackZIndex = (int) (lStackDepth - 1);
		else if (Double.isNaN(lStackZIndex))
			lStackZIndex = (int) Math.round(lStackDepth / 2.0);
		return lStackZIndex;
	}

	@Override
	public ObjectVariable<StackInterface<T, A>> getOutputStackVariable()
	{
		return mOutputStackVariable;
	}

	@Override
	public void setOutputStackVariable(ObjectVariable<StackInterface<T, A>> pOutputStackVariable)
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

	public DoubleVariable getMinimumIntensityVariable()
	{
		return mMinimumIntensity;
	}

	public DoubleVariable getMaximumIntensityVariable()
	{
		return mMaximumIntensity;
	}

	public ObjectVariable<StackInterface<T, A>> getInputStackVariable()
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
