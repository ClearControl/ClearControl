package clearcontrol.devices.signalgen.staves;

public class IntervalStave extends StaveAbstract
                           implements StaveInterface
{

  private volatile float mStart = 0;
  private volatile float mStop = 1;
  private volatile float mInsideValue = 1;
  private volatile float mOutsideValue = 0;

  public IntervalStave(final String pName)
  {
    super(pName);
  }

  public IntervalStave(final String pName,
                       float pSyncStart,
                       float pSyncStop,
                       float pInsideValue,
                       float pOutsideValue)
  {
    super(pName);
    setStart(pSyncStart);
    setStop(pSyncStop);
    setInsideValue(pInsideValue);
    setOutsideValue(pOutsideValue);
  }

  @Override
  public StaveInterface duplicate()
  {
    return new IntervalStave(getName(),
                             getStart(),
                             getStop(),
                             getInsideValue(),
                             getOutsideValue());
  }

  @Override
  public float getValue(float pNormalizedTime)
  {
    if ((pNormalizedTime < getStart())
        || (pNormalizedTime > getStop()) || !isEnabled())
      return getOutsideValue();
    else
      return getInsideValue();
  }

  public float getStart()
  {
    return mStart;
  }

  public void setStart(float pStart)
  {
    mStart = pStart;
  }

  public float getStop()
  {
    return mStop;
  }

  public void setStop(float pStop)
  {
    mStop = pStop;
  }

  public float getInsideValue()
  {
    return mInsideValue;
  }

  public void setInsideValue(float pIntervalValue)
  {
    mInsideValue = pIntervalValue;
  }

  public float getOutsideValue()
  {
    return mOutsideValue;
  }

  public void setOutsideValue(float pOutsideValue)
  {
    mOutsideValue = pOutsideValue;
  }

}
