package clearcontrol.imglib2.demo;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import coremem.fragmented.FragmentedMemory;
import coremem.offheap.OffHeapMemory;
import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.offheap.ByteOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class BasicImagLib2Demo
{

	@Test
	public void testByte()
	{

		final long[] lDim = new long[]
		{ 128, 128, 128 };

		final FragmentedMemory lFragmentedMemory = new FragmentedMemory();
		for (int z = 0; z < 128; z++)
		{
			final OffHeapMemory lAllocate = OffHeapMemory.allocateBytes(128 * 128);
			lFragmentedMemory.add(lAllocate);
		}

		final OffHeapPlanarImgFactory<ByteType> lFactory = new OffHeapPlanarImgFactory<ByteType>();

		final NativeImg<ByteType, ByteOffHeapAccess> lOffHeapPlanarImg = lFactory.createByteInstance(	lFragmentedMemory,
																																																	lDim,
																																																	new ByteType());

		final Cursor<ByteType> lCursor = lOffHeapPlanarImg.cursor();

		while (lCursor.hasNext())
		{
			final ByteType var = lCursor.next();
			var.set((byte) 171);
		}/**/

		final IntervalView<ByteType> lHyperSlice = Views.hyperSlice(lOffHeapPlanarImg,
																																1,
																																100);

		final double lSum = 0;
		final Cursor<ByteType> lCursor2 = lHyperSlice.cursor();
		while (lCursor2.hasNext())
		{
			final ByteType var = lCursor2.next();
			final byte lValue = var.get();
			if (lValue != (byte) 171)
				System.out.println(lValue);
			assertTrue(lValue == (byte) 171);
		}

		System.out.println(lSum);
	}

	@Test
	public void testUnsignedShort() throws InterruptedException
	{

		final long[] lDim = new long[]
		{ 128, 128, 128 };

		final FragmentedMemory lFragmentedMemory = new FragmentedMemory();
		for (int z = 0; z < 128; z++)
		{
			final OffHeapMemory lAllocate = OffHeapMemory.allocateShorts(lDim[0] * lDim[1]
																																		* 2);
			lFragmentedMemory.add(lAllocate);
		}

		final OffHeapPlanarImgFactory<UnsignedShortType> lFactory = new OffHeapPlanarImgFactory<UnsignedShortType>();

		final NativeImg<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarImg = lFactory.createShortInstance(lFragmentedMemory,
																																																						lDim,
																																																						new UnsignedShortType());

		final Cursor<UnsignedShortType> lCursor = lOffHeapPlanarImg.cursor();

		final char i = 0;
		while (lCursor.hasNext())
		{
			final UnsignedShortType var = lCursor.next();
			// final char lValue = i++;
			var.set(0);
		}/**/

		final IntervalView<UnsignedShortType> lHyperSlice = Views.hyperSlice(	lOffHeapPlanarImg,
																																					1,
																																					lDim[2] / 2);

		final double lSum = 0;
		final Cursor<UnsignedShortType> lCursor2 = lHyperSlice.cursor();
		while (lCursor2.hasNext())
		{
			final UnsignedShortType var = lCursor2.next();
			var.set(100);
		}

		try
		{
			new ImageJ();

			ImagePlus lShow = ImageJFunctions.show(lOffHeapPlanarImg);
			lShow.setDisplayRange(-100, 100);
			// Thread.sleep(6000);

			final RandomAccessible<UnsignedShortType> infiniteImg = Views.extendValue(lOffHeapPlanarImg,
																																								new UnsignedShortType());

			Gauss3.gauss(3, infiniteImg, lOffHeapPlanarImg);

			lShow = ImageJFunctions.show(lOffHeapPlanarImg);
			lShow.setDisplayRange(-100, 100);
			Thread.sleep(1000000);
		}
		catch (final IncompatibleTypeException e)
		{
			e.printStackTrace();
		}

		System.out.println(lSum);
	}
}
