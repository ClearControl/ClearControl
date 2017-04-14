package clearcontrol.stack;

import java.util.concurrent.TimeUnit;

import net.imglib2.img.AbstractImg;
import net.imglib2.img.NativeImg;
import net.imglib2.img.basictypeaccess.offheap.ShortOffHeapAccess;
import net.imglib2.img.planar.OffHeapPlanarImg;
import net.imglib2.img.planar.OffHeapPlanarImgFactory;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import clearcontrol.stack.metadata.MetaDataOrdinals;
import coremem.ContiguousMemoryInterface;
import coremem.enums.NativeTypeEnum;
import coremem.fragmented.FragmentedMemoryInterface;
import coremem.offheap.OffHeapMemory;
import coremem.recycling.RecyclerInterface;
import coremem.util.Size;

/**
 * Stack that uses a OffHeapPlanarImg to store the voxel data. OffHeapPlanarImg
 * are imglib2 planar images that use OffHeapMemory internally.
 *
 * @author royer
 */
public class OffHeapPlanarStack extends StackBase
                                implements StackInterface
{

  private OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> mPlanarImage;

  /**
   * Instanciates a stack given a recycler, wait time, and stack dimensions. A
   * stack request is built and the recycler is asked to provide a stack within
   * the given timeout period.
   * 
   * @param pRecycler
   *          recycler
   * @param pWaitTime
   *          wait time
   * @param pTimeUnit
   *          time unit
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack getOrWaitWithRecycler(final RecyclerInterface<StackInterface, StackRequest> pRecycler,
                                                         final long pWaitTime,
                                                         final TimeUnit pTimeUnit,
                                                         final long pWidth,
                                                         final long pHeight,
                                                         final long pDepth)
  {
    final StackRequest lStackRequest = new StackRequest(pWidth,
                                                        pHeight,
                                                        pDepth);

    return (OffHeapPlanarStack) pRecycler.getOrWait(pWaitTime,
                                                    pTimeUnit,
                                                    lStackRequest);
  }

  /**
   * Instanciates a stack given dimensions and a fragmented memory object
   * 
   * @param pFragmentedMemory
   *          memory
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stck
   */
  public static OffHeapPlanarStack createStack(final FragmentedMemoryInterface pFragmentedMemory,
                                               final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {
    return createStack(pFragmentedMemory,
                       true,
                       pWidth,
                       pHeight,
                       pDepth);
  }

  /**
   * Instanciates a stack given dimensions, a fragmented memory object, and a
   * flag that decides whether to use safe off-heap access.
   * 
   * @param pFragmentedMemory
   *          memory
   * @param pSafe
   *          true -> safe off-heap access
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack createStack(final FragmentedMemoryInterface pFragmentedMemory,
                                               final boolean pSafe,
                                               final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {
    final OffHeapPlanarStack lOffHeapPlanarStack =
                                                 new OffHeapPlanarStack();

    final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory =
                                                                              new OffHeapPlanarImgFactory<UnsignedShortType>(pSafe);

    lOffHeapPlanarStack.mPlanarImage =
                                     (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.createShortInstance(pFragmentedMemory,
                                                                                                                                            new long[]
                                                                                                                                            { pWidth, pHeight, pDepth }, new UnsignedShortType());

    return lOffHeapPlanarStack;

  }

  /**
   * Instanciates an off-heap backed stack given width, height and depth
   * parameters
   * 
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack createStack(final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {

    return createStack(true, pWidth, pHeight, pDepth);
  }

  /**
   * Instanciates an off-heap backed stack given width, height and depth
   * parameters, as well as a flag that decides whether to use safe off-heap
   * access.
   * 
   * @param pSafe
   *          true -> safe off-heap access
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack createStack(final boolean pSafe,
                                               final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {

    final long lSizeInBytes = pWidth * pHeight
                              * pDepth
                              * Size.of(NativeTypeEnum.UnsignedShort);
    final ContiguousMemoryInterface lContiguousMemory =
                                                      OffHeapMemory.allocateBytes(lSizeInBytes);
    return createStack(lContiguousMemory,
                       pSafe,
                       pWidth,
                       pHeight,
                       pDepth);
  }

  /**
   * Instanciates a stack given dimensions and a contiguous memory object.
   * 
   * @param pContiguousMemoryInterface
   *          contiguous memory object
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack createStack(final ContiguousMemoryInterface pContiguousMemoryInterface,
                                               final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {
    return createStack(pContiguousMemoryInterface,
                       true,
                       pWidth,
                       pHeight,
                       pDepth);
  }

  /**
   * Instanciates a stack given dimensions, a contiguous memory object, and a
   * flag that decides whether to use safe off-heap access.
   * 
   * @param pContiguousMemoryInterface
   *          contiguous memory object
   * @param pSafe
   *          true -> safe off-heap access
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack createStack(final ContiguousMemoryInterface pContiguousMemoryInterface,
                                               final boolean pSafe,
                                               final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {
    final OffHeapPlanarStack lOffHeapPlanarStack =
                                                 new OffHeapPlanarStack();

    final OffHeapPlanarImgFactory<UnsignedShortType> lOffHeapPlanarImgFactory =
                                                                              new OffHeapPlanarImgFactory<UnsignedShortType>(pSafe);

    lOffHeapPlanarStack.mPlanarImage =
                                     (OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess>) lOffHeapPlanarImgFactory.createShortInstance(pContiguousMemoryInterface,
                                                                                                                                            new long[]
                                                                                                                                            { pWidth, pHeight, pDepth }, new UnsignedShortType());

    return lOffHeapPlanarStack;

  }

  private OffHeapPlanarStack()
  {
    super();
  }

  /**
   * Instanciates a stack backed by a given off-heap planar imglib2 image, time
   * index, and time stamp
   * 
   * @param pImageIndex
   *          image index
   * @param pTimeStampInNanoseconds
   *          timestamp
   * @param pOffHeapPlanarImg
   *          off-heap planar image
   */
  public OffHeapPlanarStack(final long pImageIndex,
                            final long pTimeStampInNanoseconds,
                            final OffHeapPlanarImg<UnsignedShortType, ShortOffHeapAccess> pOffHeapPlanarImg)
  {
    super();
    getMetaData().setIndex(pImageIndex);
    getMetaData().setTimeStampInNanoseconds(pTimeStampInNanoseconds);
    mPlanarImage = pOffHeapPlanarImg;
  }

  @Override
  public boolean isCompatible(final StackRequest pStackRequest)
  {
    if (mPlanarImage == null)
      return false;
    if (mPlanarImage.isFree())
      return false;

    if (this.getWidth() != pStackRequest.getWidth()
        || this.getHeight() != pStackRequest.getHeight()
        || this.getDepth() != pStackRequest.getDepth())
      return false;

    return true;
  }

  @Override
  public void recycle(final StackRequest pStackRequest)
  {
    super.recycle(pStackRequest);
  }

  /**
   * Returns the internal imglib2 planar image
   * 
   * @return imglib2 planar image used internally
   */
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
    final long lBytesPerVoxel = mPlanarImage.getSizeInBytes()
                                / lNumElements;
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
  public long getVolume()
  {
    long lVolume = 1;
    long lDimensions = getNumberOfDimensions();
    for (int i = 0; i < lDimensions; i++)
      lVolume *= getDimension(i);
    return lVolume;
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

    return String.format(this.getClass().getSimpleName()
                         + " [ BytesPerVoxel=%d, width=%s, height=%s, depth=%s, index=%d, timestampns=%d ]",
                         getBytesPerVoxel(),
                         mPlanarImage.dimension(0),
                         mPlanarImage.dimension(1),
                         mPlanarImage.dimension(2),
                         getMetaData().getValue(MetaDataOrdinals.Index),
                         getMetaData().getValue(MetaDataOrdinals.TimeStampInNanoSeconds));
  }

  @Override
  public FragmentedMemoryInterface getFragmentedMemory()
  {
    final FragmentedMemoryInterface lFragmentedMemoryInterface =
                                                               mPlanarImage.getFragmentedMemory();
    return lFragmentedMemoryInterface;
  }

  @Override
  public ContiguousMemoryInterface getContiguousMemory()
  {
    return mPlanarImage.getContiguousMemory();
  }

  @Override
  public StackInterface allocateSameSize()
  {
    final long lSizeInBytes = this.getSizeInBytes();
    final OffHeapMemory lOffHeapMemory =
                                       OffHeapMemory.allocateBytes(lSizeInBytes);
    return OffHeapPlanarStack.createStack(lOffHeapMemory,
                                          getWidth(),
                                          getHeight(),
                                          getDepth());
  }

  @Override
  public StackInterface duplicate()
  {
    OffHeapPlanarStack lSameSizeStack =
                                      (OffHeapPlanarStack) allocateSameSize();

    lSameSizeStack.getContiguousMemory()
                  .copyFrom(this.getContiguousMemory());
    return lSameSizeStack;
  }

}
