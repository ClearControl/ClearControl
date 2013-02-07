package gui.swing;

import frames.Frame;
import gui.swing.jogl.VideoCanvas;

import javax.media.opengl.GLException;

import variable.objectv.ObjectInputVariableInterface;
import variable.objectv.ObjectVariable;

public class VideoDisplay extends VideoCanvas
{

	private ObjectVariable<Frame> mObjectVariable = new ObjectVariable<Frame>();

	public VideoDisplay()
	{
		this(1, 512, 512);
	}

	public VideoDisplay(final int pVideoWidth, final int pVideoHeight)
	{
		this(1, pVideoWidth, pVideoHeight);
	}

	public VideoDisplay(final int pBytesPerPixel,
											final int pVideoWidth,
											final int pVideoHeight) throws GLException
	{
		super(pBytesPerPixel, pVideoWidth, pVideoHeight);

		mObjectVariable.sendUpdatesTo(new ObjectInputVariableInterface<Frame>()
		{

			@Override
			public void setReference(	Object pDoubleEventSource,
																Frame pNewFrameReference)
			{
				setSourceBuffer(pNewFrameReference.buffer);
				setWidth(pNewFrameReference.width);
				setHeight(pNewFrameReference.height);
				setBytesPerPixel(pNewFrameReference.bpp);
				notifyNewFrame();
				display();
			}
		}

		);
	}

	public ObjectVariable<Frame> getFrameReferenceVariable()
	{
		return mObjectVariable;
	}

	@Override
	public void setBounds(int pX, int pY, int pWidth, int pHeight)
	{
		super.setBounds(pX, pY, pWidth, pHeight);
	}

}
