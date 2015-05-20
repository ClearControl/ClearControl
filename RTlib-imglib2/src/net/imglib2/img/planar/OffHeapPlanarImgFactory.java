package net.imglib2.img.planar;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.offheap.ByteOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.CharOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.DoubleOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.FloatOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.IntOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.LongOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.type.NativeType;
import net.imglib2.util.Fraction;
import coremem.ContiguousMemoryInterface;
import coremem.SafeMemory;
import coremem.fragmented.FragmentedMemory;
import coremem.fragmented.FragmentedMemoryInterface;

public class OffHeapPlanarImgFactory<T extends NativeType<T>> extends
																															NativeImgFactory<T>
{

	private final boolean mAddSafetyWrapper;

	public OffHeapPlanarImgFactory()
	{
		this(true);
	}

	public OffHeapPlanarImgFactory(boolean pAddSafetyWrapper)
	{
		super();
		mAddSafetyWrapper = pAddSafetyWrapper;
	}

	@Override
	public NativeImg<T, ?> create(final long[] dim, final T type)
	{
		return type.createSuitableNativeImg(this, dim);
	}

	public NativeImg<T, ByteOffHeapAccess> createByteInstance(ContiguousMemoryInterface pContiguousMemory,
																														final long[] dimensions,
																														final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, ByteOffHeapAccess> lInstance = (OffHeapPlanarImg<T, ByteOffHeapAccess>) createByteInstance(	lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	public NativeImg<T, CharOffHeapAccess> createCharInstance(ContiguousMemoryInterface pContiguousMemory,
																														final long[] dimensions,
																														final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, CharOffHeapAccess> lInstance = (OffHeapPlanarImg<T, CharOffHeapAccess>) createCharInstance(	lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	public NativeImg<T, DoubleOffHeapAccess> createDoubleInstance(ContiguousMemoryInterface pContiguousMemory,
																																final long[] dimensions,
																																final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, DoubleOffHeapAccess> lInstance = (OffHeapPlanarImg<T, DoubleOffHeapAccess>) createDoubleInstance(	lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	public NativeImg<T, FloatOffHeapAccess> createFloatInstance(ContiguousMemoryInterface pContiguousMemory,
																															final long[] dimensions,
																															final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, FloatOffHeapAccess> lInstance = (OffHeapPlanarImg<T, FloatOffHeapAccess>) createFloatInstance(lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	public NativeImg<T, IntOffHeapAccess> createIntInstance(ContiguousMemoryInterface pContiguousMemory,
																													final long[] dimensions,
																													final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, IntOffHeapAccess> lInstance = (OffHeapPlanarImg<T, IntOffHeapAccess>) createIntInstance(lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	public NativeImg<T, LongOffHeapAccess> createLongInstance(ContiguousMemoryInterface pContiguousMemory,
																														final long[] dimensions,
																														final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, LongOffHeapAccess> lInstance = (OffHeapPlanarImg<T, LongOffHeapAccess>) createLongInstance(	lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	public NativeImg<T, ShortOffHeapAccess> createShortInstance(ContiguousMemoryInterface pContiguousMemory,
																															final long[] dimensions,
																															final T type)
	{
		final int lNumberOfDimensions = dimensions.length;
		final int lLastDimension = (int) dimensions[lNumberOfDimensions - 1];
		final FragmentedMemory lFragmentedMemory = FragmentedMemory.split(pContiguousMemory,
																																			lLastDimension);
		final OffHeapPlanarImg<T, ShortOffHeapAccess> lInstance = (OffHeapPlanarImg<T, ShortOffHeapAccess>) createShortInstance(lFragmentedMemory,
																																																													dimensions,
																																																													type);
		lInstance.setContiguousMemory(pContiguousMemory);
		return lInstance;
	}

	/*********************************************************************************************************/

	/**
	 * Creates a byte instance given a fragmented buffer, dimensions and a type
	 * 
	 * @param pFragmentedMemory
	 *          fragmnted buffer
	 * @param dimensions
	 *          dimensions
	 * @param type
	 *          type
	 * @return native image
	 */
	public NativeImg<T, ByteOffHeapAccess> createByteInstance(FragmentedMemoryInterface pFragmentedMemory,
																														final long[] dimensions,
																														final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, ByteOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, ByteOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																						dimensions);

		int i = 0;
		for (ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			if (mAddSafetyWrapper)
				lContiguousMemoryInterface = new SafeMemory(lContiguousMemoryInterface);
			final ByteOffHeapAccess lByteBufferAccess = new ByteOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lByteBufferAccess);
		}

		return lOffHeapPlanarImg;
	}

	public NativeImg<T, CharOffHeapAccess> createCharInstance(FragmentedMemoryInterface pFragmentedMemory,
																														final long[] dimensions,
																														final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, CharOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, CharOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																						dimensions);

		int i = 0;
		for (ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			if (mAddSafetyWrapper)
				lContiguousMemoryInterface = new SafeMemory(lContiguousMemoryInterface);
			final CharOffHeapAccess lCharOffHeapAccess = new CharOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lCharOffHeapAccess);
		}
		return lOffHeapPlanarImg;
	}

	public NativeImg<T, DoubleOffHeapAccess> createDoubleInstance(FragmentedMemoryInterface pFragmentedMemory,
																																final long[] dimensions,
																																final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, DoubleOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, DoubleOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																								dimensions);

		int i = 0;
		for (ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			if (mAddSafetyWrapper)
				lContiguousMemoryInterface = new SafeMemory(lContiguousMemoryInterface);
			final DoubleOffHeapAccess lDoubleOffHeapAccess = new DoubleOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lDoubleOffHeapAccess);
		}
		return lOffHeapPlanarImg;
	}

	public NativeImg<T, FloatOffHeapAccess> createFloatInstance(FragmentedMemoryInterface pFragmentedMemory,
																															final long[] dimensions,
																															final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, FloatOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, FloatOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																							dimensions);

		int i = 0;
		for (ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			if (mAddSafetyWrapper)
				lContiguousMemoryInterface = new SafeMemory(lContiguousMemoryInterface);
			final FloatOffHeapAccess lFloatOffHeapAccess = new FloatOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lFloatOffHeapAccess);
		}
		return lOffHeapPlanarImg;
	}

	public NativeImg<T, IntOffHeapAccess> createIntInstance(FragmentedMemoryInterface pFragmentedMemory,
																													final long[] dimensions,
																													final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, IntOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, IntOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																					dimensions);

		int i = 0;
		for (ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			if (mAddSafetyWrapper)
				lContiguousMemoryInterface = new SafeMemory(lContiguousMemoryInterface);
			final IntOffHeapAccess lIntOffHeapAccess = new IntOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lIntOffHeapAccess);
		}
		return lOffHeapPlanarImg;
	}

	public NativeImg<T, LongOffHeapAccess> createLongInstance(FragmentedMemoryInterface pFragmentedMemory,
																														final long[] dimensions,
																														final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, LongOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, LongOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																						dimensions);

		int i = 0;
		for (ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			if (mAddSafetyWrapper)
				lContiguousMemoryInterface = new SafeMemory(lContiguousMemoryInterface);
			final LongOffHeapAccess lLongOffHeapAccess = new LongOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lLongOffHeapAccess);
		}
		return lOffHeapPlanarImg;
	}

	public NativeImg<T, ShortOffHeapAccess> createShortInstance(FragmentedMemoryInterface pFragmentedMemory,
																															final long[] dimensions,
																															final T type)
	{
		@SuppressWarnings("unchecked")
		final OffHeapPlanarImg<T, ShortOffHeapAccess> lOffHeapPlanarImg = (OffHeapPlanarImg<T, ShortOffHeapAccess>) type.createSuitableNativeImg(	this,
																																																																							dimensions);

		int i = 0;
		for (final ContiguousMemoryInterface lContiguousMemoryInterface : pFragmentedMemory)
		{
			final ShortOffHeapAccess lShortOffHeapAccess = new ShortOffHeapAccess(lContiguousMemoryInterface);
			lOffHeapPlanarImg.setPlane(i++, lShortOffHeapAccess);
		}
		return lOffHeapPlanarImg;
	}

	/*********************************************************************************************************/

	@Override
	public NativeImg<T, ByteOffHeapAccess> createByteInstance(final long[] dimensions,
																														final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, ByteOffHeapAccess>(new ByteOffHeapAccess(1),
																											dimensions,
																											entitiesPerPixel);
	}

	@Override
	public NativeImg<T, CharOffHeapAccess> createCharInstance(final long[] dimensions,
																														final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, CharOffHeapAccess>(new CharOffHeapAccess(1),
																											dimensions,
																											entitiesPerPixel);
	}

	@Override
	public NativeImg<T, DoubleOffHeapAccess> createDoubleInstance(final long[] dimensions,
																																final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, DoubleOffHeapAccess>(new DoubleOffHeapAccess(1),
																												dimensions,
																												entitiesPerPixel);
	}

	@Override
	public NativeImg<T, FloatOffHeapAccess> createFloatInstance(final long[] dimensions,
																															final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, FloatOffHeapAccess>(	new FloatOffHeapAccess(1),
																												dimensions,
																												entitiesPerPixel);
	}

	@Override
	public NativeImg<T, IntOffHeapAccess> createIntInstance(final long[] dimensions,
																													final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, IntOffHeapAccess>(	new IntOffHeapAccess(1),
																											dimensions,
																											entitiesPerPixel);
	}

	@Override
	public NativeImg<T, LongOffHeapAccess> createLongInstance(final long[] dimensions,
																														final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, LongOffHeapAccess>(new LongOffHeapAccess(1),
																											dimensions,
																											entitiesPerPixel);
	}

	@Override
	public NativeImg<T, ShortOffHeapAccess> createShortInstance(final long[] dimensions,
																															final Fraction entitiesPerPixel)
	{
		return new OffHeapPlanarImg<T, ShortOffHeapAccess>(	new ShortOffHeapAccess(1),
																												dimensions,
																												entitiesPerPixel);
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Override
	public <S> ImgFactory<S> imgFactory(final S type) throws IncompatibleTypeException
	{
		if (NativeType.class.isInstance(type))
			return new PlanarImgFactory();
		throw new IncompatibleTypeException(this,
																				type.getClass()
																						.getCanonicalName() + " does not implement NativeType.");
	}
}
