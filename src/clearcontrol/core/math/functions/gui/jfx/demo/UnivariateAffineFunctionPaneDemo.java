package clearcontrol.core.math.functions.gui.jfx.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.math.functions.UnivariateAffineFunction;
import clearcontrol.core.math.functions.gui.jfx.UnivariateAffineFunctionPane;
import clearcontrol.core.variable.Variable;

public class UnivariateAffineFunctionPaneDemo extends Application
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

    stage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
