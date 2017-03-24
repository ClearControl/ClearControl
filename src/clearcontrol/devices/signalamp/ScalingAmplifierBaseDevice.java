package clearcontrol.devices.signalamp;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;

public class ScalingAmplifierBaseDevice extends VirtualDevice
                                        implements
                                        ScalingAmplifierDeviceInterface
{

  protected double mMinGain = Double.NEGATIVE_INFINITY,
      mMaxGain = Double.POSITIVE_INFINITY,
      mMinOffset = Double.NEGATIVE_INFINITY,
      mMaxOffset = Double.POSITIVE_INFINITY;

  protected Variable<Number> mGainVariable, mOffsetVariable;

  public ScalingAmplifierBaseDevice(String pDeviceName)
  {
    super(pDeviceName);
  }

  @Override
  public void setGain(double pGain)
  {
    mGainVariable.set(pGain);
  }

  @Override
  public void setOffset(double pOffset)
  {
    mOffsetVariable.set(pOffset);
  }

  @Override
  public double getGain()
  {
    return mGainVariable.get().doubleValue();
  }

  @Override
  public double getOffset()
  {
    return mOffsetVariable.get().doubleValue();
  }

  @Override
  public Variable<Number> getGainVariable()
  {
    return mGainVariable;
  }

  @Override
  public Variable<Number> getOffsetVariable()
  {
    return mOffsetVariable;
  }

  @Override
  public double getMinGain()
  {
    return mMinGain;
  }

  @Override
  public double getMaxGain()
  {
    return mMaxGain;
  }

  @Override
  public double getMinOffset()
  {
    return mMinOffset;
  }

  @Override
  public double getMaxOffset()
  {
    return mMaxOffset;
  }

}
