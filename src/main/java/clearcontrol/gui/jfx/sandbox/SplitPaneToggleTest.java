package clearcontrol.gui.jfx.sandbox;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SplitPaneToggleTest extends Application
{

  @Override
  public void start(Stage primaryStage)
  {
    ToggleButton settings = new ToggleButton("Settings");

    SplitPane splitPane = new SplitPane();

    TitledPane titledPane1 = new TitledPane("Options",
                                            new Label("An option"));

    TitledPane titledPane2 =
                           new TitledPane("Options",
                                          new Label("Another option "));

    titledPane1.setAnimated(false);
    titledPane2.setAnimated(false);

    VBox settingsPane = new VBox(titledPane1, titledPane2);

    settingsPane.setMinWidth(0);
    splitPane.getItems().addAll(
                                new BorderPane(new Label("Main content")),
                                settingsPane);

    DoubleProperty splitPaneDividerPosition =
                                            splitPane.getDividers()
                                                     .get(0)
                                                     .positionProperty();

    // update toggle button status if user moves divider:
    splitPaneDividerPosition.addListener((obs,
                                          oldPos,
                                          newPos) -> settings.setSelected(newPos.doubleValue() < 0.95));

    splitPaneDividerPosition.set(0.8);

    settings.setOnAction(event -> {
      if (settings.isSelected())
      {
        splitPane.setDividerPositions(0.8);
      }
      else
      {
        splitPane.setDividerPositions(1.0);
      }
    });

    BorderPane root = new BorderPane(splitPane,
                                     new HBox(settings),
                                     null,
                                     null,
                                     null);
    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
