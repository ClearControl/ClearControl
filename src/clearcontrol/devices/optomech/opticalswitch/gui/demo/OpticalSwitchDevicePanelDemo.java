package clearcontrol.devices.optomech.opticalswitch.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.devices.optomech.opticalswitch.devices.sim.OpticalSwitchDeviceSimulator;
import clearcontrol.devices.optomech.opticalswitch.gui.OpticalSwitchDevicePanel;

/**
 * Optical Switch device panel demo
 *
 * @author royer
 */
public class OpticalSwitchDevicePanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    OpticalSwitchDeviceSimulator lOpticalSwitchDeviceSimulator =
                                                               new OpticalSwitchDeviceSimulator("demoswitch",
                                                                                                4);

    lOpticalSwitchDeviceSimulator.setSimLogging(true);
    lOpticalSwitchDeviceSimulator.setSwitch(2, true);

    OpticalSwitchDevicePanel lOpticalSwitchDevicePanel =
                                                       new OpticalSwitchDevicePanel(lOpticalSwitchDeviceSimulator);

    Scene scene = new Scene(lOpticalSwitchDevicePanel,
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
    Application.launch(OpticalSwitchDevicePanelDemo.class);
  }

}
