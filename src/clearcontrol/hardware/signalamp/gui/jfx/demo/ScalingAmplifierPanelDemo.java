package clearcontrol.hardware.signalamp.gui.jfx.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.hardware.signalamp.devices.sim.ScalingAmplifierSimulator;
import clearcontrol.hardware.signalamp.gui.jfx.ScalingAmplifierPanel;

public class ScalingAmplifierPanelDemo extends Application
{

  @Override
  public void start(Stage pPrimaryStage) throws Exception
  {
    ScalingAmplifierSimulator lScalingAmplifierSimulator =
                                                         new ScalingAmplifierSimulator("demofilterwheel");

    lScalingAmplifierSimulator.setSimLogging(true);

    ScalingAmplifierPanel lScalingAmplifierPanel =
                                                 new ScalingAmplifierPanel(lScalingAmplifierSimulator);

    Scene scene = new Scene(lScalingAmplifierPanel,
                            javafx.scene.paint.Color.WHITE);

    pPrimaryStage.setTitle(this.getClass().getSimpleName());
    pPrimaryStage.setScene(scene);
    pPrimaryStage.show();

  }

  public static void main(String[] args)
  {
    Application.launch(ScalingAmplifierPanelDemo.class);
  }

}
