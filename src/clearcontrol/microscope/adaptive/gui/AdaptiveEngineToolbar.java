package clearcontrol.microscope.adaptive.gui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import clearcontrol.core.log.LoggingFeature;
import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.VariableSetListener;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.microscope.adaptive.AdaptiveEngine;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;

/**
 * Adaptor Panel
 *
 * @author royer
 */
public class AdaptiveEngineToolbar extends CustomGridPane
                                   implements LoggingFeature
{

  /**
   * Instantiates a panel given an adaptive engine
   * 
   * @param pAdaptiveEngine
   *          adaptor
   */
  public AdaptiveEngineToolbar(AdaptiveEngine<?> pAdaptiveEngine)
  {
    super();

    int lRow = 0;
    // this.setStyle("-fx-background-color: yellow;");
    // mGridPane.setStyle("-fx-border-color: blue;");

    for (int i = 0; i < 3; i++)
    {
      ColumnConstraints lColumnConstraints = new ColumnConstraints();
      lColumnConstraints.setPercentWidth(33);
      getColumnConstraints().add(lColumnConstraints);
    }

    {
      Button lStart = new Button("Start");
      lStart.setAlignment(Pos.CENTER);
      lStart.setMaxWidth(Double.MAX_VALUE);
      lStart.setOnAction((e) -> {
        pAdaptiveEngine.startTask();
      });
      GridPane.setColumnSpan(lStart, 2);
      GridPane.setHgrow(lStart, Priority.ALWAYS);
      add(lStart, 0, lRow);

      lRow++;
    }

    {
      Button lStop = new Button("Stop");
      lStop.setAlignment(Pos.CENTER);
      lStop.setMaxWidth(Double.MAX_VALUE);
      lStop.setOnAction((e) -> {
        pAdaptiveEngine.stopTask();
      });
      GridPane.setColumnSpan(lStop, 2);
      GridPane.setHgrow(lStop, Priority.ALWAYS);
      add(lStop, 0, lRow);

      lRow++;
    }

    {
      Button lReset = new Button("Reset");
      lReset.setAlignment(Pos.CENTER);
      lReset.setMaxWidth(Double.MAX_VALUE);
      lReset.setOnAction((e) -> {
        pAdaptiveEngine.reset();
      });
      GridPane.setColumnSpan(lReset, 2);
      GridPane.setHgrow(lReset, Priority.ALWAYS);
      add(lReset, 0, lRow);

      lRow++;
    }

    {
      ProgressIndicator lCalibrationProgressIndicator =
                                                      new ProgressIndicator(0.0);
      lCalibrationProgressIndicator.setMaxWidth(Double.MAX_VALUE);
      lCalibrationProgressIndicator.setStyle(".percentage { visibility: hidden; }");
      GridPane.setRowSpan(lCalibrationProgressIndicator, 3);
      GridPane.setColumnSpan(lCalibrationProgressIndicator, 2);
      add(lCalibrationProgressIndicator, 2, 0);

      pAdaptiveEngine.getProgressVariable().addEdgeListener((n) -> {
        Platform.runLater(() -> {
          lCalibrationProgressIndicator.setProgress(pAdaptiveEngine.getProgressVariable()
                                                                   .get());
        });
      });
    }

    {
      VariableCheckBox lCheckBox =
                                 new VariableCheckBox("run until done",
                                                      pAdaptiveEngine.getRunUntilAllModulesReadyVariable());
      GridPane.setColumnSpan(lCheckBox, 3);
      add(lCheckBox, 0, lRow);
      lRow++;
    }

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    ArrayList<AdaptationModuleInterface<?>> lModuleList =
                                                        (ArrayList) pAdaptiveEngine.getModuleList();

    {
      for (AdaptationModuleInterface<?> lAdaptationModuleInterface : lModuleList)
      {
        addCalibrationModuleCheckBoxAndStatus(pAdaptiveEngine,
                                              lAdaptationModuleInterface,
                                              0,
                                              lRow++);
      }
    }

    {
      TabPane lTabPane = new TabPane();
      TitledPane lTitledPane = new TitledPane("Parameters", lTabPane);
      lTitledPane.setAnimated(false);

      for (AdaptationModuleInterface<?> lAdaptationModule : lModuleList)
      {
        try
        {
          Class<?> lAdaptationModuleClass =
                                          lAdaptationModule.getClass();
          String lAdaptationModuleClassName =
                                            lAdaptationModuleClass.getSimpleName();
          String lAdaptationModulePanelClassName =
                                                 lAdaptationModuleClass.getPackage()
                                                                       .getName()
                                                   + ".gui."
                                                   + lAdaptationModuleClassName
                                                   + "Panel";
          info("Searching for class %s as panel for adaptation module %s \n",
               lAdaptationModulePanelClassName,
               lAdaptationModuleClassName);
          Class<?> lClass =
                          Class.forName(lAdaptationModulePanelClassName);
          Constructor<?> lConstructor =
                                      lClass.getConstructor(lAdaptationModule.getClass());
          Node lPanel =
                      (Node) lConstructor.newInstance(lAdaptationModule);

          Tab lTab = new Tab(lAdaptationModule.getName());
          lTab.setClosable(false);
          lTab.setContent(lPanel);
          lTabPane.getTabs().add(lTab);

        }
        catch (ClassNotFoundException e)
        {
          warning("Cannot find panel for module %s \n",
                  lAdaptationModule.getName());
          // e.printStackTrace();
        }
        catch (Throwable e)
        {
          e.printStackTrace();
        }
      }
      GridPane.setColumnSpan(lTitledPane, 3);
      add(lTitledPane, 0, lRow);
      lRow++;
    }

  }

  private void addCalibrationModuleCheckBoxAndStatus(AdaptiveEngine<?> pAdaptiveEngine,
                                                     AdaptationModuleInterface<?> pAdaptationModule,
                                                     int pColumn,
                                                     int pRow)
  {
    String pName = pAdaptationModule.getName();
    Variable<Boolean> lCalibrateVariable =
                                         pAdaptationModule.getIsActiveVariable();
    Variable<String> pStatusStringVariable =
                                           pAdaptationModule.getStatusStringVariable();

    VariableCheckBox lCheckBox =
                               new VariableCheckBox(pName,
                                                    lCalibrateVariable);

    lCheckBox.getLabel().setAlignment(Pos.CENTER_LEFT);
    lCheckBox.getLabel().setMaxWidth(Double.MAX_VALUE);

    lCheckBox.getCheckBox().setAlignment(Pos.CENTER_RIGHT);
    lCheckBox.getCheckBox().setMaxWidth(Double.MAX_VALUE);

    Label lStatusLabel = new Label();
    Label lEstimatedTimeLabel = new Label();
    lStatusLabel.setPrefWidth(100);
    lEstimatedTimeLabel.setPrefWidth(200);
    VariableSetListener<String> lListener = (o, n) -> {

      Runnable lRunnable = () -> {
        lStatusLabel.setText(" -> " + n);
        lEstimatedTimeLabel.setText("Estimated time: "
                                    + pAdaptiveEngine.getEstimatedModuleStepTime(pAdaptationModule,
                                                                                 TimeUnit.MILLISECONDS)
                                    + " ms");
      };

      Platform.runLater(lRunnable);
    };

    pStatusStringVariable.addSetListener(lListener);

    CustomGridPane lGroupGridPane = new CustomGridPane(0, 0);
    lGroupGridPane.setAlignment(Pos.CENTER_LEFT);

    lGroupGridPane.add(lCheckBox.getCheckBox(), 0, 0);
    lGroupGridPane.add(lCheckBox.getLabel(), 1, 0);
    GridPane.setColumnSpan(lStatusLabel, 2);
    GridPane.setHgrow(lStatusLabel, Priority.ALWAYS);
    lGroupGridPane.add(lStatusLabel, 2, 0);
    GridPane.setColumnSpan(lEstimatedTimeLabel, 2);
    lGroupGridPane.add(lEstimatedTimeLabel, 4, 0);

    lGroupGridPane.setMaxWidth(Double.MAX_VALUE);

    GridPane.setColumnSpan(lGroupGridPane, 3);
    add(lGroupGridPane, pColumn, pRow);

    lCalibrateVariable.setCurrent();
  }
}
