package clearcontrol.hardware.stages.gui.jfx;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.hardware.stages.StageDeviceInterface;

/**
 * Stage 3D Control
 */
public class GenericStageDevicePanel extends CustomVariablePane
{

  private StageDeviceInterface mStageDeviceInterface;

  public GenericStageDevicePanel(StageDeviceInterface pStageDeviceInterface)
  {
    mStageDeviceInterface = pStageDeviceInterface;

    int lNumberOfDOFs = mStageDeviceInterface.getNumberOfDOFs();

    addTab(pStageDeviceInterface.getName());

    for (int i = 0; i < lNumberOfDOFs; i++)
    {
      String lDOFName = mStageDeviceInterface.getDOFNameByIndex(i);
      Variable<Double> lTargetPositionVariable =
                                               mStageDeviceInterface.getTargetPositionVariable(i);
      Variable<Double> lCurrentPositionVariable =
                                                mStageDeviceInterface.getCurrentPositionVariable(i);
      Variable<Double> lMinPositionVariable =
                                            mStageDeviceInterface.getMinPositionVariable(i);
      Variable<Double> lMaxPositionVariable =
                                            mStageDeviceInterface.getMaxPositionVariable(i);
      Variable<Double> lPositionGranularityVariable =
                                                    mStageDeviceInterface.getGranularityPositionVariable(i);
      Variable<Boolean> lEnableVariable =
                                        mStageDeviceInterface.getEnableVariable(i);
      Variable<Boolean> lHomeVariable =
                                      mStageDeviceInterface.getEnableVariable(i);

      addSliderForVariable("Target " + lDOFName,
                           lTargetPositionVariable,
                           lMinPositionVariable,
                           lMaxPositionVariable,
                           lPositionGranularityVariable,
                           5d);
      VariableSlider<Double> lCurrentPosSlider =
                                               addSliderForVariable("Current "
                                                                    + lDOFName,
                                                                    lCurrentPositionVariable,
                                                                    lMinPositionVariable,
                                                                    lMaxPositionVariable,
                                                                    lPositionGranularityVariable,
                                                                    5d);

      lCurrentPosSlider.setMouseTransparent(true);
      lCurrentPosSlider.setDisable(true);

    }

  }

}
