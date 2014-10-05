package rtlib.gui.video.video2d.jogl.demo;

import java.io.IOException;

import org.junit.Test;

import rtlib.gui.video.video2d.jogl.VideoWindow;
import rtlib.kam.memory.impl.direct.NDArrayTypedDirect;

public class VideoWindowDemo
{

	static volatile long rnd = 123456789;

	@Test
	public void simpleRandom8BitDataTest() throws InterruptedException,
																				IOException
	{
		NDArrayTypedDirect<Byte> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(byte.class,
																																							512,
																																							512,
																																							1);

		final VideoWindow<Byte> lVideoWindow = new VideoWindow<Byte>(	"VideoWindow test",
																																	Byte.class,
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
			lVideoWindow.requestDisplay();
			Thread.sleep(10);
		}

		lVideoWindow.close();

	}

	@Test
	public void simpleRandom16BitDataTest()	throws InterruptedException,
																					IOException
	{
		final int lWidth = 512;
		final int lHeight = 512;

		NDArrayTypedDirect<Character> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(	Character.class,
																																										lWidth,
																																										lHeight,
																																										1);

		final VideoWindow<Character> lVideoWindow = new VideoWindow<Character>(	"VideoWindow test",
																																						Character.class,
																																						lWidth,
																																						lHeight);
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
			lVideoWindow.requestDisplay();
			// Thread.sleep(10);
		}

		lVideoWindow.close();

	}

	@Test
	public void simpleRandom32BitDataTest()	throws InterruptedException,
																					IOException
	{
		final int lWidth = 512;
		final int lHeight = 512;

		NDArrayTypedDirect<Integer> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(	Integer.class,
																																									lWidth,
																																									lHeight,
																																									1);

		final VideoWindow<Integer> lVideoWindow = new VideoWindow<Integer>(	"VideoWindow test",
																																				Integer.class,
																																				lWidth,
																																				lHeight);
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setManualMinMax(false);
		lVideoWindow.setDisplayFrameRate(true);
		// lVideoWindow.setLinearInterpolation(true);

		lVideoWindow.setSourceBuffer(lNDArrayDirect);

		lVideoWindow.setVisible(true);
		for (int i = 0; i < 10000; i++)
		{
			generateIntNoiseBuffer(lNDArrayDirect);
			lVideoWindow.notifyNewFrame();
			lVideoWindow.requestDisplay();
			// Thread.sleep(10);
		}

		lVideoWindow.close();

	}

	@Test
	public void simpleRandomFloatDataTest()	throws InterruptedException,
																					IOException
	{
		final int lWidth = 512;
		final int lHeight = 512;

		NDArrayTypedDirect<Float> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(	Float.class,
																																								lWidth,
																																								lHeight,
																																								1);

		final VideoWindow<Float> lVideoWindow = new VideoWindow<Float>(	"VideoWindow test",
																																		Float.class,
																																		lWidth,
																																		lHeight);
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setManualMinMax(false);
		lVideoWindow.setDisplayFrameRate(true);
		// lVideoWindow.setLinearInterpolation(true);

		lVideoWindow.setSourceBuffer(lNDArrayDirect);

		lVideoWindow.setVisible(true);
		for (int i = 0; i < 10000; i++)
		{
			generateNoiseFloatBuffer(lNDArrayDirect);
			lVideoWindow.notifyNewFrame();
			lVideoWindow.requestDisplay();
			Thread.sleep(1);
		}

		lVideoWindow.close();

	}

	@Test
	public void simpleRandomDoubleDataTest() throws InterruptedException,
																					IOException
	{

		final int lWidth = 512;
		final int lHeight = 512;

		NDArrayTypedDirect<Double> lNDArrayDirect = NDArrayTypedDirect.allocateTXYZ(Double.class,
																																								lWidth,
																																								lHeight,
																																								1);

		final VideoWindow<Double> lVideoWindow = new VideoWindow<Double>(	"VideoWindow test",
																																			Double.class,
																																			lWidth,
																																			lHeight);
		lVideoWindow.setDisplayOn(true);
		lVideoWindow.setManualMinMax(false);
		lVideoWindow.setDisplayFrameRate(true);
		// lVideoWindow.setLinearInterpolation(true);

		lVideoWindow.setSourceBuffer(lNDArrayDirect);

		lVideoWindow.setVisible(true);
		for (int i = 0; i < 1000000; i++)
		{
			if (i % 10 == 0)
				generateNoiseDoubleBuffer(lNDArrayDirect);
			lVideoWindow.notifyNewFrame();
			lVideoWindow.requestDisplay();
			// Thread.sleep(1);
		}

		lVideoWindow.close();

	}

	private void generateNoiseFloatBuffer(final NDArrayTypedDirect<Float> pNDArrayDirect)
	{

		final int lLength = (int) pNDArrayDirect.getVolume();
		for (int i = 0; i < lLength; i++)
		{
			rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
			final float lValue = (rnd & 0xFF); // Math.random()
			// System.out.println(lValue);
			pNDArrayDirect.setFloatAligned(i, lValue);
		}
	}

	private void generateNoiseDoubleBuffer(final NDArrayTypedDirect<Double> pNDArrayDirect)
	{

		final int lLength = (int) pNDArrayDirect.getVolume();
		for (int i = 0; i < lLength; i++)
		{
			final double lValue = Math.random() * 0.001;
			pNDArrayDirect.setDoubleAligned(i, lValue);
		}
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

	private void generateIntNoiseBuffer(final NDArrayTypedDirect<Integer> pNDArrayDirect)
	{

		final int lNumberOfInts = (int) pNDArrayDirect.getVolume();
		for (int i = 0; i < lNumberOfInts; i++)
		{
			rnd = ((rnd % 257) * i) + 1 + (rnd << 7);
			final int lValue = (int) (rnd & 0xFF); // Math.random()
			// System.out.print(lValue);
			pNDArrayDirect.setIntAligned(i, lValue);
		}
	}
}
