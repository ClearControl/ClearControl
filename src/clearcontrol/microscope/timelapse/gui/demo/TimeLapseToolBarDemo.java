package clearcontrol.microscope.timelapse.gui.demo;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.thread.ThreadSleep;
import clearcontrol.microscope.timelapse.TimelapseBase;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import clearcontrol.microscope.timelapse.gui.TimelapseToolbar;
import clearcontrol.stack.sourcesink.sink.RawFileStackSink;
import clearcontrol.stack.sourcesink.sink.SqeazyFileStackSink;

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
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    TimelapseInterface lTimelapse = new TimelapseBase(null)
    {

      @Override
      public void acquire()
      {
        System.out.println("acquire time point: "
                           + getTimePointCounterVariable().get());
        ThreadSleep.sleep(10, TimeUnit.MILLISECONDS);
      }

    };

    lTimelapse.addFileStackSinkType(RawFileStackSink.class);
    lTimelapse.addFileStackSinkType(SqeazyFileStackSink.class);

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
