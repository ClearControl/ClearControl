package clearcontrol.gui.jfx.var.datetime.demo;

import java.time.LocalDateTime;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.datetime.DateTimePickerVariable;

/**
 * Enum combo box demo
 *
 * @author royer
 */
public class DateTimePickerVariableDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    Variable<LocalDateTime> lVariable =
                                      new Variable<>("time",
                                                     LocalDateTime.now());
    lVariable.addSetListener((o, n) -> {
      System.out.println(n);
    });

    DateTimePickerVariable lDateTimePicker =
                                           new DateTimePickerVariable(lVariable);

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
