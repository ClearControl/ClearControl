package clearcontrol.microscope.lightsheet.signalgen.gui;

import javafx.scene.control.CheckBox;

import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.microscope.lightsheet.signalgen.LightSheetSignalGeneratorDevice;

/**
 * Lightsheet signal generator panel
 * 
 * @author royer
 */
public class LightSheetSignalGeneratorPanel extends CustomVariablePane

{

  /**
   * Instantiates an interactive acquisition panel for a given interactive
   * acquisition device
   * 
   * @param pLightSheetSignalGeneratorDevice
   *          lightsheet signal generator
   */
  public LightSheetSignalGeneratorPanel(LightSheetSignalGeneratorDevice pLightSheetSignalGeneratorDevice)
  {
    super();

    addTab("DOFs");

    CheckBox lCheckBox =
                       addCheckBoxForVariable("Shared lightsheet control:",
                                              pLightSheetSignalGeneratorDevice.getIsSharedLightSheetControlVariable()).getCheckBox();
    lCheckBox.setStyle("-fx-opacity: 1");
    lCheckBox.setDisable(true);

    addIntComboBox("Selected lightsheet: ",
                   pLightSheetSignalGeneratorDevice.getSelectedLightSheetIndexVariable(),
                   0,
                   8);

    /*add("Control illumination:",
                           pLightSheetSignalGeneratorDevice.getControlIlluminationVariable());/**/

  }

}
