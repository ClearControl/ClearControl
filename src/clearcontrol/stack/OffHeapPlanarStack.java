package clearcontrol.stack;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import clearcontrol.stack.metadata.MetaDataOrdinals;
import coremem.ContiguousMemoryInterface;
import coremem.SafeContiguousMemory;
import coremem.enums.NativeTypeEnum;
import coremem.fragmented.FragmentedMemory;
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

  private ContiguousMemoryInterface mContiguousMemory;

  // Fragmented 'plane-per-plane' view:
  private FragmentedMemory mFragmentedMemory;

  private boolean mIsSafe;

  /**
   * Instantiates a stack given a recycler, wait time, and stack dimensions. A
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
   * Instantiates an off-heap backed single channel 16bit unsigned int stack
   * with given width, height and depth
   * 
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

    return new OffHeapPlanarStack(false,
                                  0,
                                  NativeTypeEnum.UnsignedShort,
                                  1,
                                  pWidth,
                                  pHeight,
                                  pDepth);
  }

  /**
   * Instantiates an contiguous memory backed single channel 16bit unsigned int
   * stack with given width, height and depth
   * 
   * @param pContiguousMemory
   *          contiguous memory to use
   * @param pWidth
   *          width
   * @param pHeight
   *          height
   * @param pDepth
   *          depth
   * @return stack
   */
  public static OffHeapPlanarStack createStack(ContiguousMemoryInterface pContiguousMemory,
                                               final long pWidth,
                                               final long pHeight,
                                               final long pDepth)
  {

    return new OffHeapPlanarStack(pContiguousMemory,
                                  false,
                                  NativeTypeEnum.UnsignedShort,
                                  1,
                                  pWidth,
                                  pHeight,
                                  pDepth);
  }

  /**
   * instantiates an off-heap stack with given parameters
   * 
   * @param pSafe
   *          true -> safe off-heap access
   * @param pAlignment
   *          alignment base for buffer allocation
   * @param pDataType
   *          data type
   * @param pNumberOfChannels
   *          number of channels
   * @param pDimensions
   *          dimensions
   */
  public OffHeapPlanarStack(final boolean pSafe,
                            final long pAlignment,
                            final NativeTypeEnum pDataType,
                            final long pNumberOfChannels,
                            final long... pDimensions)
  {
    super(pDataType, pNumberOfChannels, pDimensions);
    mIsSafe = pSafe;

    final long lSizeInBytes = getVolume() * pNumberOfChannels
                              * Size.of(pDataType);

    final ContiguousMemoryInterface lContiguousMemory =
                                                      SafeContiguousMemory.wrap(OffHeapMemory.allocateAlignedBytes("OffHeapPlanarStack",
                                                                                                                   lSizeInBytes,
                                                                                                                   pAlignment),
                                                                                pSafe);

    long lNumberOfFragments = pDimensions[pDimensions.length - 1];

    FragmentedMemory lFragmentedMemory =
                                       FragmentedMemory.split(lContiguousMemory,
                                                              lNumberOfFragments);

    mContiguousMemory = lContiguousMemory;
    mFragmentedMemory = lFragmentedMemory;

  }

  /**
   * instantiates an off-heap stack with given parameters
   * 
   * @param pContiguousMemory
   *          contiguous memory to use
   * @param pSafe
   *          true -> safe off-heap access
   * @param pDataType
   *          data type
   * @param pNumberOfChannels
   *          number of channels
   * @param pDimensions
   *          dimensions
   */
  public OffHeapPlanarStack(final ContiguousMemoryInterface pContiguousMemory,
                            final boolean pSafe,
                            final NativeTypeEnum pDataType,
                            final long pNumberOfChannels,
                            final long... pDimensions)
  {
    super(pDataType, pNumberOfChannels, pDimensions);
    mIsSafe = pSafe;

    final ContiguousMemoryInterface lContiguousMemory =
                                                      SafeContiguousMemory.wrap(pContiguousMemory,
                                                                                pSafe);

    long lNumberOfFragments = pDimensions[pDimensions.length - 1];

    FragmentedMemory lFragmentedMemory =
                                       FragmentedMemory.split(lContiguousMemory,
                                                              lNumberOfFragments);

    mContiguousMemory = lContiguousMemory;
    mFragmentedMemory = lFragmentedMemory;

  }

  /**
   * Returns true if off-heap memory is wrapped by a safe contiguous memory
   * object
   * 
   * @return true if off-heap memory is wrapped by a safe contiguous memory
   *         object
   */
  public boolean isSafe()
  {
    return mIsSafe;
  }

  @Override
  public boolean isCompatible(final StackRequest pStackRequest)
  {
    if (mContiguousMemory == null)
      return false;
    if (mContiguousMemory.isFree())
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

  @Override
  public ContiguousMemoryInterface getContiguousMemory(int pPlaneIndex)
  {
    return mFragmentedMemory.get(pPlaneIndex);
  }

  @Override
  public FragmentedMemoryInterface getFragmentedMemory()
  {
    return mFragmentedMemory;
  }

  @Override
  public ContiguousMemoryInterface getContiguousMemory()
  {
    return mContiguousMemory;
  }

  @Override
  public StackInterface allocateSameSize()
  {
    return new OffHeapPlanarStack(isSafe(),
                                  0,
                                  getDataType(),
                                  getNumberOfChannels(),
                                  getDimensions());
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

  @Override
  public void free()
  {
    mContiguousMemory.free();
  }

  @Override
  public boolean isFree()
  {
    return mContiguousMemory.isFree();
  }

  @Override
  public String toString()
  {

    return String.format(this.getClass().getSimpleName()
                         + " [ BytesPerVoxel=%d, datatype=%s, numberofchannels=%d, dimensions=%s, index=%d, timestampns=%d ]",
                         getBytesPerVoxel(),
                         getDataType(),
                         getNumberOfChannels(),
                         Arrays.toString(getDimensions()),
                         getMetaData().getValue(MetaDataOrdinals.Index),
                         getMetaData().getValue(MetaDataOrdinals.TimeStampInNanoSeconds));
  }

}
