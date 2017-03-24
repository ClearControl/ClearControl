package clearcontrol.gui.jfx.var.textfield.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.textfield.VariableNumberTextField;

public class VariableNumberTextFieldDemo extends Application
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

    VariableNumberTextField<Number> lVariableDoubleSlider =
                                                          new VariableNumberTextField<Number>("a double value: ",
                                                                                              lDoubleVariable,
                                                                                              -1.0,
                                                                                              2.0,
                                                                                              0.1);

    root.add(lVariableDoubleSlider, 0, 1);

    Variable<Number> lIntVariable = new Variable<Number>("DemoIntVar",
                                                         0.0);
    lIntVariable.addSetListener((o, n) -> {
      System.out.println("int: " + n);
    });

    VariableNumberTextField<Number> lVariableIntSlider =
                                                       new VariableNumberTextField<Number>("an int value: ",
                                                                                           lIntVariable,
                                                                                           -10,
                                                                                           20,
                                                                                           1);

    root.add(lVariableIntSlider, 0, 2);

    stage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
