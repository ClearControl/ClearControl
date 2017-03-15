package clearcontrol.hardware.signalgen.staves;

import static java.lang.Math.floor;

public class RampSteppingStave extends RampContinuousStave
                               implements StaveInterface
{

  private volatile boolean mStepping = true;
  private volatile float mStepHeight;
  private volatile int mNumberOfSteps;

  public RampSteppingStave(final String pName)
  {
    super(pName);
  }

  public RampSteppingStave(final String pName,
                           float pSyncStart,
                           float pSyncStop,
                           float pStartValue,
                           float pStopValue,
                           float pOutsideValue,
                           float pStepHeight)
  {
    super(pName,
          pSyncStart,
          pSyncStop,
          pStartValue,
          pStopValue,
          pOutsideValue,
          1);
    setStepHeight(pStepHeight);
  }

  public RampSteppingStave(final String pName,
                           float pSyncStart,
                           float pSyncStop,
                           float pStartValue,
                           float pStopValue,
                           float pOutsideValue,
                           float pExponent,
                           float pStepHeight)
  {
    super(pName,
          pSyncStart,
          pSyncStop,
          pStartValue,
          pStopValue,
          pOutsideValue,
          pExponent);
    // important next line must be after all others!
    setStepHeight(pStepHeight);
  }

  @Override
  public float getValue(float pNormalizedTime)
  {
    if (!isStepping())
      return super.getValue(pNormalizedTime);

    if (pNormalizedTime < getSyncStart()
        || pNormalizedTime > getSyncStop())
      return getOutsideValue();

    float lNormalizedRampTime = (pNormalizedTime - getSyncStart())
                                / (getSyncStop() - getSyncStart());

    if (getExponent() != 1)
    {
      lNormalizedRampTime =
                          abspow(lNormalizedRampTime, getExponent());
    }

    final float lNormalizedSteppingRampTime =
                                            (float) (floor(getNumberOfSteps()
                                                           * lNormalizedRampTime)
                                                     / getNumberOfSteps());

    final float lValue = getStartValue()
                         + (getStopValue() - getStartValue())
                           * lNormalizedSteppingRampTime;
    return lValue;

  }

  public float getStepHeight()
  {
    return mStepHeight;
  }

  public void setStepHeight(float pStepHeight)
  {
    mStepHeight = pStepHeight;
    mNumberOfSteps = (int) floor(getRampHeight() / mStepHeight);
  }

  public int getNumberOfSteps()
  {
    return mNumberOfSteps;
  }

  public boolean isStepping()
  {
    return mStepping;
  }

  public void setStepping(boolean pStepping)
  {
    mStepping = pStepping;
  }

  @Override
  public StaveInterface copy()
  {
    final RampSteppingStave lRampSteppingStave =
                                               new RampSteppingStave(getName(),
                                                                     getSyncStart(),
                                                                     getSyncStop(),
                                                                     getStartValue(),
                                                                     getStopValue(),
                                                                     getOutsideValue(),
                                                                     getStepHeight());

    lRampSteppingStave.setStepping(isStepping());
    lRampSteppingStave.setExponent(getExponent());

    return lRampSteppingStave;
  }

}
