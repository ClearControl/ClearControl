package rtlib.gui.video.video3d.test;

import java.nio.ByteBuffer;

import org.junit.Test;

import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.gui.video.video3d.Stack3DDisplay;
import rtlib.stack.Stack;

public class VideoFrame3DDisplayTests
{

	@Test
	public void test() throws InterruptedException
	{

		final int lResolutionX = 512;
		final int lResolutionY = 512;
		final int lResolutionZ = 512;

		final Stack<Byte> lVideoFrame = new Stack<Byte>(0,
																											0,
																											byte.class,
																											lResolutionX,
																											lResolutionX,
																											lResolutionZ);
		final ByteBuffer lByteBuffer = lVideoFrame.getNDArray()
																							.getRAM()
																							.passNativePointerToByteBuffer();

		final Stack3DDisplay<Byte> lVideoFrame3DDisplay = new Stack3DDisplay<Byte>();

		final ObjectVariable<Stack<Byte>> lFrameReferenceVariable = lVideoFrame3DDisplay.getStackReferenceVariable();

		lVideoFrame3DDisplay.open();

		lVideoFrame3DDisplay.start();

		for (int i = 0; i < 1000 && lVideoFrame3DDisplay.isShowing(); i++)
		{

			lByteBuffer.clear();
			for (int z = 0; z < lResolutionZ; z++)
			{
				for (int y = 0; y < lResolutionY; y++)
				{
					for (int x = 0; x < lResolutionX; x++)
					{
						final byte lValue = (byte) (i + x ^ y ^ z);
						lByteBuffer.put(lValue);
					}
				}
			}

			lFrameReferenceVariable.setReference(lVideoFrame);
			// Thread.sleep(100);
		}

		lVideoFrame3DDisplay.stop();

		lVideoFrame3DDisplay.close();

	}

}
