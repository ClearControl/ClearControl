package rtlib.kam.memory.impl.gpu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import rtlib.core.units.Magnitudes;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.impl.gpu.Image3DGPU;
import coremem.memmap.FileMappedMemoryRegion;
import coremem.offheap.OffHeapMemoryRegion;
import coremem.util.SizeOf;


public class Image3DGPUTests
{
	private static final long cSizeX = 512;
	private static final long cSizeY = 512 + 1;
	private static final long cSizeZ = 512 + 3;

	private static final long cVolume = cSizeX * cSizeY * cSizeZ;

	@Test
	public void testLifeCycle()
	{
		final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		final Image3DGPU<Float> lImage3DGPU = new Image3DGPU<Float>(lBestOpenCLContext,
																																float.class,
																																true,
																																true,
																																cSizeX,
																																cSizeY,
																																cSizeZ);

		assertEquals(cSizeX, lImage3DGPU.getWidth());
		assertEquals(cSizeY, lImage3DGPU.getHeight());
		assertEquals(cSizeZ, lImage3DGPU.getDepth());

		assertEquals(cVolume, lImage3DGPU.getVolume());

		assertEquals(cVolume, lImage3DGPU.getLengthInElements());

		assertEquals(	cVolume * SizeOf.sizeOfFloat(),
									lImage3DGPU.getSizeInBytes());

		assertFalse(lImage3DGPU.isFree());

		lImage3DGPU.free();

		assertTrue(lImage3DGPU.isFree());

	}

	@Test
	public void testReadWritePointerAccessible() throws InterruptedException
	{
		// System.out.println("testReadWritePointerAccessible().BEGIN");
		final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		final Image3DGPU<Short> lImage3DGPU = new Image3DGPU<Short>(lBestOpenCLContext,
																																short.class,
																																true,
																																true,
																																cSizeX,
																																cSizeY,
																																cSizeZ);

		final OffHeapMemoryRegion lOffHeapMemoryRegion = new OffHeapMemoryRegion(cVolume * SizeOf.sizeOfShort());

		for (int i = 0; i < cVolume; i++)
			lOffHeapMemoryRegion.setShortAligned(i, (short) i);

		for (int i = 0; i < cVolume; i++)
		{
			final short lShort = lOffHeapMemoryRegion.getShortAligned(i);
			assertEquals((short) i, lShort);
		}

		lImage3DGPU.readFrom(lOffHeapMemoryRegion);
		lImage3DGPU.getCurrentQueue().waitForCompletion();
		// Thread.sleep(2000);

		for (int i = 0; i < cVolume; i++)
			lOffHeapMemoryRegion.setShortAligned(i, (short) 0);

		lImage3DGPU.writeTo(lOffHeapMemoryRegion);
		lImage3DGPU.getCurrentQueue().waitForCompletion();
		// Thread.sleep(2000);

		for (int i = 0; i < cVolume; i++)
		{
			final short lShort = lOffHeapMemoryRegion.getShortAligned(i);
			// System.out.println(lShort);
			//

			assertEquals((short) i, lShort);
		}

		lImage3DGPU.free();

		assertTrue(lImage3DGPU.isFree());
		// System.out.println("testReadWritePointerAccessible().END");
	}

	// @Test
	public void testReadWriteToMappableFile()	throws IOException,
																						InterruptedException
	{
		final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		final Image3DGPU<Short> lImage3DGPU = new Image3DGPU<Short>(lBestOpenCLContext,
																																short.class,
																																true,
																																true,
																																cSizeX,
																																cSizeY,
																																cSizeZ);

		final File lTempFile = File.createTempFile(	this.getClass()
																										.getSimpleName(),
																								"testWriteToMappableMemory");
		final FileMappedMemoryRegion lFileMappedMemoryRegion = new FileMappedMemoryRegion(	lTempFile,
																					cVolume * SizeOf.sizeOfShort());

		lImage3DGPU.readFromMapped(lFileMappedMemoryRegion);
		lImage3DGPU.writeToMapped(lFileMappedMemoryRegion);
		lFileMappedMemoryRegion.free();
		lImage3DGPU.free();

		assertTrue(lTempFile.exists());
		assertEquals(	cVolume * SizeOf.sizeOfShort(),
									Files.size(lTempFile.toPath()));
	}

	@Test
	public void testWritePerformance()
	{
		// System.out.println("testWritePerformance().BEGIN");
		final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		final Image3DGPU<Byte> lImage3DGPU = new Image3DGPU<Byte>(lBestOpenCLContext,
																															byte.class,
																															true,
																															true,
																															cSizeX,
																															cSizeY,
																															cSizeZ);

		final OffHeapMemoryRegion lOffHeapMemoryRegion = new OffHeapMemoryRegion(cVolume * SizeOf.sizeOfByte());

		for (int i = 0; i < cVolume; i++)
			lOffHeapMemoryRegion.setByteAligned(i, (byte) i);

		final int lNumberOfCycles = 10;

		final long lStartNanos = System.nanoTime();
		for (int cycle = 0; cycle < lNumberOfCycles; cycle++)
		{
			lImage3DGPU.readFrom(lOffHeapMemoryRegion);
		}
		lImage3DGPU.getCurrentQueue().waitForCompletion();
		final long lStopNanos = System.nanoTime();

		final double lGigaBytesPerSecond = (Magnitudes.unit2giga(lNumberOfCycles * cVolume)) / Magnitudes.nano2unit(lStopNanos - lStartNanos);

		System.out.println("Image3DGPU write speed: " + lGigaBytesPerSecond
												+ " GB/s");

		assertTrue(lGigaBytesPerSecond > 0);

		lImage3DGPU.free();

		assertTrue(lImage3DGPU.isFree());
		// System.out.println("testWritePerformance().END");
	}

	@Test
	public void testReadPerformance()
	{
		// System.out.println("testReadPerformance().BEGIN");
		final ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		final Image3DGPU<Byte> lImage3DGPU = new Image3DGPU<Byte>(lBestOpenCLContext,
																															byte.class,
																															true,
																															true,
																															cSizeX,
																															cSizeY,
																															cSizeZ);

		final OffHeapMemoryRegion lOffHeapMemoryRegion = new OffHeapMemoryRegion(cVolume * SizeOf.sizeOfByte());

		for (int i = 0; i < cVolume; i++)
			lOffHeapMemoryRegion.setByteAligned(i, (byte) i);
		lImage3DGPU.readFrom(lOffHeapMemoryRegion);
		lImage3DGPU.getCurrentQueue().waitForCompletion();

		final int lNumberOfCycles = 10;

		final long lStartNanos = System.nanoTime();
		for (int cycle = 0; cycle < lNumberOfCycles; cycle++)
		{
			lImage3DGPU.writeTo(lOffHeapMemoryRegion);
		}
		lImage3DGPU.getCurrentQueue().waitForCompletion();

		final long lStopNanos = System.nanoTime();

		final double lGigaBytesPerSecond = (Magnitudes.unit2giga(lNumberOfCycles * cVolume)) / Magnitudes.nano2unit(lStopNanos - lStartNanos);

		System.out.println("Image3DGPU read speed: " + lGigaBytesPerSecond
												+ " GB/s");

		assertTrue(lGigaBytesPerSecond > 0);

		lImage3DGPU.free();

		assertTrue(lImage3DGPU.isFree());
		// System.out.println("testReadPerformance().END");
	}

}
