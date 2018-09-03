package clearcontrol.stack;

import java.util.Arrays;

import clearcontrol.stack.metadata.StackMetaData;
import coremem.enums.NativeTypeEnum;
import coremem.recycling.RecyclerInterface;
import coremem.rgc.FreeableBase;
import coremem.util.Size;

/**
 * Base class providing common fields and methods for all stack implementations
 *
 * @author royer
 */
public abstract class StackBase extends FreeableBase
                                implements StackInterface
{

  protected RecyclerInterface<StackInterface, StackRequest> mStackRecycler;
  protected volatile boolean mIsReleased;

  protected StackMetaData mMetaData = new StackMetaData();

  protected NativeTypeEnum mDataType;
  protected long[] mDimensions;
  protected long mNumberOfChannels;

  /**
   * Instantiates a stack with given data type, number of channels, and
   * dimensions
   * 
   * @param pDataType
   *          data type
   * @param pNumberOfChannels
   *          number of channels
   * @param pDimensions
   *          dimensions
   */
  public StackBase(final NativeTypeEnum pDataType,
                   final long pNumberOfChannels,
                   final long... pDimensions)
  {
    mDataType = pDataType;
    mNumberOfChannels = pNumberOfChannels;
    mDimensions = Arrays.copyOf(pDimensions, pDimensions.length);
  }

  /**
   * Returns the stack's data type
   * 
   * @return data type
   */
  public NativeTypeEnum getDataType()
  {
    return mDataType;
  }

  /**
   * Returns the stack's dimensions
   * 
   * @return dimensions
   */
  public long[] getDimensions()
  {
    return mDimensions;
  }

  @Override
  public int getNumberOfDimensions()
  {
    return getDimensions().length;
  }

  @Override
  public long getDimension(int pIndex)
  {
    return mDimensions[pIndex];
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
    if (getNumberOfDimensions() < 1)
      return 1;
    return getDimension(0);
  }

  @Override
  public long getHeight()
  {
    if (getNumberOfDimensions() < 2)
      return 1;
    return getDimension(1);
  }

  @Override
  public long getDepth()
  {
    if (getNumberOfDimensions() < 3)
      return 1;
    return getDimension(2);
  }

  @Override
  public long getBytesPerVoxel()
  {
    final long lBytesPerVoxel = Size.of(getDataType());
    return lBytesPerVoxel;
  }

  @Override
  public long getSizeInBytes()
  {
    final long lVolume = getVolume();
    return lVolume * getBytesPerVoxel();
  }

  /**
   * Returns the number of channels per voxel
   * 
   * @return number of channels
   */
  public long getNumberOfChannels()
  {
    return mNumberOfChannels;
  }

  @Override
  public void setMetaData(StackMetaData pMetaData)
  {
    mMetaData.addAll(pMetaData);
  }

  @Override
  public StackMetaData getMetaData()
  {
    return mMetaData;
  }

  @Override
  public void copyMetaDataFrom(final StackInterface pStack)
  {
    mMetaData = pStack.getMetaData().clone();
  }

  @Override
  public boolean isReleased()
  {
    return mIsReleased;
  }

  @Override
  public void setReleased(final boolean isReleased)
  {
    mIsReleased = isReleased;
  }

  @Override
  public void release()
  {
    if (mStackRecycler != null)
      mStackRecycler.release(this);
  }

  @Override
  public void setRecycler(final RecyclerInterface<StackInterface, StackRequest> pRecycler)
  {
    mStackRecycler = pRecycler;
  }

  @Override
  public void recycle(StackRequest pRequest)
  {
    getMetaData().clear();
  }

  @Override
  public String toString()
  {
    return String.format("StackBase [index=%d, timestamp=%d ns, voxeldim=[%g,%g,%g], is-released=%s, recycler=%s]",
                         getMetaData().getIndex(),
                         getMetaData().getTimeStampInNanoseconds(),
                         getMetaData().getVoxelDimX(),
                         getMetaData().getVoxelDimY(),
                         getMetaData().getVoxelDimZ(),
                         mIsReleased,
                         mStackRecycler);
  }

}
