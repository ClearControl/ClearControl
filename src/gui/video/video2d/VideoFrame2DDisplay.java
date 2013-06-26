package gui.video.video2d;

import gui.video.VideoFrame;
import gui.video.video2d.jogl.VideoWindow;

import java.io.IOException;

import javax.media.opengl.GLException;

import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectVariable;
import device.SignalStartableDevice;

public class VideoFrame2DDisplay extends SignalStartableDevice
{
	private final VideoWindow mVideoWindow;

	private final ObjectVariable<VideoFrame> mObjectVariable;

	private final BooleanVariable mDisplayOn;
	private final BooleanVariable mManualMinMaxIntensity;
	private final DoubleVariable mMinimumIntensity;
	private final DoubleVariable mMaximumIntensity;

	public VideoFrame2DDisplay()
	{
		this("2D Video Display", 1, 512, 512);
	}

	public VideoFrame2DDisplay(	final int pVideoWidth,
															final int pVideoHeight)
	{
		this("2D Video Display", 1, pVideoWidth, pVideoHeight);
	}

	public VideoFrame2DDisplay(	final String pWindowName,
															final int pVideoWidth,
															final int pVideoHeight)
	{
		this(pWindowName, 1, pVideoWidth, pVideoHeight);
	}

	public VideoFrame2DDisplay(	final String pWindowName,
															final int pBytesPerPixel,
															final int pVideoWidth,
															final int pVideoHeight) throws GLException
	{

		mVideoWindow = new VideoWindow(	pBytesPerPixel,
																		pVideoWidth,
																		pVideoHeight);

		mObjectVariable = new ObjectVariable<VideoFrame>(pWindowName)
		{

			@Override
			public void setReference(final VideoFrame pNewFrameReference)
			{
				// System.out.println(pNewFrameReference.buffer);
				if (pNewFrameReference.getDimension() == 2)
				{
					mVideoWindow.setSourceBuffer(pNewFrameReference.getByteBuffer());
					mVideoWindow.setWidth(pNewFrameReference.getWidth());
					mVideoWindow.setHeight(pNewFrameReference.getHeight());
					mVideoWindow.setBytesPerPixel(pNewFrameReference.bpp);
					mVideoWindow.notifyNewFrame();

					mVideoWindow.display();
					pNewFrameReference.releaseFrame();
				}
			}
		};

		mDisplayOn = new BooleanVariable("DisplayOn", true)
		{
			@Override
			public void setValue(final double pBoolean)
			{
				final boolean lDisplayOn = BooleanVariable.double2boolean(pBoolean);
				mVideoWindow.setDisplayOn(lDisplayOn);
			}
		};

		mManualMinMaxIntensity = new BooleanVariable(	"ManualMinMaxIntensity",
																									false)
		{
			@Override
			public void setValue(final double pBoolean)
			{
				final boolean lManualMinMax = BooleanVariable.double2boolean(pBoolean);
				mVideoWindow.setManualMinMax(lManualMinMax);
			}
		};

		mMinimumIntensity = new DoubleVariable("MinimumIntensity", 0)
		{
			@Override
			public void setValue(final double pMinIntensity)
			{
				final double lMinIntensity = Math.pow(pMinIntensity, 6);
				mVideoWindow.setMinIntensity(lMinIntensity);
			}
		};

		mMaximumIntensity = new DoubleVariable("MaximumIntensity", 1)
		{
			@Override
			public void setValue(final double pMaxIntensity)
			{
				final double lMaxIntensity = Math.pow(pMaxIntensity, 6);
				mVideoWindow.setMaxIntensity(lMaxIntensity);
			}
		};
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

	public ObjectVariable<VideoFrame> getFrameReferenceVariable()
	{
		return mObjectVariable;
	}

	public void setVisible(final boolean pIsVisible)
	{
		mVideoWindow.setVisible(pIsVisible);
	}

	public void setLinearInterpolation(final boolean pLinearInterpolation)
	{
		mVideoWindow.setLinearInterpolation(pLinearInterpolation);
	}

	public void setSyncToRefresh(final boolean pSyncToRefresh)
	{
		mVideoWindow.setSyncToRefresh(pSyncToRefresh);
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

}
