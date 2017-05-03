package clearcontrol.microscope.lightsheet.state.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.microscope.lightsheet.LightSheetMicroscope;
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

    final AcquisitionStateManager<InterpolatedAcquisitionState> lAcquisitionStateManager =
                                                                                         new AcquisitionStateManager<>(null);
    AcquisitionStateManagerPanel lAcquisitionStateManagerPanel =
                                                               new AcquisitionStateManagerPanel(lAcquisitionStateManager);

    InterpolatedAcquisitionState lState1 =
                                         new InterpolatedAcquisitionState("State1",
                                                                          lLightSheetMicroscope);
    InterpolatedAcquisitionState lState2 =
                                         new InterpolatedAcquisitionState("State2",
                                                                          lLightSheetMicroscope);

    lState1.setup(0, 50, 100, 1, 5, 5);
    lState2.setup(-100, 50, 100, 2, 5, 5);

    lAcquisitionStateManager.addState(lState1);
    lAcquisitionStateManager.addState(lState2);

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
