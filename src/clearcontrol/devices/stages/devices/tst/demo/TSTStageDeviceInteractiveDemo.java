package clearcontrol.devices.stages.devices.tst.demo;

import clearcontrol.devices.stages.devices.tst.TSTStageDevice;
import clearcontrol.devices.stages.gui.jfx.GenericStageDevicePanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * TST001 stage device interactive demo
 *
 * @author royer
 */
public class TSTStageDeviceInteractiveDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    TSTStageDevice lTSTStageDevice = new TSTStageDevice();

    GenericStageDevicePanel lGenericStageDevicePanel =
                                                     new GenericStageDevicePanel(lTSTStageDevice);

    Scene scene = new Scene(lGenericStageDevicePanel,
                            javafx.scene.paint.Color.WHITE);

    pPrimaryStage.setTitle(this.getClass().getSimpleName());
    pPrimaryStage.setScene(scene);
    pPrimaryStage.show();

  }

  /**
   * Main
   * @param args NA
   */
  public static void main(String[] args)
  {
    Application.launch(TSTStageDeviceInteractiveDemo.class);
  }

}
