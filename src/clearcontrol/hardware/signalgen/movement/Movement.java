package clearcontrol.hardware.signalgen.movement;

import java.util.concurrent.TimeUnit;

import clearcontrol.device.name.NameableBase;
import clearcontrol.hardware.signalgen.staves.StaveInterface;
import clearcontrol.hardware.signalgen.staves.ZeroStave;

public class Movement extends NameableBase
                      implements MovementInterface
{

  public static final int cDefaultNumberOfStavesPerMovement = 16;

  private volatile long mDurationInNanoseconds;
  private final StaveInterface[] mStaveListArray;
  private volatile boolean mIsSync = false;
  private volatile boolean mIsSyncOnRisingEdge = false;
  private volatile int mSyncChannel = 0;

  public Movement(final String pName)
  {
    this(pName, cDefaultNumberOfStavesPerMovement);
  }

  public Movement(final String pName, final int pNumberOfStaves)
  {
    super(pName);
    mStaveListArray = new StaveInterface[pNumberOfStaves];
    for (int i = 0; i < pNumberOfStaves; i++)
    {
      mStaveListArray[i] = new ZeroStave();
    }
  }

  @Override
  public void setStave(final int pStaveIndex,
                       final StaveInterface pNewStave)
  {
    mStaveListArray[pStaveIndex] = pNewStave;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <O extends StaveInterface> O ensureSetStave(int pStaveIndex,
                                                     O pNewStave)
  {
    if (mStaveListArray[pStaveIndex] != null
        && !(mStaveListArray[pStaveIndex] instanceof ZeroStave))
      return (O) mStaveListArray[pStaveIndex];
    else
    {
      setStave(pStaveIndex, pNewStave);
      return pNewStave;
    }
  }

  @Override
  public StaveInterface getStave(final int pStaveIndex)
  {
    return mStaveListArray[pStaveIndex];
  }

  @Override
  public int getNumberOfStaves()
  {
    final int lNumberOfChannels = mStaveListArray.length;
    return lNumberOfChannels;
  }

  @Override
  public void setDuration(long pDuration, TimeUnit pTimeUnit)
  {
    mDurationInNanoseconds = TimeUnit.NANOSECONDS.convert(pDuration,
                                                          pTimeUnit);
  }

  @Override
  public long getDuration(TimeUnit pTimeUnit)
  {
    return pTimeUnit.convert(mDurationInNanoseconds,
                             TimeUnit.NANOSECONDS);
  }

  @Override
  public boolean isSync()
  {
    return mIsSync;
  }

  @Override
  public void setSync(boolean pIsSync)
  {
    mIsSync = pIsSync;
  }

  @Override
  public boolean isSyncOnRisingEdge()
  {
    return mIsSyncOnRisingEdge;
  }

  @Override
  public void setSyncOnRisingEdge(boolean pIsSyncOnRisingEdge)
  {
    mIsSyncOnRisingEdge = pIsSyncOnRisingEdge;
  }

  @Override
  public void setSyncChannel(int pSyncChannel)
  {
    mSyncChannel = pSyncChannel;
  }

  @Override
  public int getSyncChannel()
  {
    return mSyncChannel;
  }

  @Override
  public String toString()
  {
    return String.format("Movement[%s]", getName());
  }

  public static Movement getNullMovement()
  {
    final Movement lNullMovement = new Movement("NullMovement");
    return lNullMovement;
  }

  @Override
  public MovementInterface copy()
  {
    final MovementInterface lMovementCopy =
                                          new Movement(getName(),
                                                       getNumberOfStaves());
    lMovementCopy.setSync(isSync());
    lMovementCopy.setSyncChannel(getSyncChannel());
    lMovementCopy.setSyncOnRisingEdge(isSyncOnRisingEdge());
    lMovementCopy.setDuration(getDuration(TimeUnit.NANOSECONDS),
                              TimeUnit.NANOSECONDS);

    for (int i = 0; i < mStaveListArray.length; i++)
    {
      final StaveInterface lStaveInterface = mStaveListArray[i];
      lMovementCopy.setStave(i, lStaveInterface.copy());
    }

    return lMovementCopy;
  }

}
