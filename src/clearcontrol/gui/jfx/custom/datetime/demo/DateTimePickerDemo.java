package clearcontrol.gui.jfx.custom.datetime.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.gui.jfx.custom.datetime.DateTimePicker;

/**
 * Enum combo box demo
 *
 * @author royer
 */
public class DateTimePickerDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    DateTimePicker lDateTimePicker = new DateTimePicker();

    root.add(lDateTimePicker, 0, 2);

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
