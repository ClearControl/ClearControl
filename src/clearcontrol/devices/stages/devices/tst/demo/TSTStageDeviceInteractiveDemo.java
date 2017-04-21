package clearcontrol.devices.stages.devices.tst.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.devices.stages.devices.tst.TSTStageDevice;
import clearcontrol.devices.stages.gui.StageDevicePanel;

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

    if (lTSTStageDevice.open())
    {

      StageDevicePanel lGenericStageDevicePanel =
                                                new StageDevicePanel(lTSTStageDevice);

      Scene scene = new Scene(lGenericStageDevicePanel,
                              javafx.scene.paint.Color.WHITE);

      pPrimaryStage.setTitle(this.getClass().getSimpleName());
      pPrimaryStage.setScene(scene);
      pPrimaryStage.show();
    }
    else
      System.err.println("Could not open stage device");

  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    Application.launch(TSTStageDeviceInteractiveDemo.class);
  }

}
