package rtlib.gui.video.video2d.jogl.demo;

import java.io.IOException;

import org.junit.Test;

import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;

public class VideoWindowDemo
{
	static
	{
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	static volatile long rnd = 123456789;

	@Test
	public void simpleRandom8BitDataTest() throws InterruptedException,
																				IOException
	{
		NDArrayTypedDirect<Byte> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(byte.class,
																															512,
																															512,
																															1);

		final VideoWindow lVideoWindow = new VideoWindow(	"VideoWindow test",
																											(int) lNDArrayDirect.getSizeAlongDimension(1),
																											(int) lNDArrayDirect.getSizeAlongDimension(2));
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setManualMinMax(true);
		// lVideoWindow.setLinearInterpolation(true);

		lVideoWindow.setSourceBuffer(lNDArrayDirect);

		lVideoWindow.setVisible(true);
		for (int i = 0; i < 10000; i++)
		{
			generateNoiseBuffer(lNDArrayDirect);
			lVideoWindow.notifyNewFrame();
			lVideoWindow.display();
			Thread.sleep(10);
		}

		lVideoWindow.close();

	}

	@Test
	public void simpleRandom16BitDataTest()	throws InterruptedException,
																		IOException
	{
		NDArrayTypedDirect<Short> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(	Short.class,
																															512,
																															512,
																															1);

		final VideoWindow lVideoWindow = new VideoWindow(	"VideoWindow test",
																											(int) lNDArrayDirect.getSizeAlongDimension(1),
																											(int) lNDArrayDirect.getSizeAlongDimension(2));
		lVideoWindow.setSyncToRefresh(false);
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setManualMinMax(false);
		lVideoWindow.setDisplayFrameRate(true);
		// lVideoWindow.setLinearInterpolation(true);

		lVideoWindow.setSourceBuffer(lNDArrayDirect);

		lVideoWindow.setVisible(true);
		for (int i = 0; i < 10000; i++)
		{
			generateNoiseBuffer(lNDArrayDirect);
			lVideoWindow.notifyNewFrame();
			lVideoWindow.display();
			// Thread.sleep(10);
		}

		lVideoWindow.close();

	}

	private void generateNoiseBuffer(final NDArrayTypedDirect<?> pNDArrayDirect)
	{

		final int lBufferLength = (int) pNDArrayDirect.getRAM()
																									.getSizeInBytes();
		for (int i = 0; i < lBufferLength; i++)
		{
			rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
			final byte lValue = (byte) (rnd & 0xFF); // Math.random()
			// System.out.print(lValue);
			pNDArrayDirect.setByteAligned(i, lValue);
		}
	}
}
