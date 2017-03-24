package clearcontrol.microscope.lightsheet.component.lightsheet;

import clearcontrol.core.device.VirtualDevice;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.devices.optomech.opticalswitch.OpticalSwitchDeviceInterface;

public class LightSheetOpticalSwitch extends VirtualDevice implements
                                     OpticalSwitchDeviceInterface
{

  private final Variable<Boolean>[] mLightSheetOnOff;

  @SuppressWarnings("unchecked")
  public LightSheetOpticalSwitch(String pName,
                                 int pNumberOfLightSheets)
  {
    super(pName);

    final VariableSetListener<Boolean> lBooleanVariableListener =
                                                                (u,
                                                                 v) -> {

                                                                  if (u != v)
                                                                  {
                                                                    notifyListeners(this);
                                                                  }
                                                                };

    mLightSheetOnOff = new Variable[pNumberOfLightSheets];

    for (int i = 0; i < mLightSheetOnOff.length; i++)
    {
      mLightSheetOnOff[i] = new Variable<Boolean>(
                                                  String.format("LightSheet%dOnOff",
                                                                i),
                                                  false);
      mLightSheetOnOff[i].addSetListener(lBooleanVariableListener);
    }

  }

  @Override
  public int getNumberOfSwitches()
  {
    return mLightSheetOnOff.length;
  }

  @Override
  public Variable<Boolean> getSwitchVariable(int pLightSheetIndex)
  {
    return mLightSheetOnOff[pLightSheetIndex];
  }

  @Override
  public String getSwitchName(int pSwitchIndex)
  {
    return "light sheet " + pSwitchIndex;
  }

}
