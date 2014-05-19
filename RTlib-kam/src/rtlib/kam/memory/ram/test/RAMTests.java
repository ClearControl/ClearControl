package rtlib.kam.memory.ram.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import rtlib.core.memory.NativeMemoryAccess;
import rtlib.kam.memory.Copyable;
import rtlib.kam.memory.MappableMemory;
import rtlib.kam.memory.MemoryType;
import rtlib.kam.memory.RangeCopyable;
import rtlib.kam.memory.ram.RAM;
import rtlib.kam.memory.ram.Resizable;

public class RAMTests
{

	public static void testBasics(RAM pRAM,
																MemoryType pMemoryType,
																final boolean pResize)
	{
		long lLength = pRAM.getSizeInBytes();

		if (pRAM instanceof MappableMemory)
			((MappableMemory) pRAM).map();

		assertTrue(pRAM.getAddress() != 0L);

		assertTrue(pRAM.getMemoryType() == pMemoryType);

		assertFalse(pRAM.isFree());

		if (pResize)
		{
			if (pRAM instanceof Resizable)
				((Resizable) pRAM).resize(lLength / 2);

			assertTrue(pRAM.getSizeInBytes() == lLength / 2);

			if (pRAM instanceof Resizable)
				((Resizable) pRAM).resize(lLength);

			assertTrue(pRAM.getSizeInBytes() == lLength);
		}

		if (pRAM instanceof MappableMemory)
			((MappableMemory) pRAM).unmap();

		pRAM.free();

	}

	@SuppressWarnings("unchecked")
	public static void testCopySameSize(RAM pRAM1, RAM pRAM2)
	{
		if (pRAM1 instanceof Copyable<?>)
		{
			if (pRAM1 instanceof MappableMemory)
				((MappableMemory) pRAM1).map();
			if (pRAM2 instanceof MappableMemory)
				((MappableMemory) pRAM2).map();

			// RAMDirect lRAMDirect1 = new RAMDirect(1L * Integer.MAX_VALUE);
			// RAMDirect lRAMDirect2 = new RAMDirect(1L * Integer.MAX_VALUE);
			// System.out.println(lRAMDirect1.getLength() / (1024 * 1024));

			NativeMemoryAccess.setByte(pRAM1.getAddress(), (byte) 123);

			NativeMemoryAccess.setByte(	pRAM1.getAddress() + pRAM1.getSizeInBytes()
																			/ 2,
																	(byte) 456);

			((Copyable<RAM>) pRAM1).copyTo(pRAM2);

			assertEquals(	(byte) 123,
										NativeMemoryAccess.getByte(pRAM2.getAddress()));
			assertEquals(	(byte) 456,
										NativeMemoryAccess.getByte(pRAM2.getAddress() + pRAM1.getSizeInBytes()
																								/ 2));

			pRAM1.free();
			pRAM2.free();

			try
			{
				((Copyable<RAM>) pRAM1).copyTo(pRAM2);
				fail();
			}
			catch (Throwable e)
			{
			}

			if (pRAM1 instanceof MappableMemory)
				((MappableMemory) pRAM1).unmap();
			if (pRAM2 instanceof MappableMemory)
				((MappableMemory) pRAM2).unmap();
		}
	}

	@SuppressWarnings("unchecked")
	public static void testCopyDifferentSize(RAM pRAM1, RAM pRAM2)
	{
		// RAMDirect lRAMDirect1 = new RAMDirect(4);
		// RAMDirect lRAMDirect2 = new RAMDirect(8);
		// System.out.println(lRAMDirect1.getLength() / (1024 * 1024));

		if (pRAM1 instanceof MappableMemory)
			((MappableMemory) pRAM1).map();
		if (pRAM2 instanceof MappableMemory)
			((MappableMemory) pRAM2).map();

		NativeMemoryAccess.setByte(pRAM1.getAddress() + 2, (byte) 123);

		NativeMemoryAccess.setByte(pRAM1.getAddress() + 3, (byte) 111);

		if (pRAM1 instanceof Copyable<?>)
		{
			((RangeCopyable<RAM>) pRAM1).copyRangeTo(2, pRAM2, 4, 2);

			assertEquals(	(byte) 123,
										NativeMemoryAccess.getByte(pRAM2.getAddress() + 4));
			assertEquals(	(byte) 111,
										NativeMemoryAccess.getByte(pRAM2.getAddress() + 5));

			pRAM1.free();
			pRAM2.free();

			try
			{
				((RangeCopyable<RAM>) pRAM1).copyRangeTo(2, pRAM2, 4, 2);
				fail();
			}
			catch (Throwable e)
			{
			}
		}

		if (pRAM1 instanceof MappableMemory)
			((MappableMemory) pRAM1).unmap();
		if (pRAM2 instanceof MappableMemory)
			((MappableMemory) pRAM2).unmap();
	}

	@SuppressWarnings("unchecked")
	public static void testCopyChecks(RAM pRAM1, RAM pRAM2)
	{
		// RAMDirect lRAMDirect1 = new RAMDirect(4);
		// RAMDirect lRAMDirect2 = new RAMDirect(8);
		if (pRAM1 instanceof Copyable<?>)
		{
			try
			{
				((RangeCopyable<RAM>) pRAM1).copyRangeTo(2, pRAM2, 4, 3);
				fail();
			}
			catch (Throwable e1)
			{
			}

			try
			{
				((RangeCopyable<RAM>) pRAM1).copyRangeTo(2, pRAM2, 4, -2);
				fail();
			}
			catch (Throwable e1)
			{
			}

			try
			{
				((RangeCopyable<RAM>) pRAM1).copyRangeTo(2, pRAM1, 4, 9);
				fail();
			}
			catch (Throwable e1)
			{
			}

			pRAM1.free();
			pRAM2.free();

			try
			{
				((RangeCopyable<RAM>) pRAM1).copyRangeTo(2, pRAM2, 4, 2);
				fail();
			}
			catch (Throwable e)
			{
			}
		}
	}

	public static void testWriteRead(RAM pRAM)
	{
		// RAMDirect lRAMDirect = new RAMDirect(4);

		if (pRAM instanceof MappableMemory)
			((MappableMemory) pRAM).map();

		pRAM.setByteAligned(0, (byte) 255);
		pRAM.setByteAligned(1, (byte) 255);
		pRAM.setByteAligned(2, (byte) 255);
		pRAM.setByteAligned(3, (byte) 255);

		assertEquals(Character.MAX_VALUE, pRAM.getCharAligned(0));
		assertEquals(-1, pRAM.getShortAligned(0));
		assertEquals(-1, pRAM.getIntAligned(0));

		assertEquals(Double.NaN, pRAM.getFloatAligned(0), 0);

		if (pRAM instanceof MappableMemory)
			((MappableMemory) pRAM).unmap();
	}

}
