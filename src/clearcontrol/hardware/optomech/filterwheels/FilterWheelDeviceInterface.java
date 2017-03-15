package clearcontrol.hardware.optomech.filterwheels;

import clearcontrol.core.variable.Variable;
import clearcontrol.device.name.NameableInterface;
import clearcontrol.device.openclose.OpenCloseDeviceInterface;
import clearcontrol.device.position.PositionDeviceInterface;

public interface FilterWheelDeviceInterface extends
                                            NameableInterface,
                                            OpenCloseDeviceInterface,
                                            PositionDeviceInterface
{

  Variable<Integer> getSpeedVariable();

  int getSpeed();

  void setSpeed(int pSpeed);

}
