package clearcontrol.gui.jfx.custom.visualconsole.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsolePanel;

/**
 * Simulation manager demo
 *
 * @author royer
 */
public class VisualConsoleDemo extends Application
{

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

    lVisualConsoleClient.configureChart("A",
                                        "test",
                                        "x",
                                        "y",
                                        ChartType.Line);

    lVisualConsoleClient.configureChart("B",
                                        "test",
                                        "x",
                                        "y",
                                        ChartType.Line);

    lVisualConsoleClient.configureChart("C",
                                        "test1",
                                        "x",
                                        "y",
                                        ChartType.Scatter);

    lVisualConsoleClient.configureChart("C",
                                        "test2",
                                        "x",
                                        "y",
                                        ChartType.Scatter);

    for (int i = 0; i < 100; i++)
    {
      double x = i;
      double y = Math.cos(0.1 * x);

      lVisualConsoleClient.addPoint("A", "test", i == 0, x, y);

      lVisualConsoleClient.addPoint("B", "test", i == 0, x, y);

      lVisualConsoleClient.addPoint("C", "test1", i == 0, x, y * y);

      lVisualConsoleClient.addPoint("C",
                                    "test2",
                                    i == 0,
                                    x,
                                    y * (1 - y) * y);

    }

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
