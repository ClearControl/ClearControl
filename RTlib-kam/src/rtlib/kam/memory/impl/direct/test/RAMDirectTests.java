package rtlib.kam.memory.impl.direct.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.impl.direct.RAMDirect;
import rtlib.kam.memory.ram.test.RAMTests;

public class RAMDirectTests
{

	@Test
	public void testBasics()
	{
		RAMDirect lRAMDirect = new RAMDirect(2L * Integer.MAX_VALUE);

		RAMTests.testBasics(lRAMDirect, MemoryType.CPURAMDIRECT, true);

	}

	@Test
	public void testCopySameSize()
	{
		RAMDirect lRAMDirect1 = new RAMDirect(1L * Integer.MAX_VALUE);
		RAMDirect lRAMDirect2 = new RAMDirect(1L * Integer.MAX_VALUE);

		RAMTests.testCopySameSize(lRAMDirect1, lRAMDirect2);

	}

	@Test
	public void testCopyDifferentSize()
	{
		RAMDirect lRAMDirect1 = new RAMDirect(4);
		RAMDirect lRAMDirect2 = new RAMDirect(8);

		RAMTests.testCopyDifferentSize(lRAMDirect1, lRAMDirect2);
	}

	@Test
	public void testCopyChecks()
	{
		RAMDirect lRAMDirect1 = new RAMDirect(4);
		RAMDirect lRAMDirect2 = new RAMDirect(8);

		RAMTests.testCopyChecks(lRAMDirect1, lRAMDirect2);
	}

	@Test
	public void testWriteRead()
	{
		RAMDirect lRAMDirect = new RAMDirect(4);

		RAMTests.testWriteRead(lRAMDirect);
	}

	@Test
	public void testWriteToReadFromFileChannel() throws IOException
	{
		File lTempFile = File.createTempFile(	this.getClass()
																							.getSimpleName(),
																					"testWriteToReadFromFileChannel");
		lTempFile.delete();
		lTempFile.deleteOnExit();

		FileChannel lFileChannel1 = FileChannel.open(	lTempFile.toPath(),
																									StandardOpenOption.CREATE,
																									StandardOpenOption.WRITE,
																									StandardOpenOption.READ);

		RAMDirect lRAMDirect1 = new RAMDirect(1023);

		for (int i = 0; i < lRAMDirect1.getSizeInBytes(); i++)
			lRAMDirect1.setByte(i, (byte) i);

		lRAMDirect1.writeBytesToFileChannel(lFileChannel1, 512);
		lRAMDirect1.free();
		lFileChannel1.close();

		assertTrue(lTempFile.exists());
		assertEquals(512 + 1023, lTempFile.length());

		FileChannel lFileChannel2 = FileChannel.open(	lTempFile.toPath(),
																									StandardOpenOption.CREATE,
																									StandardOpenOption.READ,
																									StandardOpenOption.WRITE);

		assertEquals(512 + 1023, lFileChannel2.size());

		RAMDirect lRAMDirect2 = new RAMDirect(1023);
		lRAMDirect2.readBytesFromFileChannel(	lFileChannel2,
																					512,
																					lRAMDirect2.getSizeInBytes());

		for (int i = 0; i < lRAMDirect2.getSizeInBytes(); i++)
			assertEquals((byte) i, lRAMDirect2.getByte(i));

		lRAMDirect2.free();
		lFileChannel2.close();
	}
}
