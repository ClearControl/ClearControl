package clearcontrol.microscope.lightsheet.state.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
import clearcontrol.microscope.lightsheet.component.detection.DetectionArm;
import clearcontrol.microscope.lightsheet.component.lightsheet.LightSheet;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.state.gui.AcquisitionStateManagerPanel;
import clearcontrol.microscope.state.AcquisitionStateManager;

/**
 * Acquisition state manager demo
 *
 * @author royer
 */
public class AcquisitionStateManagerPanelDemo extends Application
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

    final AcquisitionStateManager<InterpolatedAcquisitionState> lAcquisitionStateManager =
                                                                                         new AcquisitionStateManager<>(null);
    AcquisitionStateManagerPanel lAcquisitionStateManagerPanel =
                                                               new AcquisitionStateManagerPanel(lAcquisitionStateManager);

    InterpolatedAcquisitionState lState1 =
                                         new InterpolatedAcquisitionState("current",
                                                                          lLightSheetMicroscope);

    lState1.setupControlPlanes(3, 30);

    lAcquisitionStateManager.addState(lState1);

    for (int i = 0; i < 10; i++)
    {
      InterpolatedAcquisitionState lState =
                                          new InterpolatedAcquisitionState("State"
                                                                           + i,
                                                                           lLightSheetMicroscope);
      lState.setupControlPlanes(5, 20);

      lAcquisitionStateManager.addState(lState);
    }

    /*
    executeAsynchronously(() -> {
      try
      {
        for (int i = 0; i < 100; i++)
        {
          if (i % 50 == 0)
          {
            InterpolatedAcquisitionState lStateK =
                                                 new InterpolatedAcquisitionState("State2",
                                                                                  2,
                                                                                  4,
                                                                                  1);
            lStateK.setup(-100 + i, 50 + i, 100 + i, 2, 5, 5);
            lAcquisitionStateManager.addState(lStateK);
          }
          lState1.getInterpolationTables().set(LightSheetDOF.DZ, i);
    
          ThreadUtils.sleep(1, TimeUnit.SECONDS);
    
        }
      }
      catch (Throwable e)
      {
        e.printStackTrace();
      }
    });
    /**/

    Scene scene =
                new Scene(lAcquisitionStateManagerPanel, 1000, 1000);
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
