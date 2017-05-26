package clearcontrol.microscope.lightsheet.processor.gui.demo;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.processor.LightSheetFastFusionProcessor;
import clearcontrol.microscope.lightsheet.processor.gui.LightSheetFastFusionProcessorPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Acquisition state manager demo
 *
 * @author royer
 */
public class LightSheetFastFusionProcessorPanelDemo extends Application
                                              implements
                                              AsynchronousExecutorServiceAccess
{

  @Override
  public void start(Stage stage)
  {

    LightSheetMicroscope lLightSheetMicroscope =
                                               new LightSheetMicroscope("Dummy",
                                                                        null,
                                                                        1,
                                                                        1);
    DetectionArm lDetectionArm0 = new DetectionArm("D0");
    DetectionArm lDetectionArm1 = new DetectionArm("D1");
    lLightSheetMicroscope.addDevice(0, lDetectionArm0);
    lLightSheetMicroscope.addDevice(1, lDetectionArm1);

    LightSheet lLightSheet0 = new LightSheet("L0", 1, 2);
    LightSheet lLightSheet1 = new LightSheet("L1", 1, 2);
    lLightSheetMicroscope.addDevice(0, lLightSheet0);
    lLightSheetMicroscope.addDevice(1, lLightSheet1);

    LightSheetFastFusionProcessor lProcessor =
                                             new LightSheetFastFusionProcessor("processor",
                                                                               lLightSheetMicroscope,
                                                                               null);
    
    LightSheetFastFusionProcessorPanel lPanel =
                                              new LightSheetFastFusionProcessorPanel(lProcessor);
    


    Scene scene =
                new Scene(lPanel, 500, 500);
    stage.setScene(scene);
    stage.setTitle("Interactive2DAcquisitionPanel Demo");

    stage.show();

  }

  /**
   * Main
   * 
   * @param args
   *          NA
   * 
   */
  public static void main(String[] args)
  {

    launch(args);
  }
}
