package clearcontrol.microscope.timelapse.gui.demo;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.thread.ThreadUtils;
import clearcontrol.microscope.timelapse.TimelapseBase;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import clearcontrol.microscope.timelapse.gui.TimelapseToolbar;

/**
 * Timelapse toolbar demo
 *
 * @author royer
 */
public class TimeLapseToolBarDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    HBox root = new HBox();
    root.setAlignment(Pos.CENTER);
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    TimelapseInterface lTimelapse = new TimelapseBase()
    {

      @Override
      public void acquire()
      {
        System.out.println("acquire time point: "
                           + getTimePointCounterVariable().get());
        ThreadUtils.sleep(300, TimeUnit.MILLISECONDS);
      }

    };

    TimelapseToolbar lTimelapseToolbar =
                                       new TimelapseToolbar(lTimelapse);

    root.getChildren().add(lTimelapseToolbar);

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
