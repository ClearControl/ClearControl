package rtlib.stack;

import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclerInterface;
import coremem.util.Size;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

public class OffHeapPlanarStack<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																						StackBase<T, A>	implements
																										StackInterface<T, A>
{

	private OffHeapPlanarImg<T, A> mPlanarImage;

	@SuppressWarnings("unchecked")
	public static <T extends NativeType<T>> OffHeapPlanarStack<T, ?> createStack(	final FragmentedMemoryInterface pFragmentedMemory,
																					final T pType,

																					final long pWidth,
																					final long pHeight,
																					final long pDepth)
	{
		return createStack(	pFragmentedMemory,
							pType,
							true,
							pWidth,
							pHeight,
							pDepth);
	}

	@SuppressWarnings("unchecked")
	public static <T extends NativeType<T>> OffHeapPlanarStack<T, ?> createStack(	final FragmentedMemoryInterface pFragmentedMemory,
																					final T pType,
																					final boolean pSafe,
																					final long pWidth,
																					final long pHeight,
																					final long pDepth)
	{
		@SuppressWarnings("rawtypes")
		final OffHeapPlanarStack lOffHeapPlanarStack = new OffHeapPlanarStack();

		lOffHeapPlanarStack.setType(pType);

		final OffHeapPlanarImgFactory<T> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<T>(pSafe);

		if (pType instanceof UnsignedByteType || pType instanceof ByteType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createByteInstance(pFragmentedMemory,
																													new long[]
																													{	pWidth,
																														pHeight,
																														pDepth },
																													pType);
		}
		else if (pType instanceof UnsignedShortType || pType instanceof ShortType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createShortInstance(	pFragmentedMemory,
																														new long[]
																														{	pWidth,
																															pHeight,
																															pDepth },
																														pType);
		}
		else if (pType instanceof UnsignedIntType || pType instanceof IntType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createIntInstance(	pFragmentedMemory,
																													new long[]
																													{	pWidth,
																														pHeight,
																														pDepth },
																													pType);
		}
		else if (pType instanceof UnsignedLongType || pType instanceof LongType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createLongInstance(pFragmentedMemory,
																													new long[]
																													{	pWidth,
																														pHeight,
																														pDepth },
																													pType);
		}
		else if (pType instanceof FloatType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createFloatInstance(	pFragmentedMemory,
																														new long[]
																														{	pWidth,
																															pHeight,
																															pDepth },
																														pType);
		}
		else if (pType instanceof DoubleType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createDoubleInstance(	pFragmentedMemory,
																														new long[]
																														{	pWidth,
																															pHeight,
																															pDepth },
																														pType);
		}

		return lOffHeapPlanarStack;

	}

	@SuppressWarnings("unchecked")
	public static <T extends NativeType<T>> OffHeapPlanarStack<T, ?> createStack(	final T pType,
																					final long pWidth,
																					final long pHeight,
																					final long pDepth)
	{

		return createStack(pType, true, pWidth, pHeight, pDepth);
	}

	@SuppressWarnings("unchecked")
	public static <T extends NativeType<T>> OffHeapPlanarStack<T, ?> createStack(	final T pType,
																					final boolean pSafe,
																					final long pWidth,
																					final long pHeight,
																					final long pDepth)
	{

		final long lSizeInBytes = pWidth * pHeight
									* pDepth
									* Size.of(pType.getClass());
		final ContiguousMemoryInterface lContiguousMemory = OffHeapMemory.allocateBytes(lSizeInBytes);
		return createStack(	lContiguousMemory,
							pType,
							pSafe,
							pWidth,
							pHeight,
							pDepth);
	}

	@SuppressWarnings("unchecked")
	public static <T extends NativeType<T>> OffHeapPlanarStack<T, ?> createStack(	final ContiguousMemoryInterface pContiguousMemoryInterface,
																					final T pType,
																					final long pWidth,
																					final long pHeight,
																					final long pDepth)
	{
		return createStack(	pContiguousMemoryInterface,
							pType,
							true,
							pWidth,
							pHeight,
							pDepth);
	}

	@SuppressWarnings("unchecked")
	public static <T extends NativeType<T>> OffHeapPlanarStack<T, ?> createStack(	final ContiguousMemoryInterface pContiguousMemoryInterface,
																					final T pType,
																					final boolean pSafe,
																					final long pWidth,
																					final long pHeight,
																					final long pDepth)
	{
		@SuppressWarnings("rawtypes")
		final OffHeapPlanarStack lOffHeapPlanarStack = new OffHeapPlanarStack();

		lOffHeapPlanarStack.setType(pType);

		final OffHeapPlanarImgFactory<T> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<T>(pSafe);

		if (pType instanceof UnsignedByteType || pType instanceof ByteType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createByteInstance(pContiguousMemoryInterface,
																													new long[]
																													{	pWidth,
																														pHeight,
																														pDepth },
																													pType);
		}
		else if (pType instanceof UnsignedShortType || pType instanceof ShortType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createShortInstance(	pContiguousMemoryInterface,
																														new long[]
																														{	pWidth,
																															pHeight,
																															pDepth },
																														pType);
		}
		else if (pType instanceof UnsignedIntType || pType instanceof IntType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createIntInstance(	pContiguousMemoryInterface,
																													new long[]
																													{	pWidth,
																														pHeight,
																														pDepth },
																													pType);
		}
		else if (pType instanceof UnsignedLongType || pType instanceof LongType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createLongInstance(pContiguousMemoryInterface,
																													new long[]
																													{	pWidth,
																														pHeight,
																														pDepth },
																													pType);
		}
		else if (pType instanceof FloatType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createFloatInstance(	pContiguousMemoryInterface,
																														new long[]
																														{	pWidth,
																															pHeight,
																															pDepth },
																														pType);
		}
		else if (pType instanceof DoubleType)
		{
			lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<T, ?>) lOffHeapPlanarImgFactory.createDoubleInstance(	pContiguousMemoryInterface,
																														new long[]
																														{	pWidth,
																															pHeight,
																															pDepth },
																														pType);
		}

		return lOffHeapPlanarStack;

	}

	private OffHeapPlanarStack()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	public OffHeapPlanarStack(	final long pImageIndex,
								final long pTimeStampInNanoseconds,
								T pType,
								final OffHeapPlanarImg<T, A> pOffHeapPlanarImg)
	{
		super();
		setIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		setType(pType);
		mPlanarImage = pOffHeapPlanarImg;
	}

	@Override
	public boolean isCompatible(final StackRequest<T> pStackRequest)
	{
		if (mPlanarImage == null)
			return false;
		if (mPlanarImage.isFree())
			return false;

		final T lType = pStackRequest.getType();

		if (lType.getClass() != getType().getClass())
			return false;

		if (this.getWidth() != pStackRequest.getWidth() || this.getHeight() != pStackRequest.getHeight()
			|| this.getDepth() != pStackRequest.getDepth())
			return false;

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void recycle(final StackRequest<T> pStackRequest)
	{
		// not much to do here...
	}

	public OffHeapPlanarImg<T, A> getPlanarImage()
	{
		complainIfFreed();
		return mPlanarImage;
	}

	@Override
	public NativeImg<T, A> getImage()
	{
		return getPlanarImage();
	}

	@Override
	public Pointer<Byte> getPointer(int pPlaneIndex)
	{
		return mPlanarImage.getPlanePointer(pPlaneIndex);
	}

	@Override
	public ContiguousMemoryInterface getContiguousMemory(int pPlaneIndex)
	{
		return mPlanarImage.getPlaneContiguousMemory(pPlaneIndex);
	}

	@Override
	public long getBytesPerVoxel()
	{
		final long[] dimensions = new long[mPlanarImage.numDimensions()];
		mPlanarImage.dimensions(dimensions);
		final long lNumElements = AbstractImg.numElements(dimensions);
		final long lBytesPerVoxel = mPlanarImage.getSizeInBytes() / lNumElements;
		return lBytesPerVoxel;
	}

	@Override
	public long getSizeInBytes()
	{
		return mPlanarImage.getSizeInBytes();
	}

	@Override
	public long[] getDimensions()
	{
		final long[] lDimensions = new long[mPlanarImage.numDimensions()];
		mPlanarImage.dimensions(lDimensions);
		return lDimensions;
	}

	@Override
	public int getNumberOfDimensions()
	{
		return mPlanarImage.numDimensions();
	}

	@Override
	public long getDimension(int pIndex)
	{
		return mPlanarImage.dimension(pIndex);
	}

	@Override
	public long getWidth()
	{
		return mPlanarImage.dimension(0);
	}

	@Override
	public long getHeight()
	{
		return mPlanarImage.dimension(1);
	}

	@Override
	public long getDepth()
	{
		return mPlanarImage.dimension(2);
	}

	public long getNumberOfVoxels()
	{
		long lNumberOfVoxels = 1;
		for (int i = 0; i < getNumberOfDimensions(); i++)
			lNumberOfVoxels *= getDimension(i);
		return lNumberOfVoxels;
	}

	public static <T extends NativeType<T>, A extends ArrayDataAccess<A>> StackInterface<T, A> getOrWaitWithRecycler(	final RecyclerInterface<StackInterface<T, A>, StackRequest<T>> pRecycler,
																														final long pWaitTime,
																														final TimeUnit pTimeUnit,
																														final T pType,
																														final long pWidth,
																														final long pHeight,
																														final long pDepth)
	{
		final StackRequest<T> lStackRequest = new StackRequest<T>(	pType,
																	pWidth,
																	pHeight,
																	pDepth);

		return pRecycler.getOrWait(	pWaitTime,
									pTimeUnit,
									lStackRequest);
	}

	@Override
	public void free()
	{
		mPlanarImage.free();
	}

	@Override
	public boolean isFree()
	{
		return mPlanarImage.isFree();
	}

	@Override
	public String toString()
	{
		return String.format(	this.getClass().getSimpleName() + " [ BytesPerVoxel=%d, width=%s, height=%s, depth=%s, index=%d, timestampns=%d ]",
								getBytesPerVoxel(),
								mPlanarImage.dimension(0),
								mPlanarImage.dimension(1),
								mPlanarImage.dimension(2),
								getIndex(),
								getTimeStampInNanoseconds());
	}

	@Override
	public T getType()
	{
		return mType;
	}

	@Override
	public void setType(final T pType)
	{
		mType = pType;
	}

	@Override
	public FragmentedMemoryInterface getFragmentedMemory()
	{
		final FragmentedMemoryInterface lFragmentedMemoryInterface = mPlanarImage.getFragmentedMemory();
		return lFragmentedMemoryInterface;
	}

	@Override
	public ContiguousMemoryInterface getContiguousMemory()
	{
		return mPlanarImage.getContiguousMemory();
	}

	@SuppressWarnings("unchecked")
	@Override
	public StackInterface<T, A> duplicate()
	{
		final long lSizeInBytes = this.getSizeInBytes();
		final OffHeapMemory lOffHeapMemory = OffHeapMemory.allocateBytes(lSizeInBytes);
		return (StackInterface<T, A>) OffHeapPlanarStack.createStack(	lOffHeapMemory,
																		getType(),
																		getWidth(),
																		getHeight(),
																		getDepth());
	}

}
