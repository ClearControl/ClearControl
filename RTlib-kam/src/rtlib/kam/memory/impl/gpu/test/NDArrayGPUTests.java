package rtlib.kam.memory.impl.gpu.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.kam.context.impl.gpu.ContextGPU;
import rtlib.kam.memory.impl.gpu.NDArrayGPU;
import coremem.offheap.NativeMemoryAccess;
import coremem.util.SizeOf;

public class NDArrayGPUTests
{
	private static final long cSizeX = 128;
	private static final long cSizeY = 128 + 1;
	private static final long cSizeZ = 128 + 3;

	private static final long cVolume = cSizeX * cSizeY * cSizeZ;

	@Test
	public void testLifeCycle()
	{
		ContextGPU lBestOpenCLContext = ContextGPU.getBestOpenCLContext();

		NDArrayGPU<Short> lNDArrayGPU = new NDArrayGPU<Short>(lBestOpenCLContext,
																													short.class,
																													true,
																													true,
																													1,
																													cSizeX,
																													cSizeY,
																													cSizeZ);

		assertEquals(cSizeX, lNDArrayGPU.getWidth());
		assertEquals(cSizeY, lNDArrayGPU.getHeight());
		assertEquals(cSizeZ, lNDArrayGPU.getDepth());

		assertEquals(cVolume, lNDArrayGPU.getVolume());

		assertEquals(cVolume, lNDArrayGPU.getLengthInElements());

		assertEquals(	cSizeX * cSizeY * cSizeZ * SizeOf.sizeOfShort(),
									lNDArrayGPU.getSizeInBytes());

		assertFalse(lNDArrayGPU.isFree());

		long lMapAddress = lNDArrayGPU.map();

		for (int i = 0; i < cSizeX * cSizeY * cSizeZ; i++)
		{
			NativeMemoryAccess.setShort(lMapAddress + 2 * i, (short) (i));

			short lShort = NativeMemoryAccess.getShort(lMapAddress + 2 * i);
			assertEquals((short) i, lShort);
		}

		assertTrue(lNDArrayGPU.isCurrentlyMapped());

		lNDArrayGPU.unmap();

		assertFalse(lNDArrayGPU.isCurrentlyMapped());

		lNDArrayGPU.free();

		assertTrue(lNDArrayGPU.isFree());
	}

}
