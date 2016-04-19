package rtlib.core.variable.bounded.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rtlib.core.variable.bounded.BoundedNumber;

public class BoundedNumberTests
{

	@Test
	public void testBounds()
	{
		BoundedNumber<Double> lBoundedNumber = new BoundedNumber<Double>(	0.0,
																																			-1.0,
																																			+2.0);

		assertTrue(lBoundedNumber.doubleValue() == 0);
		assertTrue(lBoundedNumber.get() == 0.0);

		lBoundedNumber.set(3.0);

		assertTrue(lBoundedNumber.doubleValue() == 2);
		assertTrue(lBoundedNumber.get() == 2.0);

		lBoundedNumber.set(-2.0);

		assertTrue(lBoundedNumber.doubleValue() == -1);
		assertTrue(lBoundedNumber.get() == -1.0);
	}

	@Test
	public void testCrossType()
	{
		BoundedNumber<Long> lBoundedNumber = new BoundedNumber<Long>(	0L,
																																	-1L,
																																	+2L);

		assertTrue(lBoundedNumber.doubleValue() == 0);

		lBoundedNumber.set(3L);

		assertTrue(lBoundedNumber.doubleValue() == 2);

		lBoundedNumber.set(-2L);

		assertTrue(lBoundedNumber.doubleValue() == -1);
	}

}
