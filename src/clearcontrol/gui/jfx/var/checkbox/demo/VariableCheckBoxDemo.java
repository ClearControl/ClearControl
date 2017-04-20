package clearcontrol.gui.jfx.var.checkbox.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;

/**
 * VariableCheckBox Demo
 *
 * @author royer
 */
public class VariableCheckBoxDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    GridPane root = new GridPane();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle(this.getClass().getSimpleName());
    // scene.setFill(Color.BLACK);

    Variable<Boolean> lVariable = new Variable<>("var", true);
    lVariable.addSetListener((o, n) -> {
      System.out.println(n);
    });

    VariableCheckBox lVariableCheckBox =
                                       new VariableCheckBox("democheckbox",
                                                            lVariable);

    root.add(lVariableCheckBox, 0, 2);

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
