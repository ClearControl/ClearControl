package rtlib.kam.memory.ndarray.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import rtlib.kam.memory.cursor.NDBoundedCursor;
import rtlib.kam.memory.impl.direct.RAMDirect;
import rtlib.kam.memory.ndarray.InvalidNDArrayDefinitionException;
import rtlib.kam.memory.ndarray.NDArray;
import rtlib.kam.memory.ram.RAM;

public class NDArrayTests
{

	@Test
	public void testBasics()
	{
		NDBoundedCursor lNDCursor = NDBoundedCursor.create3DCursor(	3,
																																5,
																																7);
		RAM lRAM = new RAMDirect(lNDCursor.getLengthInElements());

		NDArray lNDArray = NDArray.wrap(lRAM, lNDCursor);

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
			RAM lRAMBad = new RAMDirect(lNDCursor.getLengthInElements() - 10);
			NDArray lNDArrayBad = NDArray.wrap(lRAMBad, lNDCursorBad);
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
		RAM lRAM = new RAMDirect(lNDCursor.getLengthInElements());

		NDArray lNDArray = NDArray.wrap(lRAM, lNDCursor);

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
					lNDArray.setByteAtCursor((byte) 123);
					lNDCursor.incrementCursorPosition(3);
				}

				lNDCursor.incrementCursorPosition(2);
			}

			lNDCursor.incrementCursorPosition(1);
		}

		for (int i = 0; i < lRAM.getSizeInBytes(); i++)
			assertEquals((byte) (123), lRAM.getByteAligned(i));

	}

	@Test
	public void test3DArrayVector()
	{
		NDBoundedCursor lNDCursor = NDBoundedCursor.createNDVectorCursor(	4,
																																			3,
																																			5,
																																			7);
		RAM lRAM = new RAMDirect(lNDCursor.getLengthInElements());

		NDArray lNDArray = NDArray.wrap(lRAM, lNDCursor);

		assertEquals(lNDCursor, lNDArray.getDefaultCursor());

		assertEquals(3, lNDArray.getDimension());

		assertEquals(3, lNDArray.getSizeAlongDimension(1));
		assertEquals(5, lNDArray.getSizeAlongDimension(2));
		assertEquals(7, lNDArray.getSizeAlongDimension(3));

		assertEquals(3 * 5 * 7, lNDArray.getVolume());

		assertEquals(4 * 3 * 5 * 7, lNDArray.getLengthInElements());

		assertTrue(lNDArray.isVectorized());

		lNDCursor.setCursorPosition(1, 0);

		lNDArray.setDoubleAtCursor(234.567);
		assertEquals(234.567, lNDArray.getDoubleAtCursor(lNDCursor), 0);/**/
		lNDArray.setDoubleAtCursor(lNDCursor, 234.567);
		assertEquals(234.567, lNDArray.getDoubleAtCursor(), 0);/**/

		lNDArray.setLongAtCursor(123456789123456789L);
		assertEquals(	123456789123456789L,
									lNDArray.getLongAtCursor(lNDCursor),
									0);/**/
		lNDArray.setLongAtCursor(lNDCursor, 123456789123456789L);
		assertEquals(123456789123456789L, lNDArray.getLongAtCursor(), 0);/**/

		for (int x = 0; x < lNDArray.getSizeAlongDimension(1); x++)
		{

			lNDCursor.setCursorPosition(2, 0);
			for (int y = 0; y < lNDArray.getSizeAlongDimension(2); y++)
			{

				lNDCursor.setCursorPosition(3, 0);
				for (int z = 0; z < lNDArray.getSizeAlongDimension(3); z++)
				{

					lNDArray.setByteAtCursor((byte) 1234);
					assertEquals(	(byte) 1234,
												lNDArray.getByteAtCursor(lNDCursor));
					lNDArray.setByteAtCursor(lNDCursor, (byte) 1234);
					assertEquals((byte) 1234, lNDArray.getByteAtCursor());

					lNDArray.setCharAtCursor((char) 1234);
					assertEquals(	(char) 1234,
												lNDArray.getCharAtCursor(lNDCursor));
					lNDArray.setCharAtCursor(lNDCursor, (char) 1234);
					assertEquals((char) 1234, lNDArray.getCharAtCursor());

					lNDArray.setShortAtCursor((short) -1234);
					assertEquals(	(short) -1234,
												lNDArray.getShortAtCursor(lNDCursor));
					lNDArray.setShortAtCursor(lNDCursor, (short) -1234);
					assertEquals((short) -1234, lNDArray.getShortAtCursor());

					lNDArray.setIntAtCursor(234567);
					assertEquals(234567, lNDArray.getIntAtCursor(lNDCursor));
					lNDArray.setIntAtCursor(lNDCursor, 234567);
					assertEquals(234567, lNDArray.getIntAtCursor());

					lNDArray.setFloatAtCursor(234.567f);
					assertEquals(	234.567f,
												lNDArray.getFloatAtCursor(lNDCursor),
												0);
					lNDArray.setFloatAtCursor(lNDCursor, 234.567f);
					assertEquals(234.567f, lNDArray.getFloatAtCursor(), 0);

					lNDArray.setIntAtCursor(1234567890);

					lNDCursor.incrementCursorPosition(3);
				}

				lNDCursor.incrementCursorPosition(2);
			}

			lNDCursor.incrementCursorPosition(1);
		}

		for (int i = 0; i < lNDCursor.getVolume(); i++)
			assertEquals(1234567890, lRAM.getIntAligned(i));

	}

}
