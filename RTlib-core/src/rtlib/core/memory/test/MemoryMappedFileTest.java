package rtlib.core.memory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import rtlib.core.memory.MemoryMappedFile;
import rtlib.core.memory.MemoryMappedFile.MemoryMap;
import rtlib.core.memory.MemoryMappedFileException;
import rtlib.core.memory.NativeMemoryAccess;

public class MemoryMappedFileTest
{

	@Test
	public void testMapLargeFile() throws IOException,
																InterruptedException
	{
		File lTempFile = File.createTempFile(	"MemoryMappedFileTest",
																					"test1");
		// lTempFile.deleteOnExit();
		System.out.println(lTempFile);

		System.out.println("test part 1");
		FileChannel lFileChannel = FileChannel.open(lTempFile.toPath(),
																								StandardOpenOption.CREATE,
																								StandardOpenOption.READ,
																								StandardOpenOption.WRITE);

		long lMappingLength = 2 * 4096 + 1;// (2 * (Integer.MAX_VALUE - 8L));
		System.out.println("lMappingLength=" + lMappingLength);

		MemoryMap lMemoryMap1 = MemoryMappedFile.map(	lFileChannel,
																								MemoryMappedFile.ReadWrite,
																								0,
																								lMappingLength,
																								true);

		System.out.println(NativeMemoryAccess.getByte(lMemoryMap1.mMappedRegionAddress));
		for (long i = 0; i < lMappingLength; i += 1)
		{
			NativeMemoryAccess.setByte(	lMemoryMap1.mMappedRegionAddress + i,
																	(byte) 123);
		}

		// MemoryMappedFile.force(lFileChannel, true);

		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMemoryMap1.mMappedRegionAddress));
		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMemoryMap1.mMappedRegionAddress + lMappingLength
																							- 1));

		MemoryMappedFile.unmap(	lFileChannel,
														lMemoryMap1.mMappedRegionAddress,
														lMappingLength);

		try
		{
			MemoryMappedFile.unmap(	lFileChannel,
															lMemoryMap1.mMappedRegionAddress - 1024,
															lMappingLength);/**/
			// We should not reach this point, exception should be raised
			fail();
		}
		catch (MemoryMappedFileException e)
		{
			// should land here
		}
		catch (Throwable e)
		{
			// not here
			fail();
		}

		lFileChannel.close();

		Thread.sleep(1000);

		System.out.println("test part 2");
		lFileChannel = FileChannel.open(lTempFile.toPath(),
																		StandardOpenOption.READ,
																		StandardOpenOption.WRITE);

		MemoryMap lMemoryMap2 = MemoryMappedFile.map(	lFileChannel,
																									MemoryMappedFile.ReadOnly,
																									0,
																									lMappingLength,
																									true);

		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMemoryMap2.mMappedRegionAddress));
		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMemoryMap2.mMappedRegionAddress + lMappingLength
																							- 1));

		MemoryMappedFile.unmap(	lFileChannel,
														lMemoryMap2.mMappedRegionAddress,
														lMappingLength);


		lFileChannel.close();

		System.out.println("test part 3");

		long lMappingOffset = 4096;
		lFileChannel = FileChannel.open(lTempFile.toPath(),
																		StandardOpenOption.READ,
																		StandardOpenOption.WRITE);

		MemoryMap lMemoryMap3 = MemoryMappedFile.map(	lFileChannel,
																									MemoryMappedFile.ReadOnly,
																									lMappingOffset,
																									lMappingOffset,
																									true);

		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMemoryMap3.mMappedRegionAddress + lMappingLength
																							- lMappingOffset
																							- 2));

		MemoryMappedFile.unmap(	lFileChannel,
														lMemoryMap3.mMappedRegionAddress,
														lMappingOffset);

		lFileChannel.close();
		/**/

		// lTempFile.delete();

	}

}
