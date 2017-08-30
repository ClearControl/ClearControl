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

  TSTStageDevice mTSTStageDevice;

  /**
   * Instantiates this demo app
   */
  public TSTStageDeviceInteractiveDemo()
  {
    super();
    mTSTStageDevice = new TSTStageDevice();

  }

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {

    if (mTSTStageDevice.open())
    {

      StageDevicePanel lStageDevicePanel =
                                         new StageDevicePanel(mTSTStageDevice);

      Scene scene = new Scene(lStageDevicePanel,
                              javafx.scene.paint.Color.WHITE);

      pPrimaryStage.setTitle(this.getClass().getSimpleName());
      pPrimaryStage.setScene(scene);
      pPrimaryStage.show();
    }
    else
      System.err.println("Could not open stage device");

  }

  @Override
  public void stop()
  {
    mTSTStageDevice.close();
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
