package clearcontrol.gui.jfx.var.customvarpanel.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.var.customvarpanel.CustomVariablePane;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.gui.jfx.var.slider.VariableSlider;

/**
 * Custom var panel
 *
 * @author royer
 */
public class CustomVariablePaneDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle("Slider Sample");
    // scene.setFill(Color.BLACK);

    CustomVariablePane lCustomVariablePane = new CustomVariablePane();

    lCustomVariablePane.addTab("First");

    Variable<Number> lDoubleVariable =
                                     new Variable<Number>("DoubleVar",
                                                          0.0);
    lDoubleVariable.addSetListener((o, n) -> {
      System.out.println("double: " + n);
    });
    lCustomVariablePane.addSliderForVariable(lDoubleVariable,
                                             -1.0,
                                             1.0,
                                             0.1,
                                             0.1);

    Variable<Number> lIntegerVariable1 =
                                       new Variable<Number>("IntegerVar",
                                                            2);
    lIntegerVariable1.addSetListener((o, n) -> {
      System.out.println("int: " + n);
    });
    lCustomVariablePane.addSliderForVariable(lIntegerVariable1,
                                             -10,
                                             30,
                                             1,
                                             5);

    Variable<Number> lIntegerVariable2 =
                                       new Variable<Number>("IntegerChng",
                                                            2);
    lIntegerVariable2.addSetListener((o, n) -> {
      System.out.println("int2: " + n);
    });
    VariableSlider<Number> lAddSliderForVariable =
                                                 lCustomVariablePane.addSliderForVariable(lIntegerVariable2,
                                                                                          -10,
                                                                                          30,
                                                                                          1,
                                                                                          5);
    lAddSliderForVariable.setUpdateIfChanging(true);

    BoundedVariable<Number> lBoundedVariable =
                                             new BoundedVariable<Number>("Bounded",
                                                                         2.0,
                                                                         -10.0,
                                                                         10.0,
                                                                         0.1);
    lBoundedVariable.addSetListener((o, n) -> {
      System.out.println("boundeddouble: " + n);
    });

    lCustomVariablePane.addSliderForVariable(lBoundedVariable, 5.0);

    OnOffArrayPane lAddOnOffArray =
                                  lCustomVariablePane.addOnOffArray("onoff");

    for (int i = 0; i < 5; i++)
    {
      final int fi = i;

      Variable<Boolean> lBoolVariable = new Variable<>("b" + i,
                                                       i % 2 == 0);
      lBoolVariable.addSetListener((o, n) -> {
        System.out.println("bool " + fi + ": " + n);
      });

      lAddOnOffArray.addSwitch("S" + i, lBoolVariable);
    }

    Variable<UnivariateAffineFunction> lFunctionVariable =
                                                         new Variable<>("Fun",
                                                                        UnivariateAffineFunction.identity());

    lFunctionVariable.addSetListener((o, n) -> {
      System.out.println("new function: " + lFunctionVariable);
    });

    lCustomVariablePane.addFunctionPane("MyFunction",
                                        lFunctionVariable);

    lCustomVariablePane.addTab("Second");

    Variable<Number> lAnotherDoubleVariable =
                                            new Variable<Number>("AnotherDoubleVar",
                                                                 0.0);
    lAnotherDoubleVariable.addSetListener((o, n) -> {
      System.out.println("anotherdouble: " + n);
    });
    lCustomVariablePane.addSliderForVariable(lAnotherDoubleVariable,
                                             -1.0,
                                             1.0,
                                             0.1,
                                             0.1);/**/

    BoundedVariable<Number> lVariable =
                                      new BoundedVariable<Number>("var",
                                                                  0.0,
                                                                  -1.0,
                                                                  1.0);
    lVariable.addSetListener((o, n) -> {
      System.out.println("change to value:" + n);
    });
    lVariable.getMinVariable().addSetListener((o, n) -> {
      System.out.println("change to min:" + n);
    });
    lVariable.getMaxVariable().addSetListener((o, n) -> {
      System.out.println("change to max:" + n);
    });
    lVariable.getGranularityVariable().addSetListener((o, n) -> {
      System.out.println("change to granularity:" + n);
    });

    lCustomVariablePane.addBoundedVariable("var", lVariable);

    root.getChildren().add(lCustomVariablePane);

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
