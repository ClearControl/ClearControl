package clearcontrol.devices.signalgen.devices.nirio.compiler;

import java.util.concurrent.locks.ReentrantLock;

import coremem.buffers.ContiguousBuffer;

public class NIRIOCompiledScore

{

  private volatile long mNumberOfMeasures;
  private ContiguousBuffer mDeltaTimeBuffer;
  private ContiguousBuffer mSyncBuffer;
  private ContiguousBuffer mNumberOfTimePointsBuffer;
  private ContiguousBuffer mScoreBuffer;

  public ReentrantLock mReentrantLock = new ReentrantLock();

  public NIRIOCompiledScore()
  {
  }

  @Override
  public String toString()
  {
    return String.format("NIRIOCompiledScore:\n mNumberOfMeasures=%s\n mDeltaTimeBuffer=%s\n mSyncBuffer=%s\n mNumberOfTimePointsBuffer=%s\n mScoreBuffer=%s\n\n",
                         getNumberOfMeasures(),
                         getDeltaTimeBuffer(),
                         getSyncBuffer(),
                         getNumberOfTimePointsBuffer(),
                         getScoreBuffer());
  }

  public ContiguousBuffer getDeltaTimeBuffer()
  {
    return mDeltaTimeBuffer;
  }

  public void setDeltaTimeBuffer(ContiguousBuffer pDeltaTimeBuffer)
  {
    mDeltaTimeBuffer = pDeltaTimeBuffer;
  }

  public ContiguousBuffer getSyncBuffer()
  {
    return mSyncBuffer;
  }

  public void setSyncBuffer(ContiguousBuffer pSyncBuffer)
  {
    mSyncBuffer = pSyncBuffer;
  }

  public ContiguousBuffer getNumberOfTimePointsBuffer()
  {
    return mNumberOfTimePointsBuffer;
  }

  public void setNumberOfTimePointsBuffer(ContiguousBuffer pNumberOfTimePointsBuffer)
  {
    mNumberOfTimePointsBuffer = pNumberOfTimePointsBuffer;
  }

  public ContiguousBuffer getScoreBuffer()
  {
    return mScoreBuffer;
  }

  public void setScoreBuffer(ContiguousBuffer pScoreBuffer)
  {
    mScoreBuffer = pScoreBuffer;
  }

  public void setNumberOfMeasures(long pNumberOfMeasures)
  {
    mNumberOfMeasures = pNumberOfMeasures;
  }

  public long getNumberOfMeasures()
  {
    return mNumberOfMeasures;
  }
}
