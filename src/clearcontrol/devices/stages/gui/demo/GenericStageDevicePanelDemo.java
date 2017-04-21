package clearcontrol.devices.stages.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.devices.stages.StageType;
import clearcontrol.devices.stages.devices.sim.StageDeviceSimulator;
import clearcontrol.devices.stages.gui.StageDevicePanel;

/**
 * generic stage panel demo
 *
 * @author royer
 */
public class GenericStageDevicePanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    StageDeviceSimulator lStageDeviceSimulator =
                                               new StageDeviceSimulator("demostage",
                                                                        StageType.Multi);

    lStageDeviceSimulator.setSimLogging(true);

    for (int i = 0; i < 6; i++)
      lStageDeviceSimulator.addDOF("Stage" + i,
                                   -100 + 10 * i,
                                   100 - 10 * i);

    StageDevicePanel lGenericStageDevicePanel =
                                              new StageDevicePanel(lStageDeviceSimulator);

    Scene scene = new Scene(lGenericStageDevicePanel,
                            javafx.scene.paint.Color.WHITE);

    pPrimaryStage.setTitle(this.getClass().getSimpleName());
    pPrimaryStage.setScene(scene);
    pPrimaryStage.show();

  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    Application.launch(GenericStageDevicePanelDemo.class);
  }

}
