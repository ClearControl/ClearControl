package clearcontrol.stack;

import java.util.concurrent.TimeUnit;

import org.bridj.Pointer;

import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclerInterface;
import coremem.util.Size;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class OffHeapPlanarStack extends StackBase	implements
																									StackInterface
{

	private OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> mPlanarImage;

	@SuppressWarnings("unchecked")
	public static OffHeapPlanarStack createStack(	final FragmentedMemoryInterface pFragmentedMemory,
																								final long pWidth,
																								final long pHeight,
																								final long pDepth)
	{
		return createStack(	pFragmentedMemory,
												true,
												pWidth,
												pHeight,
												pDepth);
	}

	@SuppressWarnings("unchecked")
	public static OffHeapPlanarStack createStack(	final FragmentedMemoryInterface pFragmentedMemory,
																								final boolean pSafe,
																								final long pWidth,
																								final long pHeight,
																								final long pDepth)
	{
		@SuppressWarnings("rawtypes")
		final OffHeapPlanarStack lOffHeapPlanarStack = new OffHeapPlanarStack();

		final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedShortType>(pSafe);

		lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.createShortInstance(pFragmentedMemory,
																																																																							new long[]
																																																																							{ pWidth,
																																																																								pHeight,
																																																																								pDepth },
																																																																							new UnsignedShortType());

		return lOffHeapPlanarStack;

	}

	@SuppressWarnings("unchecked")
	public static OffHeapPlanarStack createStack(	final long pWidth,
																								final long pHeight,
																								final long pDepth)
	{

		return createStack(true, pWidth, pHeight, pDepth);
	}

	@SuppressWarnings("unchecked")
	public static OffHeapPlanarStack createStack(	final boolean pSafe,
																								final long pWidth,
																								final long pHeight,
																								final long pDepth)
	{

		final long lSizeInBytes = pWidth * pHeight
															* pDepth
															* Size.of(NativeTypeEnum.UnsignedShort);
		final ContiguousMemoryInterface lContiguousMemory = OffHeapMemory.allocateBytes(lSizeInBytes);
		return createStack(	lContiguousMemory,
												pSafe,
												pWidth,
												pHeight,
												pDepth);
	}

	@SuppressWarnings("unchecked")
	public static OffHeapPlanarStack createStack(	final ContiguousMemoryInterface pContiguousMemoryInterface,
																								final long pWidth,
																								final long pHeight,
																								final long pDepth)
	{
		return createStack(	pContiguousMemoryInterface,
												true,
												pWidth,
												pHeight,
												pDepth);
	}

	@SuppressWarnings("unchecked")
	public static OffHeapPlanarStack createStack(	final ContiguousMemoryInterface pContiguousMemoryInterface,
																								final boolean pSafe,
																								final long pWidth,
																								final long pHeight,
																								final long pDepth)
	{
		@SuppressWarnings("rawtypes")
		final OffHeapPlanarStack lOffHeapPlanarStack = new OffHeapPlanarStack();

		final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory = new OffHeapPlanarImgFactory<UnsignedShortType>(pSafe);

		lOffHeapPlanarStack.mPlanarImage = (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.createShortInstance(pContiguousMemoryInterface,
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

	@SuppressWarnings("unchecked")
	public OffHeapPlanarStack(final long pImageIndex,
														final long pTimeStampInNanoseconds,
														final OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pOffHeapPlanarImg)
	{
		super();
		setIndex(pImageIndex);
		setTimeStampInNanoseconds(pTimeStampInNanoseconds);
		mPlanarImage = pOffHeapPlanarImg;
	}

	@Override
	public boolean isCompatible(final StackRequest pStackRequest)
	{
		if (mPlanarImage == null)
			return false;
		if (mPlanarImage.isFree())
			return false;

		if (this.getWidth() != pStackRequest.getWidth() || this.getHeight() != pStackRequest.getHeight()
				|| this.getDepth() != pStackRequest.getDepth())
			return false;

		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void recycle(final StackRequest pStackRequest)
	{
		// not much to do here...
	}

	public OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> getPlanarImage()
	{
		complainIfFreed();
		return mPlanarImage;
	}

	@Override
	public NativeImg<UnsignedShortType, ShortOffHeapAccess> getImage()
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

	public static StackInterface getOrWaitWithRecycler(	final RecyclerInterface<StackInterface, StackRequest> pRecycler,
																											final long pWaitTime,
																											final TimeUnit pTimeUnit,
																											final long pWidth,
																											final long pHeight,
																											final long pDepth)
	{
		final StackRequest lStackRequest = new StackRequest(pWidth,
																												pHeight,
																												pDepth);

		return pRecycler.getOrWait(pWaitTime, pTimeUnit, lStackRequest);
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
	public StackInterface allocateSameSize()
	{
		final long lSizeInBytes = this.getSizeInBytes();
		final OffHeapMemory lOffHeapMemory = OffHeapMemory.allocateBytes(lSizeInBytes);
		return OffHeapPlanarStack.createStack(lOffHeapMemory,
																					getWidth(),
																					getHeight(),
																					getDepth());
	}

	@SuppressWarnings("unchecked")
	@Override
	public StackInterface duplicate()
	{
		StackInterface lSameSizeStack = allocateSameSize();

		lSameSizeStack.getContiguousMemory()
									.copyFrom(this.getContiguousMemory());
		return lSameSizeStack;
	}

}
