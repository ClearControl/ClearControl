package rtlib.kam.memory.impl.file.test;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.impl.file.RAMFile;
import rtlib.kam.memory.ram.RAM;
import rtlib.kam.memory.ram.test.RAMTests;

public class RAMFileTests
{

	private static final long cMemoryRegionSize = 1024;

	@Test
	public void testLargeMemory() throws IOException
	{
		RAM lRAM = new RAMFile(	createTempFile(),
														1000,
														(long) (1.1 * Integer.MAX_VALUE));
		RAMTests.testBasics(lRAM, MemoryType.FILERAM, false);

	}

	@Test
	public void testBasics() throws IOException
	{
		RAM lRAM = new RAMFile(createTempFile(), 1000, cMemoryRegionSize);

		RAMTests.testBasics(lRAM, MemoryType.FILERAM, false);
	}

	private File createTempFile() throws IOException
	{
		File lCreateTempFile = File.createTempFile(	RAMFileTests.class.toString(),
																								"" + Math.random());
		lCreateTempFile.deleteOnExit();
		return lCreateTempFile;
	}

	@Test
	public void testCopySameSize() throws IOException
	{
		RAM lRAM1 = new RAMFile(createTempFile(), 1000, cMemoryRegionSize);
		RAM lRAM2 = new RAMFile(createTempFile(), 1000, cMemoryRegionSize);

		RAMTests.testCopySameSize(lRAM1, lRAM2);

	}

	@Test
	public void testCopyDifferentSize() throws IOException
	{
		RAM lRAM1 = new RAMFile(createTempFile(), 1, 4);
		RAM lRAM2 = new RAMFile(createTempFile(), 1, 8);

		RAMTests.testCopyDifferentSize(lRAM1, lRAM2);
	}

	@Test
	public void testCopyChecks() throws IOException
	{
		RAM lRAM1 = new RAMFile(createTempFile(), 1, 4);
		RAM lRAM2 = new RAMFile(createTempFile(), 1, 8);

		RAMTests.testCopyChecks(lRAM1, lRAM2);
	}

	@Test
	public void testWriteRead() throws IOException
	{
		RAM lRAM = new RAMFile(createTempFile(), 1, 4);

		RAMTests.testWriteRead(lRAM);
	}
}
