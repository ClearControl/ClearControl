package clearcontrol.hardware.signalamp;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;

public interface ScalingAmplifierDeviceInterface extends
                                                 NameableInterface
{
  public double getMinGain();

  public double getMaxGain();

  public double getMinOffset();

  public double getMaxOffset();

  public void setGain(double pGain);

  public void setOffset(double pOffset);

  public double getGain();

  public double getOffset();

  public Variable<Number> getGainVariable();

  public Variable<Number> getOffsetVariable();

}
