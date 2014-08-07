package rtlib.gui.video.video2d.jogl.demo;

import java.io.IOException;

import org.junit.Test;

import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayDirect;

public class VideoWindowDemo
{
	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	static volatile long rnd = 123456789;

	@Test
	public void simpleRandomDataTest() throws InterruptedException,
																		IOException
	{
		NDArrayDirect lNDArrayDirect = NDArrayDirect.allocateSXYZ(1,
																															512,
																															512,
																															1);

		final VideoWindow lVideoWindow = new VideoWindow(	"VideoWindow test",
																											lNDArrayDirect.getSizeAlongDimension(0),
																											lNDArrayDirect.getSizeAlongDimension(1),
																											lNDArrayDirect.getSizeAlongDimension(2));
		lVideoWindow.setDisplayOn(true);

		lVideoWindow.setSourceBuffer(lNDArrayDirect);

		lVideoWindow.setVisible(true);
		for (int i = 0; i < 1000; i++)
		{
			generateNoiseBuffer(lNDArrayDirect);
			lVideoWindow.notifyNewFrame();
			lVideoWindow.display();
			Thread.sleep(10);
		}

		lVideoWindow.close();

	}

	private void generateNoiseBuffer(final NDArrayDirect pNDArrayDirect)
	{

		final int lBufferLength = (int) pNDArrayDirect.getLengthInElements();
		for (int i = 0; i < lBufferLength; i++)
		{
			rnd = (long) (rnd + 1 + 512 * Math.random());
			rnd = rnd % 256;
			final byte lValue = (byte) (rnd); // Math.random()
			// System.out.print(lValue);
			pNDArrayDirect.setByteAligned(i, lValue);
		}
	}
}
