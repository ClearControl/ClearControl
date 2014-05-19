package rtlib.kam.memory.impl.gpu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import rtlib.core.memory.NativeMemoryAccess;
import rtlib.core.memory.SizeOf;
import rtlib.core.units.Magnitudes;
import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.impl.direct.RAMDirect;
import rtlib.kam.memory.impl.file.RAMFile;
import rtlib.kam.memory.impl.gpu.BufferGPU;

public class BufferGPUTests
{
	private static final long cSize = 128 * 128 * 128;
	private static final long cPerfSize = 512 * 512 * 512;

	@Test
	public void testLifeCycle()
	{
		// System.out.println("testLifeCycle().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Short> lBufferGPU = new BufferGPU<Short>(	lBestOpenCLContext,
																												short.class,
																												cSize,
																												true,
																												true);

		assertEquals(	cSize * SizeOf.sizeOfShort(),
									lBufferGPU.getSizeInBytes());

		assertFalse(lBufferGPU.isFree());

		long lMapAddress = lBufferGPU.map();

		for (int i = 0; i < cSize; i++)
		{
			NativeMemoryAccess.setShort(lMapAddress + 2 * i, (short) (i));

			short lShort = NativeMemoryAccess.getShort(lMapAddress + 2 * i);
			assertEquals((short) i, lShort);
		}

		assertTrue(lBufferGPU.isCurrentlyMapped());

		lBufferGPU.unmap();

		assertFalse(lBufferGPU.isCurrentlyMapped());

		lBufferGPU.free();

