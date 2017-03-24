package clearcontrol.devices.sensors.gui.jfx.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.devices.sensors.devices.sim.TemperatureSensorDeviceSimulator;
import clearcontrol.devices.sensors.gui.jfx.TemperatureSensorDevicePanel;

/**
 * Temperature Sensor Device Panel Demo
 *
 * @author royer
 */
public class TemperatureSensorDevicePanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {

    TemperatureSensorDeviceSimulator lTemperatureSensorDeviceSimulator =
                                                                       new TemperatureSensorDeviceSimulator("demotempsesor");
    lTemperatureSensorDeviceSimulator.setSimLogging(true);

    TemperatureSensorDevicePanel lTemperatureSensorDevicePanel =
                                                               new TemperatureSensorDevicePanel(lTemperatureSensorDeviceSimulator);

    Scene scene = new Scene(lTemperatureSensorDevicePanel,
                            javafx.scene.paint.Color.WHITE);

    pPrimaryStage.setTitle(this.getClass().getSimpleName());
    pPrimaryStage.setScene(scene);
    pPrimaryStage.setWidth(100);
    pPrimaryStage.setHeight(100);/**/

    lTemperatureSensorDeviceSimulator.open();
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
    Application.launch(TemperatureSensorDevicePanelDemo.class);
  }

}
