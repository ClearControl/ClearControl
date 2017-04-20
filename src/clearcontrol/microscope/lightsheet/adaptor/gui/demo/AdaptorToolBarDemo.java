package clearcontrol.microscope.lightsheet.adaptor.gui.demo;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.microscope.lightsheet.adaptor.Adaptator;
import clearcontrol.microscope.lightsheet.adaptor.gui.AdaptorToolBar;
import clearcontrol.microscope.lightsheet.adaptor.test.AdaptationTestModule;
import clearcontrol.microscope.lightsheet.adaptor.test.TestState;

/**
 * Simulation manager demo
 *
 * @author royer
 */
public class AdaptorToolBarDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    Group root = new Group();
    Scene scene = new Scene(root, 800, 600);
    stage.setScene(scene);
    stage.setTitle(AdaptorToolBarDemo.class.getSimpleName());
    // scene.setFill(Color.BLACK);

    Adaptator<TestState> lAdaptator = new Adaptator<TestState>(null);
    lAdaptator.getNewAcquisitionStateVariable()
              .set(new TestState("initial state"));

    lAdaptator.add(new AdaptationTestModule("A", 2));
    lAdaptator.add(new AdaptationTestModule("B", 3));
    // lAdaptator.add(new AdaptationTestModule("C", 2, 3));

    AdaptorToolBar<TestState> lAdaptorToolBar =
                                              new AdaptorToolBar<>(lAdaptator);

    root.getChildren().add(lAdaptorToolBar);

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
