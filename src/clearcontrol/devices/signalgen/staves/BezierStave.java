package clearcontrol.devices.signalgen.staves;

import clearcontrol.core.math.interpolation.bezier.Bezier;

/**
 * Bezier Stave
 *
 * @author royer
 */
public class BezierStave extends StaveAbstract
                         implements StaveInterface
{

  private volatile float mStartValue, mStopValue, mStartSlope,
      mStopSlope, mSmoothness;

  /**
   * Instantiates a Bezier stave that is equivalent to a constant stave of given
   * value
   * 
   * @param pName
   *          name of stave
   * @param pValue
   *          value
   */
  public BezierStave(final String pName, float pValue)
  {
    super(pName);
    setStartValue(pValue);
    setStopValue(pValue);
    setStartSlope(0);
    setStopSlope(0);
    setSmoothness(0.5f);
  }

  /**
   * Instantiates a Bezier stave
   * 
   * @param pName
   *          name of stave
   * @param pValueStart
   *          start value
   * @param pValueEnd
   *          end value
   * @param pSlopeStart
   *          slope at start
   * @param pSlopeEnd
   *          slope at end
   * @param pSmoothness
   *          smoothness
   */
  public BezierStave(final String pName,
                     final float pValueStart,
                     final float pValueEnd,
                     final float pSlopeStart,
                     final float pSlopeEnd,
                     final float pSmoothness)
  {
    super(pName);
    setStartValue(pValueStart);
    setStopValue(pValueEnd);
    setStartSlope(pSlopeStart);
    setStopSlope(pSlopeEnd);
    setSmoothness(pSmoothness);

  }

  @Override
  public float getValue(float pNormalizedTime)
  {

    float lMargin = 0.2f;
    float lValue = 0;

    if (pNormalizedTime < lMargin)
    {
      lValue = getValueStart() + getSlopeStart() * pNormalizedTime;
    }
    else if (pNormalizedTime > 1 - lMargin)
    {
      lValue = (float) (getValueStop()
                        - getSlopeEnd() * (1 - pNormalizedTime));
    }
    else
    {
      float lBezierTime = (pNormalizedTime - lMargin)
                          / (1 - 2 * lMargin);
      float lBezierValueStart = getValueStart()
                                + getSlopeStart() * lMargin;
      float lBezierValueStop = getValueStop()
                               - getSlopeEnd() * (lMargin);

      float lControlValueStart = lBezierValueStart
                                 + getSlopeStart() * getSmoothness();
      float lControlValueEnd = lBezierValueStop
                               - getSlopeEnd() * getSmoothness();

      lValue = (float) Bezier.bezier(lBezierValueStart,
                                     lControlValueStart,
                                     lControlValueEnd,
                                     lBezierValueStop,
                                     lBezierTime);
    }

    return lValue;
  }

  @Override
  public StaveInterface copy()
  {
    return new BezierStave(getName(),
                           getValueStart(),
                           getValueStop(),
                           getSlopeStart(),
                           getSlopeEnd(),
                           getSmoothness());
  }

  public float getValueStart()
  {
    return mStartValue;
  }

  public void setStartValue(float pValueStart)
  {
    mStartValue = pValueStart;
  }

  public float getValueStop()
  {
    return mStopValue;
  }

  public void setStopValue(float pValueEnd)
  {
    mStopValue = pValueEnd;
  }

  public float getSlopeStart()
  {
    return mStartSlope;
  }

  public void setStartSlope(float pSlopeStart)
  {
    mStartSlope = pSlopeStart;
  }

  public float getSlopeEnd()
  {
    return mStopSlope;
  }

  public void setStopSlope(float pSlopeEnd)
  {
    mStopSlope = pSlopeEnd;
  }

  public float getSmoothness()
  {
    return mSmoothness;
  }

  public void setSmoothness(float pSmoothness)
  {
    mSmoothness = pSmoothness;
  }

}
