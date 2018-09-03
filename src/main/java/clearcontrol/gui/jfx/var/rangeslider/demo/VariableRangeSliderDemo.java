package clearcontrol.gui.jfx.var.rangeslider.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.rangeslider.VariableRangeSlider;

/**
 * Variable range slider demo
 *
 * @author royer
 */
public class VariableRangeSliderDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle("Slider Sample");
    // scene.setFill(Color.BLACK);

    Variable<Number> lLowDoubleVariable =
                                        new Variable<Number>("DemoLowDoubleVar",
                                                             0.0);
    lLowDoubleVariable.addSetListener((o, n) -> {
      System.out.println("low double: " + n);
    });

    Variable<Number> lHighDoubleVariable =
                                         new Variable<Number>("DemoHighDoubleVar",
                                                              0.0);
    lHighDoubleVariable.addSetListener((o, n) -> {
      System.out.println("high double: " + n);
    });

    VariableRangeSlider<Number> lVariableRangeDoubleSlider =
                                                           new VariableRangeSlider<Number>("a double value: ",
                                                                                           lLowDoubleVariable,
                                                                                           lHighDoubleVariable,
                                                                                           -1.0,
                                                                                           2.0,
                                                                                           0.1,
                                                                                           0.1);

    root.add(lVariableRangeDoubleSlider, 0, 1);

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
