package clearcontrol.devices.cameras.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.devices.sim.StackCameraDeviceSimulator;
import clearcontrol.devices.cameras.devices.sim.StackCameraSimulationProvider;
import clearcontrol.devices.cameras.devices.sim.providers.FractalStackProvider;
import clearcontrol.devices.cameras.gui.CameraDevicePanel;

/**
 * Camera device demo
 *
 * @author royer
 */
public class CameraDevicePanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {

    StackCameraSimulationProvider lStackCameraSimulationProvider =
                                                                 new FractalStackProvider();

    Variable<Boolean> lTrigger =
                               new Variable<Boolean>("CameraTrigger",
                                                     false);

    StackCameraDeviceSimulator lStackCameraDeviceSimulator =
                                                           new StackCameraDeviceSimulator("StackCamera",
                                                                                          lStackCameraSimulationProvider,
                                                                                          lTrigger);
    lStackCameraDeviceSimulator.setSimLogging(true);

    lStackCameraDeviceSimulator.getStackWidthVariable().set(316L);
    lStackCameraDeviceSimulator.getStackHeightVariable().set(632L);

    CameraDevicePanel lCameraDevicePanel =
                                         new CameraDevicePanel(lStackCameraDeviceSimulator);

    VBox pane = new VBox();

    pane.getChildren().add(lCameraDevicePanel);

    Scene scene = new Scene(pane, javafx.scene.paint.Color.WHITE);

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
    Application.launch(CameraDevicePanelDemo.class);
  }

}
