package gui.video.old;

import gui.video.VideoFrame;
import gui.video.jogl.old.VideoCanvas;

import javax.media.opengl.GLException;

import variable.objectv.ObjectInputVariableInterface;
import variable.objectv.ObjectVariable;

public class VideoCanvasFrameDisplay extends VideoCanvas
{

	private final ObjectVariable<VideoFrame> mObjectVariable = new ObjectVariable<VideoFrame>();

	public VideoCanvasFrameDisplay()
	{
		this(1, 512, 512);
	}

	public VideoCanvasFrameDisplay(	final int pVideoWidth,
																	final int pVideoHeight)
	{
		this(1, pVideoWidth, pVideoHeight);
	}

	public VideoCanvasFrameDisplay(	final int pBytesPerPixel,
																	final int pVideoWidth,
																	final int pVideoHeight) throws GLException
	{
		super(pBytesPerPixel, pVideoWidth, pVideoHeight);

		mObjectVariable.sendUpdatesTo(new ObjectInputVariableInterface<VideoFrame>()
		{

			@Override
			public void setReference(	final Object pDoubleEventSource,
																final VideoFrame pNewFrameReference)
			{
				// System.out.println(pNewFrameReference.buffer);
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

	public ObjectVariable<VideoFrame> getFrameReferenceVariable()
	{
		return mObjectVariable;
	}

	@Override
	public void setBounds(final int pX,
												final int pY,
												final int pWidth,
												final int pHeight)
	{
		super.setBounds(pX, pY, pWidth, pHeight);
	}

}
