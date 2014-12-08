package rtlib.kam.memory.ndarray.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.ndarray.InvalidNDArrayDefinitionException;
import rtlib.kam.memory.ndarray.NDArrayTyped;
import coremem.MemoryRegionInterface;
import coremem.offheap.OffHeapMemoryRegion;
import coremem.util.SizeOf;


public class TypedNDArrayTests
{

	@Test
	public void testBasics()
	{
		NDBoundedCursor lNDCursor = NDBoundedCursor.create3DCursor(	3,
																																5,
																																7);
		MemoryRegionInterface lMemoryRegionInterface = new OffHeapMemoryRegion(lNDCursor.getLengthInElements() * SizeOf.sizeOfShort());

		NDArrayTyped<Short> lNDArray = NDArrayTyped.allocateNDArray(lMemoryRegionInterface,
																																short.class,
																																lNDCursor);

		assertEquals(lNDCursor, lNDArray.getDefaultCursor());

		assertEquals(3, lNDArray.getDimension());

		assertEquals(3, lNDArray.getSizeAlongDimension(1));
		assertEquals(5, lNDArray.getSizeAlongDimension(2));
		assertEquals(7, lNDArray.getSizeAlongDimension(3));

		assertEquals(3 * 5 * 7, lNDArray.getVolume());

		assertEquals(3 * 5 * 7, lNDArray.getLengthInElements());

		assertFalse(lNDArray.isVectorized());

		try
		{
			NDBoundedCursor lNDCursorBad = NDBoundedCursor.create3DCursor(3,
																																		5,
																																		7);
			MemoryRegionInterface lMemoryRegionInterfaceBad = new OffHeapMemoryRegion(lNDCursor.getLengthInElements() - 10);
			NDArrayTyped<Short> lNDArrayBad = NDArrayTyped.allocateNDArray(	lMemoryRegionInterfaceBad,
																																			short.class,
																																			lNDCursorBad);
			fail();
		}
		catch (InvalidNDArrayDefinitionException e)
		{

		}
		catch (Throwable e)
		{
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void test3D()
	{
		NDBoundedCursor lNDCursor = NDBoundedCursor.create3DCursor(	3,
																																5,
																																7);
		MemoryRegionInterface lMemoryRegionInterface = new OffHeapMemoryRegion(lNDCursor.getLengthInElements() * SizeOf.sizeOfShort());

		NDArrayTyped<Short> lNDArray = NDArrayTyped.allocateNDArray(lMemoryRegionInterface,
																																short.class,
																																lNDCursor);

		assertEquals(lNDCursor, lNDArray.getDefaultCursor());

		assertEquals(3, lNDArray.getDimension());

		assertEquals(3, lNDArray.getSizeAlongDimension(1));
		assertEquals(5, lNDArray.getSizeAlongDimension(2));
		assertEquals(7, lNDArray.getSizeAlongDimension(3));

		assertEquals(3 * 5 * 7, lNDArray.getVolume());

		assertEquals(3 * 5 * 7, lNDArray.getLengthInElements());

		assertFalse(lNDArray.isVectorized());

		lNDCursor.setCursorPosition(1, 0);
		for (int x = 0; x < lNDArray.getSizeAlongDimension(1); x++)
		{

			lNDCursor.setCursorPosition(2, 0);
			for (int y = 0; y < lNDArray.getSizeAlongDimension(2); y++)
			{

				lNDCursor.setCursorPosition(3, 0);
				for (int z = 0; z < lNDArray.getSizeAlongDimension(3); z++)
				{
					lNDArray.setShortAtCursor((byte) 123);
					lNDCursor.incrementCursorPosition(3);
				}

				lNDCursor.incrementCursorPosition(2);
			}

			lNDCursor.incrementCursorPosition(1);
		}

		for (int i = 0; i < lNDCursor.getLengthInElements(); i++)
			assertEquals((short) (123), lMemoryRegionInterface.getShortAligned(i));

	}

}
