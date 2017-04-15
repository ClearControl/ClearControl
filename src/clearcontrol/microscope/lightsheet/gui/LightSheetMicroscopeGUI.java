package clearcontrol.microscope.lightsheet.gui;

import clearcontrol.microscope.gui.MicroscopeGUI;
import clearcontrol.microscope.gui.halcyon.MicroscopeNodeType;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.AcquisitionStateManagerPanel;
import clearcontrol.microscope.lightsheet.calibrator.Calibrator;
import clearcontrol.microscope.lightsheet.calibrator.gui.CalibratorToolbar;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArmInterface;
import clearcontrol.microscope.lightsheet.component.detection.gui.jfx.DetectionArmPanel;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheetInterface;
import clearcontrol.microscope.lightsheet.component.lightsheet.gui.LightSheetPanel;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.interactive.gui.InteractiveAcquisitionPanel;
import clearcontrol.microscope.lightsheet.interactive.gui.InteractiveAcquisitionToolbar;
import clearcontrol.microscope.lightsheet.timelapse.LightSheetTimelapse;
import clearcontrol.microscope.lightsheet.timelapse.gui.LightSheetTimelapseToolbar;
import clearcontrol.microscope.state.AcquisitionStateManager;
import clearcontrol.microscope.timelapse.gui.TimelapsePanel;
import clearcontrol.microscope.timelapse.timer.TimelapseTimerInterface;

/**
 * Lightsheet microscope Ggraphical User Interface (GUI)
 *
 * @author royer
 */
public class LightSheetMicroscopeGUI extends MicroscopeGUI
{

  /**
   * Instanciates a lightsheet microscope GUI given a lightsheet microscope and
   * two flags determining whether to setup 2D and 3D displays.
   * 
   * @param pLightSheetMicroscope
   *          lightsheet microscope
   * @param p2DDisplay
   *          true -> setup 2D display
   * @param p3DDisplay
   *          true -> setup 3D display
   */
  public LightSheetMicroscopeGUI(LightSheetMicroscope pLightSheetMicroscope,
                                 boolean p2DDisplay,
                                 boolean p3DDisplay)
  {
    super(pLightSheetMicroscope,
          LSMNodeType.values(),
          p2DDisplay,
          p3DDisplay);

    addPanelMappingEntry(LightSheetInterface.class,
                         LightSheetPanel.class,
                         LSMNodeType.LightSheet);

    addPanelMappingEntry(DetectionArmInterface.class,
                         DetectionArmPanel.class,
                         LSMNodeType.DetectionArm);

    addPanelMappingEntry(InteractiveAcquisition.class,
                         InteractiveAcquisitionPanel.class,
                         MicroscopeNodeType.Acquisition);

    addPanelMappingEntry(AcquisitionStateManager.class,
                         AcquisitionStateManagerPanel.class,
                         MicroscopeNodeType.Acquisition);

    addPanelMappingEntry(TimelapseTimerInterface.class,
                         TimelapsePanel.class,
                         MicroscopeNodeType.Acquisition);

    /*addHalcyonMappingEntry(	AutoPilotInterface.class,
    												AutoPilotPanel.class,
    												MicroscopeNodeType.Acquisition);/**/

    addToolbarMappingEntry(InteractiveAcquisition.class,
                           InteractiveAcquisitionToolbar.class);

    addToolbarMappingEntry(Calibrator.class, CalibratorToolbar.class);

    addToolbarMappingEntry(LightSheetTimelapse.class,
                           LightSheetTimelapseToolbar.class);

  }

  @Override
  public void setup()
  {
    super.setup();
  }

}
