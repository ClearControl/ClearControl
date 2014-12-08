package rtlib.stack.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;
import rtlib.stack.StackRequest;
import rtlib.stack.server.LocalFileStackSink;
import rtlib.stack.server.LocalFileStackSource;
import coremem.recycling.Recycler;
import coremem.util.SizeOf;

public class LocalFileStackTests
{

	private static final int cBytesPerVoxel = SizeOf.sizeOf(short.class);
	private static final int cSizeZ = 2;
	private static final int cSizeY = 2;
	private static final int cSizeX = 2;
	private static final int cNumberOfStacks = 10;
	private static final int cMaximalNumberOfAvailableStacks = 20;

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
			final LocalFileStackSink<Character> lLocalFileStackSink = new LocalFileStackSink<Character>(lRootFolder,
																																																	"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSink.getMetaDataVariableBundle();

			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));

			final Stack<Character> lStack = new Stack<Character>(	0,
																														0,
																														char.class,
																														cSizeX,
																														cSizeY,
																														cSizeZ);

			assertEquals(	cSizeX * cSizeY * cSizeZ,
										lStack.getNDArray().getLengthInElements());
			// System.out.println(lStack.mNDimensionalArray.getLengthInElements() *
			// 2);

			assertEquals(	cSizeX * cSizeY * cSizeZ * cBytesPerVoxel,
										lStack.getNDArray().getSizeInBytes());

			for (int i = 0; i < cNumberOfStacks; i++)
			{
				for (int k = 0; k < cSizeX * cSizeY * cSizeZ * cBytesPerVoxel; k++)
				{
					lStack.getNDArray().setByteAligned(k, (byte) i);
					final byte lValue = lStack.getNDArray().getByteAligned(k);
					// System.out.println(lValue);
					assertEquals(i, lValue);
				}

				assertTrue(lLocalFileStackSink.appendStack(lStack));
			}

			assertEquals(	cNumberOfStacks,
										lLocalFileStackSink.getNumberOfStacks());

			lLocalFileStackSink.close();
		}

		{
			final Recycler<Stack<Character>, StackRequest<Character>> lStackRecycler = new Recycler<Stack<Character>, StackRequest<Character>>(	Stack.class,
																																																																					cMaximalNumberOfAvailableStacks);

			final LocalFileStackSource<Character> lLocalFileStackSource = new LocalFileStackSource<Character>(lStackRecycler,
																																																				lRootFolder,
																																																				"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSource.getMetaDataVariableBundle();
			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));
			final VariableInterface<Double> lVariable1 = lVariableBundle.getVariable("doublevar1");
			// System.out.println(lVariable1.get());
			assertEquals(312, lVariable1.get(), 0.5);

			final VariableInterface<String> lVariable2 = lVariableBundle.getVariable("stringvar1");
			// System.out.println(lVariable2.get());
			assertEquals("123", lVariable2.get());

			Stack<Character> lStack;

			lLocalFileStackSource.update();

			assertEquals(	cNumberOfStacks,
										lLocalFileStackSource.getNumberOfStacks());

			for (int i = 0; i < cNumberOfStacks; i++)
			{
				lStack = lLocalFileStackSource.getStack(i);
				for (int k = 0; k < cSizeX * cSizeY * cSizeZ * cBytesPerVoxel; k++)
				{
					final byte lValue = lStack.getNDArray().getByteAligned(k);
					// System.out.println("value=" + lValue);
					assertEquals(i, lValue);
				}
			}

			lLocalFileStackSource.close();
		}

	}
}
