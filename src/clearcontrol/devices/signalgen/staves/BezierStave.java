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

    float lControlValueStart = getValueStart()
                               + getSlopeStart() * getSmoothness();
    float lControlValueEnd = getValueEnd()
                             - getSlopeEnd() * getSmoothness();

    final float lBezierValue = (float) Bezier.bezier(getValueStart(),
                                                     lControlValueStart,
                                                     lControlValueEnd,
                                                     getValueEnd(),
                                                     pNormalizedTime);

    return lBezierValue;
  }

  @Override
  public StaveInterface copy()
  {
    return new BezierStave(getName(),
                           getValueStart(),
                           getValueEnd(),
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

  public float getValueEnd()
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
