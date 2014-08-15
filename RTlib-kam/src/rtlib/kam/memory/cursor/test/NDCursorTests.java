package rtlib.kam.memory.cursor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.kam.memory.cursor.NDBoundedCursor;

public class NDCursorTests
{

	@Test
	public void testComputeMultipliers()
	{
		final long[] dim = new long[]
		{ 1, 3, 5, 7 };
		final long[] mult = new long[4];

		NDBoundedCursor.computeMultipliers(dim, mult);

		assertEquals(1, mult[0]);
		assertEquals(1, mult[1]);
		assertEquals(3, mult[2]);
		assertEquals(15, mult[3]);

		// System.out.println(Arrays.toString(mult));

	}

	@Test
	public void testGetArrayLength()
	{
		final long[] dim = new long[]
		{ 1, 3, 5, 7 };

		long length = NDBoundedCursor.getVolume(dim);

		assertEquals(1 * 3 * 5 * 7, length);

		// System.out.println(length);

	}

	@Test
	public void testGetFlatIndex()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.createNDVectorCursor(	3,
																																		5,
																																		7,
																																		11);

		long index = NDBoundedCursor.getIndex(lCursor.getDimensions(),
																					1,
																					2,
																					3,
																					4);

		assertEquals(1 + 3 * 2 + 3 * 5 * 3 + 3 * 5 * 7 * 4, index);

	}

	@Test
	public void test1D()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.create1DCursor(1000);

		for (int i = 0; i < 1000; i++)
		{
			long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();
			assertEquals(i, lCurrentFlatIndex);
			lCursor.incrementCursorPosition(1);
		}

		for (int i = 0; i < 1000; i++)
		{
			lCursor.incrementCursorPosition(1);
			long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();
			assertTrue(lCurrentFlatIndex == 1000 - 1);
		}

		for (int i = 0; i < 1000; i++)
		{
			long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();
			assertEquals(1000 - i - 1, lCurrentFlatIndex);
			lCursor.decrementCursorPosition(1);
		}

		for (int i = 0; i < 1000; i++)
		{
			lCursor.decrementCursorPosition(1);
			long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();
			assertTrue(lCurrentFlatIndex == 0);
		}

	}

	@Test
	public void test2D()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.create2DCursor(	1000,
																															1000);

		lCursor.setCursorPosition(1, 0);
		for (int x = 0; x < 1000; x++)
		{

			lCursor.setCursorPosition(2, 0);
			for (int y = 0; y < 1000; y++)
			{
				long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();

				/*System.out.println(String.format(	"(%d,%d) -> %d",
																					x,
																					y,
																					lCurrentFlatIndex));/**/

				assertEquals(x + 1000 * y, lCurrentFlatIndex);
				lCursor.incrementCursorPosition(2);
			}

			lCursor.incrementCursorPosition(1);
		}

	}

	@Test
	public void test3D()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.create3DCursor(	100,
																															100,
																															100);

		lCursor.setCursorPosition(1, 0);
		for (int x = 0; x < 100; x++)
		{

			lCursor.setCursorPosition(2, 0);
			for (int y = 0; y < 100; y++)
			{

				lCursor.setCursorPosition(3, 0);
				for (int z = 0; z < 100; z++)
				{

					long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();

					/*System.out.println(String.format(	"(%d,%d,%d) -> %d",
																						x,
																						y,
																						z,
																						lCurrentFlatIndex));/**/

					assertEquals(x + 100 * y + 100 * 100 * z, lCurrentFlatIndex);
					lCursor.incrementCursorPosition(3);
				}

				lCursor.incrementCursorPosition(2);
			}

			lCursor.incrementCursorPosition(1);
		}

	}

	@Test
	public void testNDArray()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.createNDVectorCursor(	1,
																																		100L,
																																		100L,
																																		100L);

		lCursor.setCursorPosition(1, 0);
		for (int x = 0; x < 100; x++)
		{

			lCursor.setCursorPosition(2, 0);
			for (int y = 0; y < 100; y++)
			{

				lCursor.setCursorPosition(3, 0);
				for (int z = 0; z < 100; z++)
				{

					long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();

					/*System.out.println(String.format(	"(%d,%d,%d) -> %d",
																						x,
																						y,
																						z,
																						lCurrentFlatIndex));/**/

					assertEquals(x + 100 * y + 100 * 100 * z, lCurrentFlatIndex);

					assertEquals(z, lCursor.getCursorPosition()[3]);
					assertEquals(z, lCursor.getCursorPosition(3));

					lCursor.incrementCursorPosition(3);
				}

				assertEquals(y, lCursor.getCursorPosition()[2]);
				assertEquals(y, lCursor.getCursorPosition(2));
				lCursor.incrementCursorPosition(2);
			}

			assertEquals(x, lCursor.getCursorPosition()[1]);
			assertEquals(x, lCursor.getCursorPosition(1));
			lCursor.incrementCursorPosition(1);
		}

	}

	@Test
	public void testNDVectorArray()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.createNDVectorCursor(	3,
																																		100,
																																		100,
																																		100);

		lCursor.setCursorPosition(0, 0);
		for (int v = 0; v < 3; v++)
		{
			lCursor.setCursorPosition(1, 0);
			for (int x = 0; x < 100; x++)
			{

				lCursor.setCursorPosition(2, 0);
				for (int y = 0; y < 100; y++)
				{

					lCursor.setCursorPosition(3, 0);
					for (int z = 0; z < 100; z++)
					{

						long lCurrentFlatIndex = lCursor.getCurrentFlatIndex();

						/*System.out.println(String.format(	"(%d,%d,%d) -> %d",
																							x,
																							y,
																							z,
																							lCurrentFlatIndex));/**/

						assertEquals(	v + 3 * x + 3 * 100 * y + 3 * 100 * 100 * z,
													lCurrentFlatIndex);

						assertEquals(z, lCursor.getCursorPosition()[3]);
						assertEquals(z, lCursor.getCursorPosition(3));

						lCursor.incrementCursorPosition(3);
					}

					assertEquals(y, lCursor.getCursorPosition()[2]);
					assertEquals(y, lCursor.getCursorPosition(2));
					lCursor.incrementCursorPosition(2);
				}

				assertEquals(x, lCursor.getCursorPosition()[1]);
				assertEquals(x, lCursor.getCursorPosition(1));
				lCursor.incrementCursorPosition(1);
			}

			assertEquals(v, lCursor.getCursorPosition()[0]);
			assertEquals(v, lCursor.getCursorPosition(0));
			lCursor.incrementCursorPosition(0);
		}
	}

	@Test
	public void testMinMaxPositions()
	{
		NDBoundedCursor lCursor = NDBoundedCursor.createNDVectorCursor(	3,
																																		100,
																																		100,
																																		100);

		for (int d = 1; d < 3; d++)
		{
			final long min = lCursor.getMinPosition(d);
			final long max = lCursor.getMaxPosition(d);
			final long length = lCursor.getSizeAlongDimension(d);

			assertEquals(0, min);
			assertEquals(99, max);
			assertEquals(length, length);
		}
	}

}
