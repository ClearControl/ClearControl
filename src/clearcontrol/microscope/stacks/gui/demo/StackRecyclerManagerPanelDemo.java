package clearcontrol.microscope.stacks.gui.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import clearcontrol.core.concurrent.executors.AsynchronousExecutorFeature;
import clearcontrol.gui.jfx.other.recycler.RecyclerPanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.microscope.stacks.gui.StackRecyclerManagerPanel;

/**
 * 
 *
 * @author royer
 */
public class StackRecyclerManagerPanelDemo extends Application
                                           implements
                                           AsynchronousExecutorFeature
{

  @Override
  public void start(Stage stage)
  {

    StackRecyclerManager lStackRecyclerManager =
                                               new StackRecyclerManager();
    StackRecyclerManagerPanel lStackRecyclerManagerPanel =
                                                         new StackRecyclerManagerPanel(lStackRecyclerManager);

    lStackRecyclerManager.getRecycler("recycler1", 10, 11);

    lStackRecyclerManager.getRecycler("recycler2", 20, 12);

    lStackRecyclerManager.getRecycler("recycler3", 30, 13);

    lStackRecyclerManager.getRecycler("recycler4", 40, 14);

    Scene scene = new Scene(lStackRecyclerManagerPanel,
                            RecyclerPanel.cPrefWidth,
                            RecyclerPanel.cPrefHeight);
    stage.setScene(scene);
    stage.setTitle("RecyclerPane Demo");

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
