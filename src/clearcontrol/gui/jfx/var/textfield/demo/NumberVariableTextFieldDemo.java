package clearcontrol.gui.jfx.var.textfield.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;

/**
 * Number text field demo
 *
 * @author royer
 */
public class NumberVariableTextFieldDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    Variable<Number> lDoubleVariable =
                                     new Variable<Number>("DemoDoubleVar",
                                                          0.0);
    lDoubleVariable.addSetListener((o, n) -> {
      System.out.println("double: " + n);
    });

    NumberVariableTextField<Number> lVariableDoubleTextField =
                                                             new NumberVariableTextField<Number>("a double value: ",
                                                                                                 lDoubleVariable,
                                                                                                 -1.0,
                                                                                                 2.0,
                                                                                                 0.1);

    root.add(lVariableDoubleTextField, 0, 1);

    Variable<Number> lIntVariable = new Variable<Number>("DemoIntVar",
                                                         0.0);
    lIntVariable.addSetListener((o, n) -> {
      System.out.println("int: " + n);
    });

    NumberVariableTextField<Number> lVariableIntTextField =
                                                          new NumberVariableTextField<Number>("an int value: ",
                                                                                              lIntVariable,
                                                                                              -10,
                                                                                              20,
                                                                                              1);

    root.add(lVariableIntTextField, 0, 2);

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
