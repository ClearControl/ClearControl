package clearcontrol.core.variable.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.core.variable.Variable;

public class ObjectVariableTests implements
																AsynchronousExecutorServiceAccess
{

	@Test
	public void testDoubleVariable()
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

	@Test
	public void testWaitForEquals()
	{
		final Variable<Double> x = new Variable<Double>("x", 0.0);

		executeAsynchronously(() -> {
			System.out.println("wating for value");
			try
			{
				assertFalse(x.get().equals(1.0));
				x.waitForEqualsTo(1.0, 10, TimeUnit.SECONDS);
				assertTrue(x.get().equals(1.0));
				System.out.println("value reached");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			
		});

		ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);

		assertFalse(x.get().equals(1.0));
		System.out.println("Setting Value");
		x.set(1.0);
		assertTrue(x.get().equals(1.0));
		
		
		ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);

	}
	
	@Test
	public void testWaitForSame()
	{
		Double lValue = 0.3;
		
		final Variable<Double> x = new Variable<Double>("x", 0.0);

		executeAsynchronously(() -> {
			System.out.println("wating for value");
			try
			{
				assertFalse(x.get().equals(lValue));
				assertFalse(x.waitForSameAs(0.0, 1, TimeUnit.MILLISECONDS));
				assertFalse(x.get().equals(lValue));
				x.waitForSameAs(lValue, 10, TimeUnit.SECONDS);
				assertTrue(x.get().equals(lValue));
				System.out.println("value reached");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
			
		});

		ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);

		assertFalse(x.get().equals(lValue));
		System.out.println("Setting Value");
		x.set(lValue);
		assertTrue(x.get().equals(lValue));
		
		
		ThreadUtils.sleep(100, TimeUnit.MILLISECONDS);

	}
}
