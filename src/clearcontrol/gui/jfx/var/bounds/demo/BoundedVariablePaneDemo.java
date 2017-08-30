package clearcontrol.gui.jfx.var.bounds.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousSchedulerFeature;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.var.bounds.BoundedVariablePane;

/**
 * Univariate affine function pane demo
 *
 * @author royer
 */
public class BoundedVariablePaneDemo extends Application implements
                                     AsynchronousSchedulerFeature
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getName());

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

    BoundedVariablePane lBoundedVariablePane =
                                             new BoundedVariablePane("MyFunction",
                                                                     lVariable);

    root.getChildren().add(lBoundedVariablePane);

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
