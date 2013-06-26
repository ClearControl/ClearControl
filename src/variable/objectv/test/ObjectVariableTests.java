package variable.objectv.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import variable.objectv.ObjectVariable;

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
		assertTrue(x.getReference() == 0.0);
		assertTrue(y.getReference() == 0.0);

		x.setReference(1.0);
		assertTrue(x.getReference() == 1.0);
		assertTrue(y.getReference() == 1.0);

		y.setReference(2.0);
		assertTrue(x.getReference() == 2.0);
		assertTrue(y.getReference() == 2.0);

	}

}
