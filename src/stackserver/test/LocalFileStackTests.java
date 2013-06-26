package stackserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import ndarray.implementations.heapbuffer.directbuffer.NDArrayDirectBufferShort;

import org.junit.Test;

import stackserver.LocalFileStackSink;
import stackserver.LocalFileStackSource;
import variable.VariableInterface;
import variable.bundle.VariableBundle;
import variable.doublev.DoubleVariable;
import variable.objectv.ObjectVariable;

public class LocalFileStackTests
{

	@Test
	public void testSinkAndSource() throws IOException
	{

		final File lRootFolder = new File(File.createTempFile("test",
																													"test")
																					.getParentFile(),
																			"LocalFileStackTests" + Math.random());
		lRootFolder.mkdirs();

		{
			final LocalFileStackSink lLocalFileStackSink = new LocalFileStackSink(lRootFolder,
																																						"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSink.getVariableBundle();

			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));

			final NDArrayDirectBufferShort lNDArrayDirectBufferShort = NDArrayDirectBufferShort.allocateXYZ(128,
																																																			128,
																																																			32);

			assertEquals(	128 * 128 * 32,
										lNDArrayDirectBufferShort.getArrayLength());
			System.out.println(lNDArrayDirectBufferShort.getArrayLength() * 2);

			for (int i = 0; i < 10; i++)
			{
				assertTrue(lLocalFileStackSink.appendStack(	System.nanoTime(),
																										lNDArrayDirectBufferShort));
				lNDArrayDirectBufferShort.add((short) 1);
			}

			assertEquals(10, lLocalFileStackSink.getNumberOfStacks());

			lLocalFileStackSink.close();
		}

		{

			final LocalFileStackSource lLocalFileStackSource = new LocalFileStackSource(lRootFolder,
																																									"testSink");

			final NDArrayDirectBufferShort lNDArrayDirectBufferShort = new NDArrayDirectBufferShort(128,
																																															128,
																																															32);

			lLocalFileStackSource.update();

			assertEquals(10, lLocalFileStackSource.getNumberOfStacks());

			for (int i = 0; i < 10; i++)
			{
				lLocalFileStackSource.getStack(i, lNDArrayDirectBufferShort);
				final short lValue = lNDArrayDirectBufferShort.getAt(0, 0, 0);
				System.out.println(lValue);
				assertEquals(i, lValue);
			}

			final VariableBundle lVariableBundle = lLocalFileStackSource.getVariableBundle();
			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));
			final VariableInterface<Double> lVariable1 = lVariableBundle.getVariable("doublevar1");
			System.out.println(lVariable1.get());
			assertEquals(312, lVariable1.get(), 0.5);

			final VariableInterface<String> lVariable2 = lVariableBundle.getVariable("stringvar1");
			System.out.println(lVariable2.get());
			assertEquals("123", lVariable2.get());

			lLocalFileStackSource.close();
		}

	}
}
