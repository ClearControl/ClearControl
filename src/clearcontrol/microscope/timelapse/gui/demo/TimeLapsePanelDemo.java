package clearcontrol.microscope.timelapse.gui.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.togglebutton.CustomToggleButton;

public class TimeLapsePanelDemo extends Application
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

    CustomToggleButton lCustomToggleButton =
                                           new CustomToggleButton("ON",
                                                                  "OFF",
                                                                  lVariable);

    root.getChildren().add(lCustomToggleButton);

    stage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
