package clearcontrol.devices.signalgen.staves;

import static java.lang.Math.floor;

public abstract class PatternSteppingStave extends StaveAbstract
                                           implements StaveInterface
{

  private volatile float mSyncStart = 0;
  private volatile float mSyncStop = 1;
  private volatile int mNumberOfSteps = 1024;

  public PatternSteppingStave(final String pName)
  {
    super(pName);
  }

  public PatternSteppingStave(final String pName,
                              float pSyncStart,
                              float pSyncStop,
                              int pNumberOfSteps)
  {
    super(pName);
    setNumberOfSteps(pNumberOfSteps);
    setSyncStart(pSyncStart);
    setSyncStop(pSyncStop);
  }

  @Override
  public abstract StaveInterface duplicate();

  @Override
  public float getValue(float pNormalizedTime)
  {
    if (!isEnabled())
      return 1;

    if (pNormalizedTime < getSyncStart()
        || pNormalizedTime > getSyncStop())
      return 0;

    final float lNormalizedRampTime =
                                    (pNormalizedTime - getSyncStart())
                                      / (getSyncStop()
                                         - getSyncStart());

    final int lNormalizedSteppingRampTime =
                                          (int) floor(getNumberOfSteps()
                                                      * lNormalizedRampTime);

    return function(lNormalizedSteppingRampTime);
  }

  public abstract float function(int pIndex);

  public float getSyncStart()
  {
    return mSyncStart;
  }

  public void setSyncStart(float pSyncStart)
  {
    mSyncStart = pSyncStart;
  }

  public float getSyncStop()
  {
    return mSyncStop;
  }

  public void setSyncStop(float pSyncStop)
  {
    mSyncStop = pSyncStop;
  }

  public int getNumberOfSteps()
  {
    return mNumberOfSteps;
  }

  public void setNumberOfSteps(int pNumberOfSteps)
  {
    mNumberOfSteps = pNumberOfSteps;
  }

}