		assertTrue(lBufferGPU.isFree());
		// System.out.println("testLifeCycle().END");
	}

	@Test
	public void testReadWritePointerAccessible()
	{
		// System.out.println("testReadWritePointerAccessible().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Short> lBufferGPU = new BufferGPU<Short>(	lBestOpenCLContext,
																												short.class,
																												cSize,
																												true,
																												true);

		RAMDirect lRAMDirect = new RAMDirect(cSize * SizeOf.sizeOfShort());

		for (int i = 0; i < cSize; i++)
			lRAMDirect.setShortAligned(i, (short) i);

		lBufferGPU.readFrom(lRAMDirect);

		lBufferGPU.getCurrentQueue().waitForCompletion();

		for (int i = 0; i < cSize; i++)
			lRAMDirect.setShortAligned(i, (short) 0);

		long lMapAddress = lBufferGPU.map();

		for (int i = 0; i < cSize; i++)
		{
			short lShort = NativeMemoryAccess.getShort(lMapAddress + 2 * i);
			assertEquals((short) i, lShort);
		}

		assertTrue(lBufferGPU.isCurrentlyMapped());

		lBufferGPU.unmap();

		lBufferGPU.writeTo(lRAMDirect);
		lBufferGPU.getCurrentQueue().waitForCompletion();

		for (int i = 0; i < cSize; i++)
		{
			short lShort = lRAMDirect.getShortAligned(i);
			assertEquals((short) i, lShort);
		}

		lBufferGPU.free();

		assertTrue(lBufferGPU.isFree());
		// System.out.println("testReadWritePointerAccessible().END");
	}

	@Test
	public void testMapAndReadWrite()	throws IOException,
																		InterruptedException
	{
		// System.out.println("testMapAndReadWrite().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Short> lBufferGPU = new BufferGPU<Short>(	lBestOpenCLContext,
																												short.class,
																												cSize,
																												true,
																												true);

		RAMDirect lRAMDirect = new RAMDirect(cSize * SizeOf.sizeOfShort());

		// System.out.println("for (int i = 0; i < cSize; i++)");
		for (int i = 0; i < cSize; i++)
			lRAMDirect.setShortAligned(i, (short) i);

		// System.out.println("lBufferGPU.mapAndReadFrom(lRAMDirect);");
		lBufferGPU.mapAndReadFrom(lRAMDirect);

		for (int i = 0; i < cSize; i++)
			lRAMDirect.setShortAligned(i, (short) 0);

		// System.out.println("lBufferGPU.mapAndWriteTo(lRAMDirect);");
		lBufferGPU.mapAndWriteTo(lRAMDirect);

		// System.out.println("for (int i = 0; i < cSize; i++)");
		for (int i = 0; i < cSize; i++)
		{
			short lShort = lRAMDirect.getShortAligned(i);
			// System.out.println(lShort);
			assertEquals((short) i, lShort);
		}

		// System.out.println("lRAMDirect.free();");
		lRAMDirect.free();
		// System.out.println("lBufferGPU.free();");
		lBufferGPU.free();
		// System.out.println("testMapAndReadWrite().END");
	}

	@Test
	public void testReadWriteToMappableFile()	throws IOException,
																						InterruptedException
	{
		// System.out.println("testReadWriteToMappableFile().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Short> lBufferGPU = new BufferGPU<Short>(	lBestOpenCLContext,
																												short.class,
																												cSize,
																												true,
																												true);

		File lTempFile = File.createTempFile(	this.getClass()
																							.getSimpleName(),
																					"testWriteToMappableMemory");
		RAMFile lRAMFile = new RAMFile(	lTempFile,
																		cSize * SizeOf.sizeOfShort());

		// System.out.println("lBufferGPU.readFromMapped(lRAMFile);");
		lBufferGPU.readFromMapped(lRAMFile);
		// System.out.println("lBufferGPU.writeToMapped(lRAMFile);");
		lBufferGPU.writeToMapped(lRAMFile);
		// System.out.println("lRAMFile.free();");
		lRAMFile.free();
		// System.out.println("lBufferGPU.free();");
		lBufferGPU.free();

		assertTrue(lTempFile.exists());
		assertEquals(	cSize * SizeOf.sizeOfShort(),
									Files.size(lTempFile.toPath()));
		// System.out.println("testReadWriteToMappableFile().END");
	}

	@Test
	public void testWritePerformance()
	{
		// System.out.println("testWritePerformance().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Byte> lBufferGPU = new BufferGPU<Byte>(	lBestOpenCLContext,
																											byte.class,
																											cPerfSize,
																											false,
																											true);

		RAMDirect lRAMDirect = new RAMDirect(cPerfSize * SizeOf.sizeOfByte());

		for (int i = 0; i < cPerfSize; i++)
			lRAMDirect.setByteAligned(i, (byte) i);

		final int lNumberOfCycles = 10;

		final long lStartNanos = System.nanoTime();
		for (int cycle = 0; cycle < lNumberOfCycles; cycle++)
		{
			lBufferGPU.readFrom(lRAMDirect);
		}
		lBufferGPU.getCurrentQueue().waitForCompletion();

		final long lStopNanos = System.nanoTime();

		final double lGigaBytesPerSecond = (Magnitudes.unit2giga(lNumberOfCycles * cPerfSize)) / Magnitudes.nano2unit(lStopNanos - lStartNanos);

		System.out.println("BufferGPU write speed: " + lGigaBytesPerSecond
												+ " GB/s");

		assertTrue(lGigaBytesPerSecond > 1);

		lBufferGPU.free();

		assertTrue(lBufferGPU.isFree());
		// System.out.println("testWritePerformance().END");
	}

	@Test
	public void testReadPerformance()
	{
		// System.out.println("testReadPerformance().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Byte> lBufferGPU = new BufferGPU<Byte>(	lBestOpenCLContext,
																											byte.class,
																											cPerfSize,
																											true,
																											false);

		RAMDirect lRAMDirect = new RAMDirect(cPerfSize * SizeOf.sizeOfByte());

		for (int i = 0; i < cPerfSize; i++)
			lRAMDirect.setByteAligned(i, (byte) i);
		lBufferGPU.readFrom(lRAMDirect);
		lBufferGPU.getCurrentQueue().waitForCompletion();

		final int lNumberOfCycles = 10;

		final long lStartNanos = System.nanoTime();
		for (int cycle = 0; cycle < lNumberOfCycles; cycle++)
		{
			lBufferGPU.writeTo(lRAMDirect);
		}
		lBufferGPU.getCurrentQueue().waitForCompletion();

		final long lStopNanos = System.nanoTime();

		final double lGigaBytesPerSecond = (Magnitudes.unit2giga(lNumberOfCycles * cPerfSize)) / Magnitudes.nano2unit(lStopNanos - lStartNanos);

		System.out.println("BufferGPU read speed: " + lGigaBytesPerSecond
												+ " GB/s");

		assertTrue(lGigaBytesPerSecond > 1);

		lBufferGPU.free();

		assertTrue(lBufferGPU.isFree());
		// System.out.println("testReadPerformance().END");
	}

	@Test
	public void testMappedWritePerformance()
	{
		// System.out.println("testMappedWritePerformance().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Byte> lBufferGPU = new BufferGPU<Byte>(	lBestOpenCLContext,
																											byte.class,
																											cPerfSize,
																											false,
																											true);

		RAMDirect lRAMDirect = new RAMDirect(cPerfSize * SizeOf.sizeOfByte());

		for (int i = 0; i < cPerfSize; i++)
			lRAMDirect.setByteAligned(i, (byte) i);

		final int lNumberOfCycles = 10;

		final long lStartNanos = System.nanoTime();
		for (int cycle = 0; cycle < lNumberOfCycles; cycle++)
		{
			lBufferGPU.mapAndReadFrom(lRAMDirect);
		}
		lBufferGPU.getCurrentQueue().waitForCompletion();
		final long lStopNanos = System.nanoTime();

		final double lGigaBytesPerSecond = (Magnitudes.unit2giga(lNumberOfCycles * cPerfSize)) / Magnitudes.nano2unit(lStopNanos - lStartNanos);

		System.out.println("BufferGPU mapped write speed: " + lGigaBytesPerSecond
												+ " GB/s");

		assertTrue(lGigaBytesPerSecond > 1);

		lBufferGPU.free();

		assertTrue(lBufferGPU.isFree());
		// System.out.println("testMappedWritePerformance().END");
	}

	@Test
	public void testMappedReadPerformance()
	{
		// System.out.println("testMappedReadPerformance().BEGIN");
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		BufferGPU<Byte> lBufferGPU = new BufferGPU<Byte>(	lBestOpenCLContext,
																											byte.class,
																											cPerfSize,
																											true,
																											false);

		RAMDirect lRAMDirect = new RAMDirect(cPerfSize * SizeOf.sizeOfByte());

		for (int i = 0; i < cPerfSize; i++)
			lRAMDirect.setByteAligned(i, (byte) i);
		lBufferGPU.readFrom(lRAMDirect);
		lBufferGPU.getCurrentQueue().waitForCompletion();

		final int lNumberOfCycles = 10;

		final long lStartNanos = System.nanoTime();
		for (int cycle = 0; cycle < lNumberOfCycles; cycle++)
		{
			lBufferGPU.mapAndWriteTo(lRAMDirect);
		}
		lBufferGPU.getCurrentQueue().waitForCompletion();

		final long lStopNanos = System.nanoTime();

		final double lGigaBytesPerSecond = (Magnitudes.unit2giga(lNumberOfCycles * cPerfSize)) / Magnitudes.nano2unit(lStopNanos - lStartNanos);

		System.out.println("BufferGPU mapped read speed: " + lGigaBytesPerSecond
												+ " GB/s");

		assertTrue(lGigaBytesPerSecond > 1);

		lBufferGPU.free();

		assertTrue(lBufferGPU.isFree());
		// System.out.println("testMappedReadPerformance().END");
	}

}
