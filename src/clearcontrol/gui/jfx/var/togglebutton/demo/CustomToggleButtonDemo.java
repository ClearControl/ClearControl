package clearcontrol.gui.jfx.var.togglebutton.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.togglebutton.VariableToggleButton;

/**
 * Custom toggle button demo
 *
 * @author royer
 */
public class CustomToggleButtonDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    HBox root = new HBox();
    root.setAlignment(Pos.CENTER);
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle("Slider Sample");
    // scene.setFill(Color.BLACK);

    Variable<Boolean> lVariable =
                                new Variable<Boolean>("bool", false);
    lVariable.addSetListener((o, n) -> {
      System.out.println("bool: " + n);
    });

    VariableToggleButton lCustomToggleButton =
                                             new VariableToggleButton("ON",
                                                                      "OFF",
                                                                      lVariable);

    root.getChildren().add(lCustomToggleButton);

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
