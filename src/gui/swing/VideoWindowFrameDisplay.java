package gui.swing;

import frames.Frame;
import gui.swing.jogl.VideoWindow;

import javax.media.opengl.GLException;

import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectInputVariableInterface;
import variable.objectv.ObjectVariable;

public class VideoWindowFrameDisplay
{
	private final VideoWindow mVideoWindow;

	private final ObjectVariable<Frame> mObjectVariable = new ObjectVariable<Frame>();

	public BooleanVariable mDisplayOn;
	public BooleanVariable mManualMinMaxIntensity;
	public DoubleVariable mMinimumIntensity;
	public DoubleVariable mMaximumIntensity;

	public VideoWindowFrameDisplay()
	{
		this(1, 512, 512);
	}

	public VideoWindowFrameDisplay(	final int pVideoWidth,
																	final int pVideoHeight)
	{
		this(1, pVideoWidth, pVideoHeight);
	}

	public VideoWindowFrameDisplay(	final int pBytesPerPixel,
																	final int pVideoWidth,
																	final int pVideoHeight) throws GLException
	{

		mVideoWindow = new VideoWindow(	pBytesPerPixel,
																		pVideoWidth,
																		pVideoHeight);

		mObjectVariable.sendUpdatesTo(new ObjectInputVariableInterface<Frame>()
		{

			@Override
			public void setReference(	final Object pDoubleEventSource,
																final Frame pNewFrameReference)
			{
				// System.out.println(pNewFrameReference.buffer);
				mVideoWindow.setSourceBuffer(pNewFrameReference.buffer);
				mVideoWindow.setWidth(pNewFrameReference.width);
				mVideoWindow.setHeight(pNewFrameReference.height);
				mVideoWindow.setBytesPerPixel(pNewFrameReference.bpp);
				mVideoWindow.notifyNewFrame();

				mVideoWindow.display();
				pNewFrameReference.releaseFrame();
			}
		});

		mDisplayOn = new BooleanVariable(true)
		{
			@Override
			public void setValue(final double pBoolean)
			{
				final boolean lDisplayOn = BooleanVariable.double2boolean(pBoolean);
				mVideoWindow.setDisplayOn(lDisplayOn);
			}
		};

		mManualMinMaxIntensity = new BooleanVariable(false)
		{
			@Override
			public void setValue(final double pBoolean)
			{
				final boolean lManualMinMax = BooleanVariable.double2boolean(pBoolean);
				mVideoWindow.setManualMinMax(lManualMinMax);
			}
		};

		mMinimumIntensity = new DoubleVariable(0)
		{
			@Override
			public void setValue(final double pMinIntensity)
			{
				final double lMinIntensity = Math.pow(pMinIntensity, 6);
				mVideoWindow.setMinIntensity(lMinIntensity);
			}
		};

		mMaximumIntensity = new DoubleVariable(1)
		{
			@Override
			public void setValue(final double pMaxIntensity)
			{
				final double lMaxIntensity = Math.pow(pMaxIntensity, 6);
				mVideoWindow.setMaxIntensity(lMaxIntensity);
			}
		};
	}

	public ObjectVariable<Frame> getFrameReferenceVariable()
	{
		return mObjectVariable;
	}

	public void setVisible(final boolean pIsVisible)
	{
		mVideoWindow.setVisible(pIsVisible);
	}

}
