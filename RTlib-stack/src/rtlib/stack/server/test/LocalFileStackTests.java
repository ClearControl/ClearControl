package rtlib.stack.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.imglib2.Cursor;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.PlanarCursor;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.junit.Test;

import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.core.variable.doublev.DoubleVariable;
import rtlib.core.variable.objectv.ObjectVariable;
import rtlib.stack.FragmentedOffHeapPlanarStackFactory;
import rtlib.stack.OffHeapPlanarStack;
import rtlib.stack.StackInterface;
import rtlib.stack.StackRequest;
import rtlib.stack.server.LocalFileStackSink;
import rtlib.stack.server.LocalFileStackSource;
import coremem.recycling.BasicRecycler;

public class LocalFileStackTests
{

	private static final int cBytesPerVoxel = 2;
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
			final LocalFileStackSink<UnsignedShortType, ShortOffHeapAccess> lLocalFileStackSink = new LocalFileStackSink<UnsignedShortType, ShortOffHeapAccess>(new UnsignedShortType(),
																																																																													lRootFolder,
																																																																													"testSink");

			final VariableBundle lVariableBundle = lLocalFileStackSink.getMetaDataVariableBundle();

			lVariableBundle.addVariable(new DoubleVariable(	"doublevar1",
																											312));
			lVariableBundle.addVariable(new ObjectVariable<String>(	"stringvar1",
																															"123"));


			@SuppressWarnings("unchecked")
			final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> lStack = (OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>) OffHeapPlanarStack.createStack(new UnsignedShortType(),
																																																																																					cSizeX,
																																																																																					cSizeY,
																																																																																					cSizeZ);

			assertEquals(	cSizeX * cSizeY * cSizeZ,
										lStack.getNumberOfVoxels());
			// System.out.println(lStack.mNDimensionalArray.getLengthInElements() *
			// 2);

			assertEquals(	cSizeX * cSizeY * cSizeZ * cBytesPerVoxel,
										lStack.getSizeInBytes());

			for (int i = 0; i < cNumberOfStacks; i++)
			{

				final PlanarCursor<UnsignedShortType> lCursor = lStack.getPlanarImage()
																															.cursor();

				while (lCursor.hasNext())
				{
					final UnsignedShortType lUnsignedShortType = lCursor.next();
					lUnsignedShortType.set(i);
				}

				lCursor.reset();

				while (lCursor.hasNext())
				{
					final UnsignedShortType lUnsignedShortType = lCursor.next();
					assertEquals(i & 0xFFFF, lUnsignedShortType.get());
				}

				assertTrue(lLocalFileStackSink.appendStack(lStack));
			}

			assertEquals(	cNumberOfStacks,
										lLocalFileStackSink.getNumberOfStacks());

			lLocalFileStackSink.close();
		}

		{
			final FragmentedOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStackFactory = new FragmentedOffHeapPlanarStackFactory<UnsignedShortType, ShortOffHeapAccess>();

			final BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>> lStackRecycler = new BasicRecycler<StackInterface<UnsignedShortType, ShortOffHeapAccess>, StackRequest<UnsignedShortType>>(	lOffHeapPlanarStackFactory,
																																																																																																																							cMaximalNumberOfAvailableStacks);

			final LocalFileStackSource<UnsignedShortType, ShortOffHeapAccess> lLocalFileStackSource = new LocalFileStackSource<UnsignedShortType, ShortOffHeapAccess>(new UnsignedShortType(),
																																																																																lStackRecycler,
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

			StackInterface<UnsignedShortType, ShortOffHeapAccess> lStack;

			lLocalFileStackSource.update();

			assertEquals(	cNumberOfStacks,
										lLocalFileStackSource.getNumberOfStacks());

			for (int i = 0; i < cNumberOfStacks; i++)
			{
				lStack = lLocalFileStackSource.getStack(i);
				final Cursor<UnsignedShortType> lCursor = lStack.getImage()
																												.cursor();

				while (lCursor.hasNext())
				{
					final UnsignedShortType lValue = lCursor.next();
					// System.out.println("value=" + lValue);
					assertEquals(i, lValue.get());
				}
			}

			lLocalFileStackSource.close();
		}

	}
}
