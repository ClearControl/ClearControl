package clearcontrol.devices.stages.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.devices.stages.StageType;
import clearcontrol.devices.stages.devices.sim.StageDeviceSimulator;
import clearcontrol.devices.stages.gui.StageDevicePanel;

/**
 * XYZR stage device panel demo
 *
 * @author royer
 */
public class XYZRStageDevicePanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    StageDeviceSimulator lStageDeviceSimulator =
                                               new StageDeviceSimulator("demostage",
                                                                        StageType.XYZR);

    lStageDeviceSimulator.setSimLogging(true);

    lStageDeviceSimulator.addDOF("X", -100, 100);
    lStageDeviceSimulator.addDOF("Y", -100, 100);
    lStageDeviceSimulator.addDOF("Z", -100, 100);
    lStageDeviceSimulator.addDOF("R", 0, 360);

    StageDevicePanel lStageDevicePanel =
                                       new StageDevicePanel(lStageDeviceSimulator);

    Scene scene = new Scene(lStageDevicePanel,
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
    Application.launch(XYZRStageDevicePanelDemo.class);
  }

}
