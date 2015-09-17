package rtlib.microscope.lsm.adaptation.utils.test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rtlib.microscope.lsm.adaptation.utils.NDIterator;

public class NDIteratorTests
{

	@Test
	public void test()
	{
		NDIterator lNDIterator = new NDIterator(2, 3, 5);

		int i = 0;
		int[] lNext = null;
		while (lNDIterator.hasNext())
		{
			lNext = lNDIterator.next();

			System.out.println("i=" + i
								+ " -> "
								+ Arrays.toString(lNext));

			if (i == 0)
				assertArrayEquals(lNext, new int[]
				{ 0, 0, 0 });
			if (i == 10)
				assertArrayEquals(lNext, new int[]
				{ 0, 2, 1 });
			if (i == 20)
				assertArrayEquals(lNext, new int[]
				{ 0, 1, 3 });
			if (i == 29)
				assertArrayEquals(lNext, new int[]
				{ 1, 2, 4 });

			i++;  
		}

		assertEquals(i, lNDIterator.getNumberOfIterations()); 
	}

}
