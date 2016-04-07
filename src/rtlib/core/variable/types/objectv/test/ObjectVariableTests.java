package rtlib.core.variable.types.objectv.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rtlib.core.variable.types.objectv.ObjectVariable;

public class ObjectVariableTests
{

	@Test
	public void DoubleVariableTest()
	{
		final ObjectVariable<Double> x = new ObjectVariable<Double>("x",
																	0.0);
		final ObjectVariable<Double> y = new ObjectVariable<Double>("y",
																	0.0);

		x.syncWith(y);
		assertEquals(new Double(0.0), x.get());
		assertEquals(new Double(0.0), y.get());

		x.set(1.0);
		assertEquals(new Double(1.0), x.get());
		assertEquals(new Double(1.0), y.get());

		y.set(2.0);
		assertEquals(new Double(2.0), x.get());
		assertEquals(new Double(2.0), y.get());

	}

}
