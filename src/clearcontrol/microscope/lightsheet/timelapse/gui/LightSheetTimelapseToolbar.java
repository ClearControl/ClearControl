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
      VariableCheckBox lFuseStacksCheckBox =
                                           new VariableCheckBox("Fuse stacks",
                                                                pLightSheetTimelapse.getFuseStacksVariable());

      GridPane.setHalignment(lFuseStacksCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lFuseStacksCheckBox.getLabel(), 1);
      GridPane.setColumnSpan(lFuseStacksCheckBox.getCheckBox(), 1);

      GridPane.setColumnSpan(lFuseStacksCheckBox.getLabel(), 3);
      add(lFuseStacksCheckBox.getCheckBox(), 0, mRow);
      add(lFuseStacksCheckBox.getLabel(), 1, mRow);

      mRow++;
    }

    {
      VariableCheckBox lFuseStacksPerCameraOnlyCheckBox =
                                                        new VariableCheckBox("Fuse stacks per camera only",
                                                                             pLightSheetTimelapse.getFuseStacksPerCameraVariable());

      GridPane.setHalignment(lFuseStacksPerCameraOnlyCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lFuseStacksPerCameraOnlyCheckBox.getLabel(),
                             1);
      GridPane.setColumnSpan(lFuseStacksPerCameraOnlyCheckBox.getCheckBox(),
                             1);

      GridPane.setColumnSpan(lFuseStacksPerCameraOnlyCheckBox.getLabel(),
                             3);
      add(lFuseStacksPerCameraOnlyCheckBox.getCheckBox(), 0, mRow);
      add(lFuseStacksPerCameraOnlyCheckBox.getLabel(), 1, mRow);

      mRow++;
    }

  }

}
