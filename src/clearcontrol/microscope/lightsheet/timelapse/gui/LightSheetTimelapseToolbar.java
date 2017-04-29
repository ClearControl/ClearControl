package clearcontrol.microscope.lightsheet.timelapse.gui;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;

import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.microscope.lightsheet.timelapse.LightSheetTimelapse;
import clearcontrol.microscope.timelapse.gui.TimelapseToolbar;

/**
 * Lightsheet Timelapse toolbar
 *
 * @author royer
 */
public class LightSheetTimelapseToolbar extends TimelapseToolbar
{
  /**
   * Instanciates a lightsheet timelapse toolbar.
   * 
   * @param pLightSheetTimelapse
   *          timelapse device
   */
  public LightSheetTimelapseToolbar(LightSheetTimelapse pLightSheetTimelapse)
  {
    super(pLightSheetTimelapse);

    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 4);
      add(lSeparator, 0, mRow);
      mRow++;
    }

    {
      VariableCheckBox lInterleavedAcquisition =
                                               new VariableCheckBox("",
                                                                    pLightSheetTimelapse.getInterleavedAcquisitionVariable());

      Label lInterleavedAcquisitionLabel =
                                         new Label("Interleaved acquisition");

      GridPane.setHalignment(lInterleavedAcquisition.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lInterleavedAcquisition.getCheckBox(),
                             1);
      GridPane.setColumnSpan(lInterleavedAcquisitionLabel, 3);

      add(lInterleavedAcquisition.getCheckBox(), 0, mRow);
      add(lInterleavedAcquisitionLabel, 1, mRow);
      mRow++;
    }

    {
      VariableCheckBox lEnforceMaxDateTimeCheckBox =
                                                   new VariableCheckBox("Fuse Stacks",
                                                                        pLightSheetTimelapse.getFuseStacksVariable());

      GridPane.setHalignment(lEnforceMaxDateTimeCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lEnforceMaxDateTimeCheckBox.getLabel(),
                             1);
      GridPane.setColumnSpan(lEnforceMaxDateTimeCheckBox.getCheckBox(),
                             1);

      add(lEnforceMaxDateTimeCheckBox.getCheckBox(), 0, mRow);
      add(lEnforceMaxDateTimeCheckBox.getLabel(), 1, mRow);

      mRow++;
    }

  }

}
