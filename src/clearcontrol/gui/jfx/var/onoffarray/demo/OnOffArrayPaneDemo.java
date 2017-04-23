package clearcontrol.gui.jfx.var.onoffarray.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;

/**
 * On/Off array demo
 *
 * @author royer
 */
public class OnOffArrayPaneDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 600, 400);
    stage.setScene(scene);
    stage.setTitle("Slider Sample");
    // scene.setFill(Color.BLACK);

    OnOffArrayPane lOnOffArrayPanel = new OnOffArrayPane();

    for (int i = 0; i < 5; i++)
    {
      final int fi = i;

      Variable<Boolean> lBoolVariable =
                                      new Variable<>("DemoBoolVar"
                                                     + i, i % 2 == 0);
      lBoolVariable.addSetListener((o, n) -> {
        System.out.println("bool " + fi + ": " + n);
      });

      lOnOffArrayPanel.addSwitch("switch" + i, lBoolVariable);
    }

    root.getChildren().add(lOnOffArrayPanel);

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
