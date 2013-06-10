package gui.video.jogl.test;

import gui.video.jogl.VideoWindow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

public class VideoWindowTests
{
	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	@Test
	public void simpleRandomDataTest() throws InterruptedException
	{
		final VideoWindow lVideoWindow = new VideoWindow(1, 512, 512);
		lVideoWindow.setDisplayOn(true);

		final ByteBuffer lVideoByteBuffer = ByteBuffer.allocateDirect(lVideoWindow.getMaxBufferLength())
																									.order(ByteOrder.nativeOrder());

		lVideoWindow.setSourceBuffer(lVideoByteBuffer);

		lVideoWindow.setVisible(true);
		while (true)
		{
			generateNoiseBuffer(lVideoByteBuffer);

			lVideoWindow.notifyNewFrame();
			lVideoWindow.display();
			Thread.sleep(1000);
		}

	}

	private void generateNoiseBuffer(final ByteBuffer pVideoByteBuffer)
	{
		pVideoByteBuffer.clear();

		final int lBufferLength = pVideoByteBuffer.limit();
		for (int i = 0; i < lBufferLength; i++)
		{
			final byte lValue = (byte) ((int) (1 * 255) % 256); // Math.random()
			// System.out.print(lValue);
			pVideoByteBuffer.put(lValue);
		}
	}
}
