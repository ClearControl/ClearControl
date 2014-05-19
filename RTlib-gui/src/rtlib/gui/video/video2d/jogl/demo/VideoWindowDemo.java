package rtlib.gui.video.video2d.jogl.demo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.junit.Test;

import rtlib.gui.video.video2d.jogl.VideoWindow;

public class VideoWindowDemo
{
	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	static long rnd = 123456789;
	
	@Test
	public void simpleRandomDataTest() throws InterruptedException
	{
		final VideoWindow lVideoWindow = new VideoWindow(	"VideoWindow test",
																											1,
																											512,
																											512);
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
			Thread.sleep(10);
		}

	}

	private void generateNoiseBuffer(final ByteBuffer pVideoByteBuffer)
	{
		pVideoByteBuffer.clear();
		
		final int lBufferLength = pVideoByteBuffer.limit();
		for (int i = 0; i < lBufferLength; i++)
		{
			rnd = rnd+i+1;
			rnd = rnd%256;
			final byte lValue = (byte) (rnd); // Math.random()
			// System.out.print(lValue);
			pVideoByteBuffer.put(lValue);
		}
	}
}
