package clearcontrol.core.variable.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import clearcontrol.core.variable.Variable;

public class ObjectVariableTests
{

	@Test
	public void DoubleVariableTest()
	{
		final Variable<Double> x = new Variable<Double>("x", 0.0);
		final Variable<Double> y = new Variable<Double>("y", 0.0);

		x.syncWith(y);
		assertEquals(new Double(0.0), x.get());
		assertEquals(new Double(0.0), y.get());

		x.set(1.0);
		assertEquals(new Double(1.0), x.get());
		assertEquals(new Double(1.0), y.get());

		y.set(2.0);
		assertEquals(new Double(2.0), x.get());
		assertEquals(new Double(2.0), y.get());

		final Variable<Double> z = new Variable<Double>("y", 0.0);

		z.sendUpdatesTo(x);

		y.set(3.0);
		assertEquals(new Double(3.0), x.get());
		assertEquals(new Double(3.0), y.get());
		assertEquals(new Double(0.0), z.get());

		z.set(4.0);
		assertEquals(new Double(4.0), x.get());
		assertEquals(new Double(4.0), y.get());
		assertEquals(new Double(4.0), z.get());

	}

}
