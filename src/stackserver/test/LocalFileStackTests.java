package stackserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import recycling.Recycler;
import stack.Stack;
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
		System.out.println(lRootFolder);

		{
			final LocalFileStackSink lLocalFileStackSink = new LocalFileStackSink(lRootFolder,
																																						"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSink.getMetaDataVariableBundle();

			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));

			final Stack lStack = new Stack(0, 0, 128, 128, 32, 2);

			assertEquals(	128 * 128 * 32 * 2,
										lStack.mNDimensionalArray.getArrayLength());
			System.out.println(lStack.mNDimensionalArray.getArrayLength() * 2);

			for (int i = 0; i < 10; i++)
			{
				assertTrue(lLocalFileStackSink.appendStack(lStack));
				lStack.mNDimensionalArray.add((byte) 1);
			}

			assertEquals(10, lLocalFileStackSink.getNumberOfStacks());

			lLocalFileStackSink.close();
		}

		{

			final LocalFileStackSource lLocalFileStackSource = new LocalFileStackSource(lRootFolder,
																																									"testSink");

			final Recycler<Stack> lStackRecycler = new Recycler<Stack>(Stack.class);
			lLocalFileStackSource.setStackRecycler(lStackRecycler);

			Stack lStack;

			lLocalFileStackSource.update();

			assertEquals(10, lLocalFileStackSource.getNumberOfStacks());

			for (int i = 0; i < 10; i++)
			{
				lStack = lLocalFileStackSource.getStack(i);
				final byte lValue = lStack.mNDimensionalArray.getAt(0, 0, 0);
				System.out.println(lValue);
				assertEquals(i, lValue);
			}

			final VariableBundle lVariableBundle = lLocalFileStackSource.getMetaDataVariableBundle();
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
