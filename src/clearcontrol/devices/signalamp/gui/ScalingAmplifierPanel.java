package clearcontrol.devices.signalamp.gui;

import clearcontrol.devices.signalamp.ScalingAmplifierDeviceInterface;
import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;

public class ScalingAmplifierPanel extends CustomVariablePane
{

  public ScalingAmplifierPanel(ScalingAmplifierDeviceInterface pScalingAmplifierInterface)
  {
    super();

    double lMinGain = pScalingAmplifierInterface.getMinGain();
    double lMaxGain = pScalingAmplifierInterface.getMaxGain();

    addTab("");
    addSliderForVariable("Gain: ",
                         pScalingAmplifierInterface.getGainVariable(),
                         lMinGain,
                         lMaxGain,
                         0.001,
                         0.1 * (lMaxGain - lMinGain));

    double lMinOffset = pScalingAmplifierInterface.getMinOffset();
    double lMaxOffset = pScalingAmplifierInterface.getMaxOffset();

    addSliderForVariable("Offset: ",
                         pScalingAmplifierInterface.getOffsetVariable(),
                         lMinOffset,
                         lMaxOffset,
                         0.001,
                         0.1 * (lMaxOffset - lMinOffset));
  }

}
