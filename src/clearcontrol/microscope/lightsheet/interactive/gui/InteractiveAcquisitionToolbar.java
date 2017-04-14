package clearcontrol.microscope.lightsheet.interactive.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

import clearcontrol.core.variable.Variable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.onoffarray.OnOffArrayPane;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.togglebutton.CustomToggleButton;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator.IndicatorStyle;

/**
 * Interactive acquiistion toolbar
 *
 * @author royer
 */
public class InteractiveAcquisitionToolbar extends CustomGridPane
{

  /**
   * Instanciates an interactive acquisition toolbar given an interaction
   * acquisition device
   * 
   * @param pInteractiveAcquisition
   *          interactive acquisition
   */
  public InteractiveAcquisitionToolbar(InteractiveAcquisition pInteractiveAcquisition)
  {
    super();

    setPrefSize(300, 200);

    Variable<Boolean> lUseAcqStateVariable =
                                           pInteractiveAcquisition.getUseCurrentAcquisitionStateVariable();

    CustomToggleButton lUseAcqStateToggleButton =
                                                new CustomToggleButton("Using current Acquisition State",
                                                                       "Not using current Acquisition State",
                                                                       lUseAcqStateVariable);
    // lUseAcqStateToggleButton.setMinWidth(250);
    lUseAcqStateToggleButton.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(lUseAcqStateToggleButton, Priority.ALWAYS);
    GridPane.setColumnSpan(lUseAcqStateToggleButton, 3);
    add(lUseAcqStateToggleButton, 0, 0);

    Button lStart2D = new Button("Start 2D");
    lStart2D.setAlignment(Pos.CENTER);
    lStart2D.setMaxWidth(Double.MAX_VALUE);
    lStart2D.setOnAction((e) -> {
      pInteractiveAcquisition.start2DAcquisition();
    });
    GridPane.setColumnSpan(lStart2D, 2);
    add(lStart2D, 0, 1);

    Button lStart3D = new Button("Start 3D");
    lStart3D.setAlignment(Pos.CENTER);
    lStart3D.setMaxWidth(Double.MAX_VALUE);
    lStart3D.setOnAction((e) -> {
      pInteractiveAcquisition.start3DAcquisition();
    });
    GridPane.setColumnSpan(lStart3D, 2);
    add(lStart3D, 0, 2);

    Button lStop = new Button("Stop");
    lStop.setAlignment(Pos.CENTER);
    lStop.setMaxWidth(Double.MAX_VALUE);
    lStop.setOnAction((e) -> {
      pInteractiveAcquisition.stopAcquisition();
    });
    GridPane.setColumnSpan(lStop, 2);
    add(lStop, 0, 3);

    SimpleIndicator lAcquisitionStateIndicator =
                                               new SimpleIndicator();
    lAcquisitionStateIndicator.indicatorStyleProperty()
                              .set(IndicatorStyle.RED);
    pInteractiveAcquisition.getIsRunningVariable()
                           .addSetListener((o, n) -> {
                             lAcquisitionStateIndicator.onProperty()
                                                       .set(n);
                           });

    lAcquisitionStateIndicator.setMinSize(100, 100);
    GridPane.setColumnSpan(lAcquisitionStateIndicator, 1);
    GridPane.setRowSpan(lAcquisitionStateIndicator, 3);
    add(lAcquisitionStateIndicator, 2, 1);

    VariableSlider<Double> lIntervalSlider =
                                           new VariableSlider<Double>("Period (s)",
                                                                      pInteractiveAcquisition.getLoopPeriodVariable(),
                                                                      0.0,
                                                                      1000.0,
                                                                      0.001,
                                                                      100.0);
    lIntervalSlider.setAlignment(Pos.BASELINE_CENTER);
    GridPane.setHgrow(lIntervalSlider.getSlider(), Priority.ALWAYS);
    add(lIntervalSlider.getLabel(), 0, 4);
    add(lIntervalSlider.getSlider(), 1, 4);
    add(lIntervalSlider.getTextField(), 2, 4);

    VariableSlider<Double> lExposureSlider =
                                           new VariableSlider<Double>("Exp (s)",
                                                                      pInteractiveAcquisition.getExposureVariable(),
                                                                      0.0,
                                                                      1.0,
                                                                      0.001,
                                                                      0.1);
    lExposureSlider.setAlignment(Pos.BASELINE_CENTER);
    GridPane.setHgrow(lExposureSlider.getSlider(), Priority.ALWAYS);
    add(lExposureSlider.getLabel(), 0, 5);
    add(lExposureSlider.getSlider(), 1, 5);
    add(lExposureSlider.getTextField(), 2, 5);

    Label lActiveCamerasLabel = new Label("Active Cameras");
    add(lActiveCamerasLabel, 0, 6);
    GridPane.setColumnSpan(lActiveCamerasLabel, 2);

    OnOffArrayPane lAddOnOffArray = new OnOffArrayPane();

    for (int c =
               0; c < pInteractiveAcquisition.getNumberOfCameras(); c++)
    {
      lAddOnOffArray.addSwitch("" + c,
                               pInteractiveAcquisition.getActiveCameraVariable(c));
    }

    GridPane.setColumnSpan(lAddOnOffArray, 2);
    add(lAddOnOffArray, 2, 6);

    Variable<Boolean> lTriggerOnChangeVariable =
                                               pInteractiveAcquisition.getTriggerOnChangeVariable();

    CustomToggleButton lTriggerOnChangeToggleButton =
                                                    new CustomToggleButton("Trigger-on-change active",
                                                                           "Trigger-on-change inactive",
                                                                           lTriggerOnChangeVariable);
    lTriggerOnChangeToggleButton.setMaxWidth(Double.MAX_VALUE);
    GridPane.setHgrow(lTriggerOnChangeToggleButton, Priority.ALWAYS);
    GridPane.setColumnSpan(lTriggerOnChangeToggleButton, 3);
    add(lTriggerOnChangeToggleButton, 0, 9);

    Label lInteractiveAcquisitionStatusLabel = new Label();
    lInteractiveAcquisitionStatusLabel.setFont(Font.font("Monospaced",
                                                         lInteractiveAcquisitionStatusLabel.getFont()
                                                                                           .getSize()));
    lInteractiveAcquisitionStatusLabel.setAlignment(Pos.CENTER);
    lInteractiveAcquisitionStatusLabel.setMinWidth(400);
    lInteractiveAcquisitionStatusLabel.setMaxWidth(400);
    GridPane.setHgrow(lInteractiveAcquisitionStatusLabel,
                      Priority.SOMETIMES);
    GridPane.setColumnSpan(lInteractiveAcquisitionStatusLabel, 3);
    GridPane.setValignment(lInteractiveAcquisitionStatusLabel,
                           VPos.CENTER);
    add(lInteractiveAcquisitionStatusLabel, 0, 11);

    pInteractiveAcquisition.getAcquisitionCounterVariable()
                           .addSetListener((o, n) -> {
                             Platform.runLater(() -> {

                               long lAcquisitionCounter = n;
                               long lNumberOfCameras =
                                                     pInteractiveAcquisition.getNumberOfCameras();
                               long lNumberofStacks =
                                                    lAcquisitionCounter
                                                      * lNumberOfCameras;
                               String lStatus =
                                              String.format("Acquired: %5s stacks = %s cam. x %5s acqu.",
                                                            "" + lNumberofStacks,
                                                            "" + lNumberOfCameras,
                                                            "" + lAcquisitionCounter);
                               lInteractiveAcquisitionStatusLabel.setText(lStatus);
                             });

                           });

  }

}
