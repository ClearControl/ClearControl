package clearcontrol.stack;

import clearcontrol.stack.metadata.StackMetaData;
import coremem.recycling.RecyclerInterface;
import coremem.rgc.FreeableBase;

/**
 * Base class providing cmmon fields and methods for all stack implementations
 *
 * @author royer
 */
public abstract class StackBase extends FreeableBase
                                implements StackInterface
{

  protected RecyclerInterface<StackInterface, StackRequest> mStackRecycler;
  protected volatile boolean mIsReleased;

  protected StackMetaData mMetaData = new StackMetaData();

  /**
   * Instanciates a non-initialized stack
   */
  public StackBase()
  {
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
