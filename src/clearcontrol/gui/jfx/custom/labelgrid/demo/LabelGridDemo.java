package clearcontrol.gui.jfx.custom.labelgrid.demo;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.gui.jfx.custom.labelgrid.LabelGrid;

/**
 * Mulichart demo
 *
 * @author royer
 */
public class LabelGridDemo extends Application
                           implements AsynchronousExecutorFeature
{

  @Override
  public void start(Stage stage)
  {
    HBox root = new HBox();
    Scene scene = new Scene(root, 1400, 400);
    stage.setScene(scene);
    stage.setTitle("MultiChartDemo");

    final LabelGrid lLabelGrid = new LabelGrid();

    root.getChildren().add(lLabelGrid);

    for (int i = 0; i < 10; i++)
      lLabelGrid.setColumnName(i, "c" + i);

    for (int j = 0; j < 10; j++)
      lLabelGrid.setRowName(j, "r" + j);

    executeAsynchronously(() -> {

      for (int i = 0; i < 10; i++)
        for (int j = 0; j < 10; j++)
        {
          String lString = String.format("Cell %dx%d", i, j);
          System.out.println(lString);
          ThreadSleep.sleep(1, TimeUnit.SECONDS);
          lLabelGrid.getLabel(i, j).setText(lString);
        }

    });

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
