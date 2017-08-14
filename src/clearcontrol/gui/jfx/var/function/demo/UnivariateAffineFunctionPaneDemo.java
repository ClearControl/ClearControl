package clearcontrol.gui.jfx.var.function.demo;

import static java.lang.Math.random;

import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.function.UnivariateAffineFunctionPane;

/**
 * Univariate affine function pane demo
 *
 * @author royer
 */
public class UnivariateAffineFunctionPaneDemo extends Application
                                              implements
                                              AsynchronousSchedulerFeature
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getName());

    Variable<UnivariateAffineFunction> lFunctionVariable =
                                                         new Variable<>("Fun",
                                                                        UnivariateAffineFunction.identity());

    lFunctionVariable.addSetListener((o, n) -> {
      System.out.println("new function: " + lFunctionVariable);
    });

    UnivariateAffineFunctionPane lUnivariateAffineFunctionPane =
                                                               new UnivariateAffineFunctionPane("MyFunction",
                                                                                                lFunctionVariable);

    root.getChildren().add(lUnivariateAffineFunctionPane);

    scheduleAtFixedRate(() -> {
      lFunctionVariable.set(UnivariateAffineFunction.axplusb(random(),
                                                             random()));
    }, 1, TimeUnit.SECONDS);

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
