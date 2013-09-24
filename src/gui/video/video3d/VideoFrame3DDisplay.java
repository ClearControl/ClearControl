package gui.video.video3d;

import java.io.IOException;
import java.nio.ByteBuffer;

import stack.Stack;
import variable.booleanv.BooleanVariable;
import variable.objectv.ObjectVariable;
import asyncprocs.AsynchronousProcessorBase;
import clearvolume.jcuda.JCudaClearVolumeRenderer;
import clearvolume.transfertf.TransfertFunctions;
import device.NamedDevice;

public class VideoFrame3DDisplay extends NamedDevice
{

	private final JCudaClearVolumeRenderer mJCudaClearVolumeRenderer;

	private final ObjectVariable<Stack> mObjectVariable;
	
	private AsynchronousProcessorBase<Stack, Object> mAsynchronousDisplayUpdater;

	private final BooleanVariable mDisplayOn;



	public VideoFrame3DDisplay()
	{
		this("3d Video Display", 1);
	}
	
	public VideoFrame3DDisplay(	final String pWindowName,
															final int pBytesPerVoxel)
	{
		this(pWindowName,pBytesPerVoxel,2);
	}

	public VideoFrame3DDisplay(	final String pWindowName,
															final int pBytesPerVoxel,
															final int pUpdaterQueueLength)
	{
		super(pWindowName);

		mJCudaClearVolumeRenderer = new JCudaClearVolumeRenderer(	pWindowName,
																															768,
																															768,
																															pBytesPerVoxel);
		mJCudaClearVolumeRenderer.setTransfertFunction(TransfertFunctions.getGrayLevel());
		mJCudaClearVolumeRenderer.setVolumeSize(1, 1, 1);

		mAsynchronousDisplayUpdater = new AsynchronousProcessorBase<Stack, Object>(	"AsynchronousDisplayUpdater-"+pWindowName,
																																								pUpdaterQueueLength)
		{
			@Override
			public Object process(Stack pStack)
			{
				// System.out.println(pNewFrameReference.buffer);

				final ByteBuffer lByteBuffer = pStack.getByteBuffer();
				final int lWidth = pStack.getWidth();
				final int lHeight = pStack.getHeight();
				final int lDepth = pStack.getDepth();

				mJCudaClearVolumeRenderer.setVolumeDataBuffer(lByteBuffer,
																											lWidth,
																											lHeight,
																											lDepth);
				mJCudaClearVolumeRenderer.setVolumeSize(pStack.mVolumeSize[0],
				                                        pStack.mVolumeSize[1],
				                                        pStack.mVolumeSize[2]);
				mJCudaClearVolumeRenderer.requestDisplay();
				mJCudaClearVolumeRenderer.waitToFinishDataBufferCopy();

				pStack.releaseFrame();

				return null;
			}
		};

		mAsynchronousDisplayUpdater.start();

		mObjectVariable = new ObjectVariable<Stack>("VideoFrame")
		{

			@Override
			public Stack setEventHook(Stack pStack)
			{
				if(!mAsynchronousDisplayUpdater.passOrFail(pStack))
				{
					pStack.releaseFrame();
				}
				return super.setEventHook(pStack);
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

	public ObjectVariable<Stack> getStackReferenceVariable()
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

	public void disableClose()
	{
		mJCudaClearVolumeRenderer.disableClose();
	}
}
