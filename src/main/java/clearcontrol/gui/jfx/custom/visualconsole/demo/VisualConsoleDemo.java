package clearcontrol.gui.jfx.custom.visualconsole.demo;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsolePanel;

/**
 * Simulation manager demo
 *
 * @author royer
 */
public class VisualConsoleDemo extends Application
                               implements AsynchronousSchedulerFeature
{

  private int mCounter;

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle(VisualConsoleDemo.class.getSimpleName());
    // scene.setFill(Color.BLACK);

    VisualConsoleInterface lVisualConsoleClient =
                                                new VisualConsoleInterface()
                                                {
                                                };

    VisualConsolePanel lVisualConsolePanel =
                                           new VisualConsolePanel(lVisualConsoleClient);

    root.getChildren().add(lVisualConsolePanel);

    Runnable lRunnable = () -> {
      lVisualConsoleClient.configureChart("A" + mCounter,
                                          "test",
                                          "x",
                                          "y",
                                          ChartType.Line);

      lVisualConsoleClient.configureChart("B" + mCounter,
                                          "test",
                                          "x",
                                          "y",
                                          ChartType.Line);

      lVisualConsoleClient.configureChart("C" + mCounter,
                                          "test1",
                                          "x",
                                          "y",
                                          ChartType.Scatter);

      lVisualConsoleClient.configureChart("C",
                                          "testu" + mCounter,
                                          "x",
                                          "y",
                                          ChartType.Scatter);

      for (int i = 0; i < 100; i++)
      {
        double x = i;
        double y = Math.cos(0.1 * x + mCounter);

        lVisualConsoleClient.addPoint("A" + mCounter,
                                      "test",
                                      i == 0,
                                      x,
                                      y);

        lVisualConsoleClient.addPoint("B" + mCounter,
                                      "test",
                                      i == 0,
                                      x,
                                      y);

        lVisualConsoleClient.addPoint("C" + mCounter,
                                      "test1",
                                      i == 0,
                                      x,
                                      y * y);

        lVisualConsoleClient.addPoint("C",
                                      "testu" + mCounter,
                                      i == 0,
                                      x,
                                      y * (1 - y) * y);

      }

      mCounter++;
    };

    scheduleAtFixedRate(lRunnable, 10, TimeUnit.SECONDS);

    stage.show();
  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
