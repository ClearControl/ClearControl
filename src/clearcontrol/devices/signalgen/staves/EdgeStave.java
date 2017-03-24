package clearcontrol.devices.signalgen.staves;

public class EdgeStave extends IntervalStave implements StaveInterface
{

  public EdgeStave(final String pName,
                   float pEdgePosition,
                   float mValueBefore,
                   float mValueAfter)
  {
    super(pName);
    setEdgePosition(pEdgePosition);
    this.setValueBefore(mValueBefore);
    this.setValueAfter(mValueAfter);
  }

  @Override
  public float getValue(float pNormalizedTime)
  {
    if (isEnabled() && pNormalizedTime > getEdgePosition())
      return getValueAfter();
    else
      return getValueBefore();
  }

  public float getEdgePosition()
  {
    return getStart();
  }

  public void setEdgePosition(float pEdgePosition)
  {
    setStart(pEdgePosition);
  }

  public float getValueBefore()
  {
    return getOutsideValue();
  }

  public void setValueBefore(float pValueBefore)
  {
    setOutsideValue(pValueBefore);
  }

  public float getValueAfter()
  {
    return getInsideValue();
  }

  public void setValueAfter(float pValueAfter)
  {
    setInsideValue(pValueAfter);
  }

  @Override
  public StaveInterface copy()
  {
    return new EdgeStave(getName(),
                         getEdgePosition(),
                         getValueBefore(),
                         getValueAfter());
  }

}
