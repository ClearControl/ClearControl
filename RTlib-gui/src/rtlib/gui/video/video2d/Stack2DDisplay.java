package rtlib.gui.video.video2d;

import java.io.IOException;

import rtlib.core.concurrent.asyncprocs.AsynchronousProcessorBase;
import rtlib.core.device.NamedVirtualDevice;
import rtlib.core.variable.booleanv.BooleanVariable;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.StackDisplayInterface;
import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;
import rtlib.stack.Stack;

import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

public class Stack2DDisplay<T> extends NamedVirtualDevice	implements
																													StackDisplayInterface<T>
{
	private final VideoWindow mVideoWindow;

	private final ObjectVariable<Stack<T>> mInputStackVariable;
	private ObjectVariable<Stack<T>> mOutputStackVariable;

	private Stack<T> mLastReceivedStack;

	private final BooleanVariable mDisplayOn;
	private final BooleanVariable mManualMinMaxIntensity;
	private final DoubleVariable mMinimumIntensity;
	private final DoubleVariable mMaximumIntensity;

	private final DoubleVariable mStackSliceNormalizedIndex;

	private AsynchronousProcessorBase<Stack<T>, Object> mAsynchronousDisplayUpdater;

	private Object mReleaseLock = new Object();

	public Stack2DDisplay(Class<T> pType)
	{
		this("2D Video Display", pType, 512, 512, 1);
	}

	public Stack2DDisplay(Class<T> pType,
												final int pVideoWidth,
												final int pVideoHeight)
	{
		this("2D Video Display", pType, pVideoWidth, pVideoHeight, 10);
	}

	public Stack2DDisplay(final String pWindowName,
												Class<T> pType,
												final int pVideoWidth,
												final int pVideoHeight)
	{
		this(pWindowName, pType, pVideoWidth, pVideoHeight, 10);
	}

	public Stack2DDisplay(final String pWindowName,
												Class<T> pType,
												final int pVideoWidth,
												final int pVideoHeight,
												final int pUpdaterQueueLength)
	{
		super(pWindowName);

		mVideoWindow = new VideoWindow(	pWindowName,
																		pType,
																		pVideoWidth,
																		pVideoHeight);

		MouseAdapter lMouseAdapter = new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent pMouseEvent)
			{
				if (pMouseEvent.isAltDown() && pMouseEvent.isButtonDown(1))
				{
					final double nx = ((double) pMouseEvent.getX()) / mVideoWindow.getWindowWidth();
					mStackSliceNormalizedIndex.setValue(nx);
					// TODO: could be asynchronous for performance
					displayStack(mLastReceivedStack, false);
				}

				super.mouseDragged(pMouseEvent);
			}
		};

		mVideoWindow.getGLWindow().addMouseListener(lMouseAdapter);

		mAsynchronousDisplayUpdater = new AsynchronousProcessorBase<Stack<T>, Object>("AsynchronousDisplayUpdater",
																																									pUpdaterQueueLength)
		{
			@Override
			public Object process(final Stack<T> pStack)
			{
				displayStack(pStack, true);
				return null;
			}
		};

		mAsynchronousDisplayUpdater.start();

		mInputStackVariable = new ObjectVariable<Stack<T>>(pWindowName + "StackInput")
		{

			@Override
			public Stack<T> setEventHook(	final Stack<T> pOldStack,
																		final Stack<T> pNewStack)
			{
				if (!mAsynchronousDisplayUpdater.passOrFail(pNewStack))
				{
					pNewStack.releaseStack();
				}
				return super.setEventHook(pOldStack, pNewStack);
			}

		};

		mDisplayOn = new BooleanVariable("DisplayOn", true)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
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
			public double setEventHook(	final double pOldValue,
																	final double pNewValue)
			{
				final boolean lManualMinMax = BooleanVariable.double2boolean(pNewValue);
				mVideoWindow.setManualMinMax(lManualMinMax);
				return super.setEventHook(pOldValue, pNewValue);
			}
		};

		mMinimumIntensity = new DoubleVariable("MinimumIntensity", 0)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewMinIntensity)
			{
				final double lMinIntensity = Math.pow(pNewMinIntensity, 6);
				mVideoWindow.setMinIntensity(lMinIntensity);
				return super.setEventHook(pOldValue, pNewMinIntensity);
			}
		};

		mMaximumIntensity = new DoubleVariable("MaximumIntensity", 1)
		{
			@Override
			public double setEventHook(	final double pOldValue,
																	final double pNewMaxIntensity)
			{
				final double lMaxIntensity = Math.pow(pNewMaxIntensity, 6);
				mVideoWindow.setMaxIntensity(lMaxIntensity);
				return super.setEventHook(pOldValue, pNewMaxIntensity);
			}
		};

		mStackSliceNormalizedIndex = new DoubleVariable("StackSliceNormalizedIndex",
																										Double.NaN);
	}

	private void displayStack(final Stack<T> pStack,
														boolean pReleaseLastReceivedStack)
	{

		NDArrayTypedDirect<T> lNDArray = pStack.getNDArray();
		long lStackWidth = lNDArray.getWidth();
		long lStackHeight = lNDArray.getHeight();
		long lStackDepth = lNDArray.getDepth();
		if (lStackDepth > 1)
		{

			long lStackZIndex = (long) (mStackSliceNormalizedIndex.getValue() * lStackDepth);
			if (lStackZIndex < 0)
				lStackZIndex = 0;
			else if (lStackZIndex >= lStackDepth)
				lStackZIndex = lStackDepth - 1;
			else if (Double.isNaN(lStackZIndex))
				lStackZIndex = Math.round(lStackDepth / 2.0);

			mVideoWindow.setSourceBuffer(lNDArray.sliceMajorAxis(lStackZIndex));
		}
		else
		{
			mVideoWindow.setSourceBuffer(lNDArray);
		}
		mVideoWindow.setWidth((int) lStackWidth);
		mVideoWindow.setHeight((int) lStackHeight);

		mVideoWindow.notifyNewFrame();
		mVideoWindow.requestDisplay();

		// synchronized (mReleaseLock)
		{
			if (getOutputStackVariable() != null)
			{
				boolean lIsLastReceivedStack = mLastReceivedStack == pStack;
				if (!lIsLastReceivedStack)
				{
					mLastReceivedStack = pStack; // TODO: this is dangerous, this stack
																				// could be released!
					getOutputStackVariable().setReference(pStack);
				}
			}
			else
			{
				if (mLastReceivedStack != null && pReleaseLastReceivedStack
						&& !mLastReceivedStack.isReleased())
					mLastReceivedStack.releaseStack();
				mLastReceivedStack = pStack;
			}
		}
	}

	@Override
	public ObjectVariable<Stack<T>> getOutputStackVariable()
	{
		return mOutputStackVariable;
	}

	@Override
	public void setOutputStackVariable(ObjectVariable<Stack<T>> pOutputStackVariable)
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

	public ObjectVariable<Stack<T>> getFrameReferenceVariable()
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
		setVisible(true);
		return true;
	}

	@Override
	public boolean close()
	{
		setVisible(false);
		try
		{
			mVideoWindow.close();
			return true;
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean start()
	{

		mDisplayOn.setValue(true);
		return true;
	}

	@Override
	public boolean stop()
	{

		mDisplayOn.setValue(false);
		return true;
	}

	public void disableClose()
	{
		mVideoWindow.disableClose();
	}

}
