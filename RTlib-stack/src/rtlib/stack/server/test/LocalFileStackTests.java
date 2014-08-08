package rtlib.stack.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import rtlib.core.memory.SizeOf;
import rtlib.core.recycling.Recycler;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.Stack;
import rtlib.stack.server.LocalFileStackSink;
import rtlib.stack.server.LocalFileStackSource;

public class LocalFileStackTests
{

	private static final int cBytesPerVoxel = SizeOf.sizeOf(short.class);
	private static final int cSizeZ = 2;
	private static final int cSizeY = 2;
	private static final int cSizeX = 2;
	private static final int cNumberOfStacks = 10;

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
			final LocalFileStackSink<Short> lLocalFileStackSink = new LocalFileStackSink<Short>(lRootFolder,
																																						"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSink.getMetaDataVariableBundle();

			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));

			final Stack<Short> lStack = new Stack<Short>(	0,
																			0,
																			cSizeX,
																			cSizeY,
																			cSizeZ,
																			short.class);

			assertEquals(	cSizeX * cSizeY * cSizeZ * cBytesPerVoxel,
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

			final LocalFileStackSource<Short> lLocalFileStackSource = new LocalFileStackSource<Short>(lRootFolder,
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

			@SuppressWarnings("rawtypes")
			final Recycler<Stack<Short>, Long> lStackRecycler = new Recycler(Stack.class);
			lLocalFileStackSource.setStackRecycler(lStackRecycler);

			Stack<Short> lStack;

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
