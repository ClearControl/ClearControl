package clearcontrol.microscope.stacks.gui;

import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import clearcontrol.gui.jfx.other.recycler.RecyclerPanel;
import clearcontrol.microscope.stacks.StackRecyclerManager;
import clearcontrol.stack.StackInterface;
import clearcontrol.stack.StackRequest;
import coremem.recycling.RecyclerInterface;

/**
 * StackRecyclerManagerPanel is a GUI element that displays information about
 * all recyclers managed by a StackRecyclerManager.
 * 
 * @author royer
 */
public class StackRecyclerManagerPanel extends VBox
{

  /**
   * Constructs a {@link StackRecyclerManagerPanel} given a
   * {@link StackRecyclerManager}.
   * 
   * @param pStackRecyclerManager
   *          {@link StackRecyclerManager} to use.
   */
  public StackRecyclerManagerPanel(StackRecyclerManager pStackRecyclerManager)
  {
    super();

    pStackRecyclerManager.addChangeListener((m) -> {
      updateRecyclerPanels(((StackRecyclerManager) m).getRecyclerMap());
    });

  }

  /**
   * This private method is responsible to upate the Recyclers display. It
   * should be called whenever the list of recyclers in the manager is changed.
   * 
   * @param pMap
   */
  private void updateRecyclerPanels(ConcurrentHashMap<String, RecyclerInterface<StackInterface, StackRequest>> pMap)
  {
    StackRecyclerManagerPanel lMainVBox = this;

    Platform.runLater(() -> {

      lMainVBox.getChildren().clear();

      ScrollPane lScrollPane = new ScrollPane();
      lScrollPane.setPrefSize(RecyclerPanel.cPrefWidth,
                              RecyclerPanel.cPrefHeight * 1.5);
      lScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
      lScrollPane.setVmax(RecyclerPanel.cPrefHeight * 1.5);
      lMainVBox.getChildren().add(lScrollPane);
      VBox.setVgrow(lScrollPane, Priority.ALWAYS);

      VBox lScrollingBox = new VBox();
      lScrollPane.setContent(lScrollingBox);

      Set<Entry<String, RecyclerInterface<StackInterface, StackRequest>>> lEntrySet =
                                                                                    pMap.entrySet();
      for (Entry<String, RecyclerInterface<StackInterface, StackRequest>> lEntry : lEntrySet)
      {
        String lRecyclerName = lEntry.getKey();
        RecyclerInterface<StackInterface, StackRequest> lRecycler =
                                                                  lEntry.getValue();

        Label lLabel = new Label(lRecyclerName + ":");
        VBox lLabelVBox = new VBox(lLabel);
        lLabelVBox.setPadding(new Insets(10, 10, 0, 10));

        RecyclerPanel lRecyclerPane = new RecyclerPanel(lRecycler);
        lRecyclerPane.setPadding(10);

        Separator lSeparator = new Separator();
        lScrollingBox.getChildren().addAll(lLabelVBox,
                                           lRecyclerPane,
                                           lSeparator);

      }

    });

  }

}
