package rtlib.core.memory.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import rtlib.core.memory.InvalidNativeMemoryAccessException;
import rtlib.core.memory.NativeMemoryAccess;

public class NativeMemoryAccessTests
{
	final private static long cBufferSize = 2 * (long) Integer.MAX_VALUE;

	@Test
	public void testMaxAllocation()
	{
		int i = 0;
		try
		{
			NativeMemoryAccess.setMaximumAllocatableMemory(1000L * 1000L);
			for (; i < 2000; i++)
			{
				NativeMemoryAccess.allocateMemory(1000);
			}
			fail();
		}
		catch (OutOfMemoryError e)
		{
			assertTrue(i >= 1000);
		}
		catch (Throwable lE)
		{
			fail();
		}

		NativeMemoryAccess.freeAll();
		assertEquals(0, NativeMemoryAccess.getTotalAllocatedMemory());

		NativeMemoryAccess.setMaximumAllocatableMemory(Long.MAX_VALUE);
	}

	@Test
	public void testAllocateReallocateFree()
	{
		try
		{
			// System.out.println(cBufferSize);
			long lAddress = NativeMemoryAccess.allocateMemory(cBufferSize);

			NativeMemoryAccess.setByte(lAddress, (byte) 123);
			assertEquals(NativeMemoryAccess.getByte(lAddress), (byte) 123);

			long lAddressReallocated = NativeMemoryAccess.reallocateMemory(	lAddress,
																																			10);

			NativeMemoryAccess.setByte(lAddressReallocated + 9, (byte) 123);
			assertEquals(	NativeMemoryAccess.getByte(lAddressReallocated + 9),
										(byte) 123);

			NativeMemoryAccess.freeMemory(lAddressReallocated);
		}
		catch (InvalidNativeMemoryAccessException e)
		{
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void testSuperBig()
	{
		// System.out.println("begin");

		try
		{
			final long lLength = 320L * 1000L * 1000L * 1000L;

			System.out.println("allocateMemory");
			long lAddress = NativeMemoryAccess.allocateMemory(lLength);
			System.out.println("lAddress=" + lAddress);
			assertFalse(lAddress == 0);

			/*System.out.println("setMemory");
			NativeMemoryAccess.setMemory(lAddress, lLength, (byte) 0);/**/

			System.out.println("setByte(s)");
			for (long i = 0; i < lLength; i += 1000L * 1000L)
			{
				NativeMemoryAccess.setByte(lAddress + i, (byte) i);
			}

			System.out.println("getByte(s)");
			for (long i = 0; i < lLength; i += 1000L * 1000L)
			{
				byte lValue = NativeMemoryAccess.getByte(lAddress + i);
				assertEquals((byte) i, lValue);
			}

			// Thread.sleep(10000);

			System.out.println("freeMemory");
			NativeMemoryAccess.freeMemory(lAddress);

			// System.out.println("end");
		}
		catch (InvalidNativeMemoryAccessException e)
		{
			e.printStackTrace();
			fail();
		}
	}

}
