package rtlib.stack;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.AbstractImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.offheap.ByteOffHeapAccess;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.recycling.Recycler;

public class OffHeapPlanarStack<T extends NativeType<T>, A extends ArrayDataAccess<A>>	extends
																																												StackBase<T, A>	implements
																																																				StackInterface<T, A>
{

	private OffHeapPlanarImg<T, A> mPlanarImage;


	public static OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess> createUnsignedByteStack(final long pImageIndex,
																																													final long pTimeStampInNanoseconds,
																																													final FragmentedMemoryInterface pFragmentedMemory,
																																													final long pWidth,
																																													final long pHeight,
																																													final long pDepth)
	{
		final OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess> lOffHeapPlanarStack = new OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess>();

		lOffHeapPlanarStack.setIndex(pImageIndex);
		lOffHeapPlanarStack.setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		lOffHeapPlanarStack.setType(new UnsignedByteType());

		final OffHeapPlanarImgFactory<UnsignedByteType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedByteType>(true);

		lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<UnsignedByteType, ByteOffHeapAccess>) lOffHeapPlanarImgFactory.createByteInstance(	pFragmentedMemory,
																																																																						new long[]
																																																																						{ pWidth,
																																																																							pHeight,
																																																																							pDepth },
																																																																						new UnsignedByteType());

		return lOffHeapPlanarStack;

	}

	public static OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess> createUnsignedByteStack(final long pImageIndex,
																																													final long pTimeStampInNanoseconds,
																																													final ContiguousMemoryInterface pContiguousMemory,
																																													final long pWidth,
																																													final long pHeight,
																																													final long pDepth)
	{
		final OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess> lOffHeapPlanarStack = new OffHeapPlanarStack<UnsignedByteType, ByteOffHeapAccess>();

		lOffHeapPlanarStack.setIndex(pImageIndex);
		lOffHeapPlanarStack.setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		lOffHeapPlanarStack.setType(new UnsignedByteType());

		final OffHeapPlanarImgFactory<UnsignedByteType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedByteType>(true);

		lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<UnsignedByteType, ByteOffHeapAccess>) lOffHeapPlanarImgFactory.createByteInstance(	pContiguousMemory,
																																																																						new long[]
																																																																						{ pWidth,
																																																																							pHeight,
																																																																							pDepth },
																																																																						new UnsignedByteType());

		return lOffHeapPlanarStack;

	}

	public static OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> createUnsignedShortStack(	final long pImageIndex,
																																																	final long pTimeStampInNanoseconds,
																																																	final FragmentedMemoryInterface pFragmentedMemory,
																																																	final long pWidth,
																																																	final long pHeight,
																																																	final long pDepth)
	{
		final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStack = new OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>();

		lOffHeapPlanarStack.setIndex(pImageIndex);
		lOffHeapPlanarStack.setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		lOffHeapPlanarStack.setType(new UnsignedShortType());

		final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedShortType>(true);

		lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.createShortInstance(pFragmentedMemory,
																																																																						new long[]
																																																																						{ pWidth,
																																																																							pHeight,
																																																																							pDepth },
																																																																							new UnsignedShortType());

		return lOffHeapPlanarStack;

	}

	public static OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> createUnsignedShortStack(	final long pImageIndex,
																																																	final long pTimeStampInNanoseconds,
																																																	final ContiguousMemoryInterface pContiguousMemory,
																																																	final long pWidth,
																																																	final long pHeight,
																																																	final long pDepth)
	{
		final OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess> lOffHeapPlanarStack = new OffHeapPlanarStack<UnsignedShortType, ShortOffHeapAccess>();

		lOffHeapPlanarStack.setIndex(pImageIndex);
		lOffHeapPlanarStack.setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		lOffHeapPlanarStack.setType(new UnsignedShortType());

		final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedShortType>(true);

		lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.createShortInstance(pContiguousMemory,
																																																																						new long[]
																																																																						{ pWidth,
																																																																							pHeight,
																																																																							pDepth },
																																																																						new UnsignedShortType());

		return lOffHeapPlanarStack;

	}

	private OffHeapPlanarStack()
	{
		super();
	}

	public OffHeapPlanarStack(StackRequest<T> pParameters)
	{
		this(	0,
					0,
					pParameters.getType(),
					pParameters.getWidth(),
					pParameters.getHeight(),
					pParameters.getDepth());
	}

	@SuppressWarnings("unchecked")
	public OffHeapPlanarStack(final long pImageIndex,
														final long pTimeStampInNanoseconds,
														final T pType,
														final long pWidth,
														final long pHeight,
														final long pDepth)
	{
		super();

		setIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		setType(pType);

		final OffHeapPlanarImgFactory<T> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<T>(true);

		mPlanarImage = (OffHeapPlanarImg<T, A>) lOffHeapPlanarImgFactory.create(new long[]
																																						{ pWidth,
																																							pHeight,
																																							pDepth },
																																						pType);

	}

	@SuppressWarnings("unchecked")
	public OffHeapPlanarStack(final long pImageIndex,
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
		mType = pStackRequest.getType();
		if (!isCompatible(pStackRequest))
		{
			if (mPlanarImage != null)
				mPlanarImage.free();
			final OffHeapPlanarImgFactory<T> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<T>(true);
			final long[] lDimensions = new long[]
			{ pStackRequest.getWidth(),
				pStackRequest.getHeight(),
				pStackRequest.getDepth() };
			mPlanarImage = (OffHeapPlanarImg<T, A>) lOffHeapPlanarImgFactory.create(lDimensions,
																																							mType);

		}
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

	public static <T extends NativeType<T>, A extends ArrayDataAccess<A>> StackInterface<T, A> requestOrWaitWithRecycler(	final Recycler<StackInterface<T, A>, StackRequest<T>> pRecycler,
																																																												final long pWaitTime,
																																																												final TimeUnit pTimeUnit,
																																																												final T pType,
																																																												final long pWidth,
																																																												final long pHeight,
																																																												final long pDepth)
	{
		final StackRequest<T> lStackRequest = new StackRequest<T>(pType,
																															pWidth,
																															pHeight,
																															pDepth);

		return pRecycler.waitOrRequestRecyclableObject(	pWaitTime,
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

}
