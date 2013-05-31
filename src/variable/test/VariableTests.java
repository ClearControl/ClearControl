package variable.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import variable.doublev.DoubleVariable;

public class VariableTests
{

	@Test
	public void DoubleVariableTest()
	{
		final DoubleVariable x = new DoubleVariable(0);
		final DoubleVariable y = new DoubleVariable(0);

		x.syncWith(y);
		assertTrue(x.getValue() == 0);
		assertTrue(y.getValue() == 0);

		x.setValue(1);
		assertTrue(x.getValue() == 1);
		assertTrue(y.getValue() == 1);

		y.setValue(2);
		assertTrue(x.getValue() == 2);
		assertTrue(y.getValue() == 2);

	}

}
