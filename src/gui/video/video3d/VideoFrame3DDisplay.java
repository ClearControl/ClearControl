package gui.video.video3d;

import java.io.IOException;
import java.nio.ByteBuffer;

import stack.Stack;
import variable.booleanv.BooleanVariable;
import variable.objectv.ObjectVariable;
import clearvolume.jcuda.JCudaClearVolumeRenderer;
import clearvolume.transfertf.TransfertFunctions;
import device.SignalStartableDevice;

public class VideoFrame3DDisplay extends SignalStartableDevice
{

	private final JCudaClearVolumeRenderer mJCudaClearVolumeRenderer;

	private final ObjectVariable<Stack> mObjectVariable;

	private final BooleanVariable mDisplayOn;

	public VideoFrame3DDisplay()
	{
		this("3d Video Display", 512, 512, 1);
	}

	public VideoFrame3DDisplay(	final String pWindowName,
															final int pVideoWidth,
															final int pVideoHeight,
															final int pBytesPerVoxel)
	{
		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	pWindowName,
																															pVideoWidth,
																															pVideoHeight,
																															pBytesPerVoxel);
		mJCudaClearVolumeRenderer.setTransfertFunction(TransfertFunctions.getGrayLevel());
		mJCudaClearVolumeRenderer.setupControlFrame();

		mObjectVariable = new ObjectVariable<Stack>("VideoFrame")
		{

			@Override
			public Stack setEventHook(Stack pNewVideoFrameReference)
			{
				// System.out.println(pNewFrameReference.buffer);

				final ByteBuffer lByteBuffer = pNewVideoFrameReference.getByteBuffer();
				final int lWidth = pNewVideoFrameReference.getWidth();
				final int lHeight = pNewVideoFrameReference.getHeight();
				final int lDepth = pNewVideoFrameReference.getDepth();

				mJCudaClearVolumeRenderer.setVolumeDataBuffer(lByteBuffer,
																											lWidth,
																											lHeight,
																											lDepth);
				mJCudaClearVolumeRenderer.requestDisplay();
				mJCudaClearVolumeRenderer.waitToFinishDataBufferCopy();
				
				// pNewVideoFrameReference.releaseFrame();

				return super.setEventHook(pNewVideoFrameReference);
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

	public BooleanVariable getDisplayOnVariable()
	{
		return mDisplayOn;
	}

	public ObjectVariable<Stack> getFrameReferenceVariable()
	{
		return mObjectVariable;
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
		catch (final IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean isShowing()
	{
		return mJCudaClearVolumeRenderer.isShowing();
	}
}
