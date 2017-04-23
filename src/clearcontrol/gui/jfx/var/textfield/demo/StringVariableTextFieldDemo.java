package clearcontrol.gui.jfx.var.textfield.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.textfield.StringVariableTextField;

/**
 * Number text field demo
 *
 * @author royer
 */
public class StringVariableTextFieldDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    Variable<String> lStringVariable = new Variable<>("DemoStringVar",
                                                      "abcd");
    lStringVariable.addSetListener((o, n) -> {
      System.out.println("string: " + n);
    });

    StringVariableTextField lStringVariableTextField =
                                                     new StringVariableTextField("a string value: ",
                                                                                 lStringVariable);

    root.add(lStringVariableTextField, 0, 1);

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
