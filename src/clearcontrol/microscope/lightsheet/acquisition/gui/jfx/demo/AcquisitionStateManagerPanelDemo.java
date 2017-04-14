package clearcontrol.microscope.lightsheet.acquisition.gui.jfx.demo;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorServiceAccess;
import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import clearcontrol.microscope.lightsheet.acquisition.gui.jfx.AcquisitionStateManagerPanel;
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

    final AcquisitionStateManager lAcquisitionStateManager =
                                                           new AcquisitionStateManager(null);
    AcquisitionStateManagerPanel lAcquisitionStateManagerPanel =
                                                               new AcquisitionStateManagerPanel(lAcquisitionStateManager);

    InterpolatedAcquisitionState lState1 =
                                         new InterpolatedAcquisitionState("State1",
                                                                          2,
                                                                          4,
                                                                          1);
    InterpolatedAcquisitionState lState2 =
                                         new InterpolatedAcquisitionState("State2",
                                                                          2,
                                                                          4,
                                                                          1);

    lState1.setup(0, 50, 100, 1, 5, 5);
    lState2.setup(-100, 50, 100, 2, 5, 5);

    lAcquisitionStateManager.addState(lState1);
    lAcquisitionStateManager.addState(lState2);

    executeAsynchronously(() -> {
      try
      {
        for (int i = 0; i < 100; i++)
        {
          if (i % 5 == 0)
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
