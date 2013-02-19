package gui.swing;

import frames.Frame;
import gui.swing.jogl.VideoCanvas;
import gui.swing.jogl.VideoWindow;

import javax.media.opengl.GLException;

import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectInputVariableInterface;
import variable.objectv.ObjectVariable;

public class VideoWindowFrameDisplay
{
	private VideoWindow mVideoWindow;

	private ObjectVariable<Frame> mObjectVariable = new ObjectVariable<Frame>();

	public BooleanVariable mManualMinMaxIntensity = new BooleanVariable(false);
	public DoubleVariable mMinimumIntensity = new DoubleVariable(0);
	public DoubleVariable mMaximumIntensity = new DoubleVariable(1);

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
			public void setReference(	Object pDoubleEventSource,
																Frame pNewFrameReference)
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
		
		
		mManualMinMaxIntensity.sendUpdatesTo(new DoubleInputVariableInterface()
		{
			@Override
			public void setValue(Object pDoubleEventSource, double pBoolean)
			{
				final boolean lManualMinMax = BooleanVariable.double2boolean(pBoolean);
				mVideoWindow.setManualMinMax(lManualMinMax);
			}
		});

		mMinimumIntensity.sendUpdatesTo(new DoubleInputVariableInterface()
		{
			@Override
			public void setValue(Object pDoubleEventSource, double pMinIntensity)
			{
				final double lMinIntensity = Math.pow(pMinIntensity,6);
				mVideoWindow.setMinIntensity(lMinIntensity);
			}
		});
		
		mMaximumIntensity.sendUpdatesTo(new DoubleInputVariableInterface()
		{
			@Override
			public void setValue(Object pDoubleEventSource, double pMaxIntensity)
			{
				final double lMaxIntensity = Math.pow(pMaxIntensity,6);
				mVideoWindow.setMaxIntensity(lMaxIntensity);
			}
		});
	}

	public ObjectVariable<Frame> getFrameReferenceVariable()
	{
		return mObjectVariable;
	}

	public void setVisible(boolean pIsVisible)
	{
		mVideoWindow.setVisible(pIsVisible);
	}

}
