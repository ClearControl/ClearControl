package gui.video.video3d.test;

import gui.video.video3d.VideoFrame3DDisplay;

import java.nio.ByteBuffer;

import org.junit.Test;

import stack.Stack;
import variable.objectv.ObjectVariable;

public class VideoFrame3DDisplayTests
{

	@Test
	public void test() throws InterruptedException
	{

		final int lResolutionX = 512;
		final int lResolutionY = 512;
		final int lResolutionZ = 512;

		final Stack lVideoFrame = new Stack(0,
																									0,
																									lResolutionX,
																									lResolutionX,
																									lResolutionZ,
																									1);
		final ByteBuffer lByteBuffer = lVideoFrame.getByteBuffer();

		final VideoFrame3DDisplay lVideoFrame3DDisplay = new VideoFrame3DDisplay();

		final ObjectVariable<Stack> lFrameReferenceVariable = lVideoFrame3DDisplay.getFrameReferenceVariable();

		lVideoFrame3DDisplay.open();

		lVideoFrame3DDisplay.start();

		for (int i = 0; i < 1000 && lVideoFrame3DDisplay.isShowing(); i++)
		{

			lByteBuffer.clear();
			for (int z = 0; z < lResolutionZ; z++)
				for (int y = 0; y < lResolutionY; y++)
					for (int x = 0; x < lResolutionX; x++)
					{
						final byte lValue = (byte) (i + x ^ y ^ z);
						lByteBuffer.put(lValue);
					}

			lFrameReferenceVariable.setReference(lVideoFrame);
			// Thread.sleep(100);
		}

		lVideoFrame3DDisplay.stop();

		lVideoFrame3DDisplay.close();

	}

}
