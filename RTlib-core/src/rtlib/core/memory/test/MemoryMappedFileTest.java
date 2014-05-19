package rtlib.core.memory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

import rtlib.core.memory.MemoryMappedFile;
import rtlib.core.memory.MemoryMappedFileException;
import rtlib.core.memory.NativeMemoryAccess;

public class MemoryMappedFileTest
{

	@Test
	public void test() throws IOException, InterruptedException
	{
		File lTempFile = File.createTempFile(	"MemoryMappedFileTest",
																					"test1");
		lTempFile.deleteOnExit();
		System.out.println(lTempFile);
		FileChannel lFileChannel = FileChannel.open(lTempFile.toPath(),
																								StandardOpenOption.CREATE,
																								StandardOpenOption.READ,
																								StandardOpenOption.WRITE,
																								StandardOpenOption.DELETE_ON_CLOSE);

		long lMappingLength = (2 * (Integer.MAX_VALUE - 8L));
		System.out.println("lMappingLength=" + lMappingLength);

		long lMappingAddress = MemoryMappedFile.map(lFileChannel,
																								MemoryMappedFile.ReadWrite,
																								0,
																								lMappingLength,
																								true);


		System.out.println(NativeMemoryAccess.getByte(lMappingAddress));
		for (long i = 0; i < lMappingLength; i++)
		{
			NativeMemoryAccess.setByte(lMappingAddress + i, (byte) 123);
		}

		// MemoryMappedFile.force(lFileChannel, true);

		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMappingAddress));
		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMappingAddress + lMappingLength
																							- 1));



		MemoryMappedFile.unmap(	lFileChannel,
														lMappingAddress,
														lMappingLength);

		try
		{
			MemoryMappedFile.unmap(	lFileChannel,
															lMappingAddress - 1024,
															lMappingLength);
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

		long lMappingAddress2 = MemoryMappedFile.map(	lFileChannel,
																									MemoryMappedFile.ReadOnly,
																									0,
																									lMappingLength,
																									true);

		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMappingAddress2));
		assertEquals(	(byte) 123,
									NativeMemoryAccess.getByte(lMappingAddress2 + lMappingLength
																							- 1));

		MemoryMappedFile.unmap(	lFileChannel,
														lMappingAddress2,
														lMappingLength);

		lFileChannel.close();

	}

}
