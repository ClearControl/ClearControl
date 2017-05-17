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
      mStopSlope, mSmoothness, mMargin;

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
   * @param pMargin
   *          margin parameter
   */
  public BezierStave(final String pName,
                     final float pValueStart,
                     final float pValueEnd,
                     final float pSlopeStart,
                     final float pSlopeEnd,
                     final float pSmoothness,
                     final float pMargin)
  {
    super(pName);
    setStartValue(pValueStart);
    setStopValue(pValueEnd);
    setStartSlope(pSlopeStart);
    setStopSlope(pSlopeEnd);
    setSmoothness(pSmoothness);
    setMargin(pMargin);
  }

  @Override
  public StaveInterface duplicate()
  {
    return new BezierStave(getName(),
                           getValueStart(),
                           getValueStop(),
                           getSlopeStart(),
                           getSlopeEnd(),
                           getSmoothness(),
                           getMargin());
  }

  @Override
  public float getValue(float pNormalizedTime)
  {

    float lValue = 0;

    if (pNormalizedTime < getMargin())
    {
      lValue = getValueStart() + getSlopeStart() * pNormalizedTime;
    }
    else if (pNormalizedTime > 1 - getMargin())
    {
      lValue = getValueStop() - getSlopeEnd() * (1 - pNormalizedTime);
    }
    else
    {
      float lBezierTime = (pNormalizedTime - getMargin())
                          / (1 - 2 * getMargin());
      float lBezierValueStart = getValueStart()
                                + getSlopeStart() * getMargin();
      float lBezierValueStop = getValueStop()
                               - getSlopeEnd() * (getMargin());

      float lControlValueStart = lBezierValueStart
                                 + getSlopeStart() * getSmoothness()
                                   * (1 - 2 * getMargin());
      float lControlValueEnd = lBezierValueStop
                               - getSlopeEnd() * getSmoothness()
                                 * (1 - 2 * getMargin());

      lValue = (float) Bezier.bezier(lBezierValueStart,
                                     lControlValueStart,
                                     lControlValueEnd,
                                     lBezierValueStop,
                                     lBezierTime);
    }

    return lValue;
  }



  /**
   * Returns the value at the beginning of the stave.
   * 
   * @return start value
   */
  public float getValueStart()
  {
    return mStartValue;
  }

  /**
   * Sets the value at the start of the stave
   * 
   * @param pValueStart
   *          start value
   */
  public void setStartValue(float pValueStart)
  {
    mStartValue = pValueStart;
  }

  /**
   * Returns the value at the end of the stave
   * 
   * @return stop value
   */
  public float getValueStop()
  {
    return mStopValue;
  }

  /**
   * Sets the value at the end of the stave
   * 
   * @param pValueStop
   *          stop value
   */
  public void setStopValue(float pValueStop)
  {
    mStopValue = pValueStop;
  }

  /**
   * Returns the slope at the start of the stave
   * 
   * @return start slope
   */
  public float getSlopeStart()
  {
    return mStartSlope;
  }

  /**
   * Sets the slope at the start of the save.
   * 
   * @param pSlopeStart
   *          start slope
   */
  public void setStartSlope(float pSlopeStart)
  {
    mStartSlope = pSlopeStart;
  }

  /**
   * Returns the slope at the end of the stave
   * 
   * @return end slope
   */
  public float getSlopeEnd()
  {
    return mStopSlope;
  }

  /**
   * Sets the slope at the end of the stave
   * 
   * @param pSlopeStop
   *          end slope
   */
  public void setStopSlope(float pSlopeStop)
  {
    mStopSlope = pSlopeStop;
  }

  /**
   * Returns the smoothness parameter
   * 
   * @return smoothness parameter within [0,1]
   */
  public float getSmoothness()
  {
    return mSmoothness;
  }

  /**
   * Sets the smoothness parameter
   * 
   * @param pSmoothness
   *          smoothness parameter within [0,1]
   */
  public void setSmoothness(float pSmoothness)
  {
    mSmoothness = pSmoothness;
  }

  /**
   * Returns margin parameter
   * 
   * @return margin parameter
   */
  public float getMargin()
  {
    return mMargin;
  }

  /**
   * Sets the margin parameter
   * 
   * @param pMargin
   *          margin parameter
   */
  public void setMargin(float pMargin)
  {
    mMargin = pMargin;
  }

}
