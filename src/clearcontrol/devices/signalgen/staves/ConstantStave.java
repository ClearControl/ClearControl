package clearcontrol.devices.signalgen.staves;

public class ConstantStave extends StaveAbstract
                           implements StaveInterface
{
  private volatile float mConstantValue;

  public ConstantStave(final String pName, final float pValue)
  {
    super(pName);
    setValue(pValue);
  }

  @Override
  public StaveInterface duplicate()
  {
    return new ConstantStave(getName(), getConstantValue());
  }

  @Override
  public float getValue(float pNormalizedTime)
  {
    return mConstantValue;
  }

  public float getConstantValue()
  {
    return mConstantValue;
  }

  public void setValue(float pValue)
  {
    mConstantValue = pValue;
  }



}
