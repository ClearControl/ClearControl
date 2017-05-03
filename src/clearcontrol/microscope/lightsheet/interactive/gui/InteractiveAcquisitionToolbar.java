package clearcontrol.microscope.lightsheet.interactive.gui;

import clearcontrol.core.variable.Variable;
import clearcontrol.devices.cameras.gui.CameraResolutionGrid;
import clearcontrol.devices.optomech.opticalswitch.gui.OpticalSwitchDevicePanel;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.lcd.VariableLCD;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.gui.jfx.var.togglebutton.VariableToggleButton;
import clearcontrol.microscope.lightsheet.component.opticalswitch.LightSheetOpticalSwitch;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisition;
import clearcontrol.microscope.lightsheet.interactive.InteractiveAcquisitionModes;
import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator.IndicatorStyle;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Interactive acquiistion toolbar
 *
 * @author royer
 */
public class InteractiveAcquisitionToolbar extends CustomGridPane
{

  private VariableToggleButton mUseAcqStateToggleButton;

  /**
   * Instantiates an interactive acquisition toolbar given an interaction
   * acquisition device
   * 
   * @param pInteractiveAcquisition
   *          interactive acquisition
   */
  public InteractiveAcquisitionToolbar(InteractiveAcquisition pInteractiveAcquisition)
  {
    super();

    setPrefSize(400, 200);

    int[] lPercent = new int[]{20,40,40}; 
    for (int i = 0; i < 3; i++)
    {
      ColumnConstraints lColumnConstraints = new ColumnConstraints();
      lColumnConstraints.setPercentWidth(lPercent[i]);
      getColumnConstraints().add(lColumnConstraints);
    }/**/
    
    int lRow = 0;

    {
      SimpleIndicator lAcquisitionStateIndicator =
                                                 new SimpleIndicator();
      lAcquisitionStateIndicator.indicatorStyleProperty()
                                .set(IndicatorStyle.RED);
      pInteractiveAcquisition.getIsRunningVariable()
                             .addSetListener((o, n) -> {
                               lAcquisitionStateIndicator.onProperty()
                                                         .set(n);
                             });

      lAcquisitionStateIndicator.setMinSize(50, 50);
      GridPane.setColumnSpan(lAcquisitionStateIndicator, 1);
      GridPane.setRowSpan(lAcquisitionStateIndicator, 2);
      add(lAcquisitionStateIndicator, 0, 0);
    }

    {
      Button lStart2D = new Button("Start 2D");
      lStart2D.setAlignment(Pos.CENTER);
      lStart2D.setMaxWidth(Double.MAX_VALUE);
      lStart2D.setOnAction((e) -> {
        pInteractiveAcquisition.start2DAcquisition();

      });
      GridPane.setColumnSpan(lStart2D, 1);
      add(lStart2D, 1, lRow++);
    }

    {
      Button lStart3D = new Button("Start 3D");
      lStart3D.setAlignment(Pos.CENTER);
      lStart3D.setMaxWidth(Double.MAX_VALUE);
      lStart3D.setOnAction((e) -> {
        pInteractiveAcquisition.start3DAcquisition();
        if (mUseAcqStateToggleButton != null)
          mUseAcqStateToggleButton.selectedProperty().set(true);
      });
      GridPane.setColumnSpan(lStart3D, 1);
      add(lStart3D, 1, lRow++);
    }

    {
      Button lStop = new Button("Stop");
      lStop.setAlignment(Pos.CENTER);
      lStop.setMaxWidth(Double.MAX_VALUE);
      lStop.setOnAction((e) -> {
        InteractiveAcquisitionModes lCurrentAcquisitionMode =
                                                            pInteractiveAcquisition.getCurrentAcquisitionMode();
        pInteractiveAcquisition.stopAcquisition();
        if (lCurrentAcquisitionMode == InteractiveAcquisitionModes.Acquisition3D)
          if (mUseAcqStateToggleButton != null
              && mUseAcqStateToggleButton.selectedProperty().get())
            mUseAcqStateToggleButton.selectedProperty().set(false);
      });
      GridPane.setColumnSpan(lStop, 3);
      add(lStop, 0, lRow++);
    }

    {
      Lcd lTimeLapseLcdDisplay =
                               LcdBuilder.create()
                                         // .prefWidth(480)
                                         // .prefHeight(192)
                                         .styleClass(Lcd.STYLE_CLASS_WHITE)
                                         .backgroundVisible(true)
                                         .value(0)
                                         .minValue(0)
                                         .maxValue(Double.MAX_VALUE)
                                         .foregroundShadowVisible(true)
                                         .crystalOverlayVisible(false)
                                         .title("time points")
                                         .titleVisible(false)
                                         .batteryVisible(false)
                                         .signalVisible(false)
                                         .alarmVisible(false)
                                         .unit("tp")
                                         .unitVisible(true)
                                         .decimals(0)

                                         .minMeasuredValueDecimals(2)
                                         .minMeasuredValueVisible(false)
                                         .maxMeasuredValueDecimals(2)
                                         .maxMeasuredValueVisible(false)
                                         .formerValueVisible(false)
                                         .threshold(26)
                                         .thresholdVisible(false)
                                         .trendVisible(false)
                                         .trend(Lcd.Trend.RISING)
                                         .numberSystemVisible(true)
                                         .lowerRightTextVisible(false)
                                         .valueFont(Lcd.LcdFont.LCD)
                                         .animated(false)
                                         .build();

      VariableLCD<Long> lVariableLCD =
                                     new VariableLCD<Long>(lTimeLapseLcdDisplay,
                                                           pInteractiveAcquisition.getAcquisitionCounterVariable());
      GridPane.setRowSpan(lVariableLCD, 2);
      GridPane.setHalignment(lVariableLCD, HPos.CENTER);
      add(lVariableLCD, 2, 0);

    }
    
    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 4);
      add(lSeparator, 0, lRow);
      lRow++;
    }

    {
      NumberVariableTextField<Double> lIntervalTextField =
                                                         new NumberVariableTextField<Double>("Period (s)",
                                                                                             pInteractiveAcquisition.getLoopPeriodVariable(),
                                                                                             0.001,
                                                                                             10.0,
                                                                                             0.001);

      ComboBox<Double> lTypicalIntervalComboBox =
                                                new ComboBox<>(FXCollections.observableArrayList(0.001,
                                                                                                 0.010,
                                                                                                 0.050,
                                                                                                 0.100,
                                                                                                 0.200,
                                                                                                 0.500,
                                                                                                 1.0,
                                                                                                 2.0,
                                                                                                 5.0,
                                                                                                 10.0,
                                                                                                 20.0,
                                                                                                 30.0));

      lTypicalIntervalComboBox.valueProperty()
                              .addListener((c, o, n) -> {
                                if (n != null)
                                  pInteractiveAcquisition.getLoopPeriodVariable()
                                                         .setAsync(n);
                              });

      lIntervalTextField.getTextField()
                        .textProperty()
                        .addListener((c, o, n) -> {
                          if (o != null && !o.equals(n))
                            lTypicalIntervalComboBox.getSelectionModel()
                                                    .clearSelection();
                        });

      lIntervalTextField.setAlignment(Pos.BASELINE_CENTER);
      add(lIntervalTextField.getLabel(), 0, lRow);
      add(lTypicalIntervalComboBox, 1, lRow);
      add(lIntervalTextField.getTextField(), 2, lRow);
      lRow++;
    }

    {
      NumberVariableTextField<Double> lExposureTextField =
                                                         new NumberVariableTextField<Double>("Exp (s)",
                                                                                             pInteractiveAcquisition.getExposureVariable(),
                                                                                             0.0,
                                                                                             1.0,
                                                                                             0.001);

      ComboBox<Double> lTypicalExposuresComboBox =
                                                 new ComboBox<>(FXCollections.observableArrayList(0.001,
                                                                                                  0.005,
                                                                                                  0.010,
                                                                                                  0.015,
                                                                                                  0.020,
                                                                                                  0.025,
                                                                                                  0.030,
                                                                                                  0.050,
                                                                                                  0.100,
                                                                                                  0.200,
                                                                                                  0.500,
                                                                                                  1.0,
                                                                                                  2.0,
                                                                                                  5.0,
                                                                                                  10.0));

      lTypicalExposuresComboBox.valueProperty()
                               .addListener((c, o, n) -> {
                                 if (n != null)
                                   pInteractiveAcquisition.getExposureVariable()
                                                          .setAsync(n);
                               });

      lExposureTextField.getTextField()
                        .textProperty()
                        .addListener((c, o, n) -> {
                          if (o != null && !o.equals(n))
                            lTypicalExposuresComboBox.getSelectionModel()
                                                     .clearSelection();
                        });

      lExposureTextField.setAlignment(Pos.BASELINE_CENTER);
      add(lExposureTextField.getLabel(), 0, lRow);
      add(lTypicalExposuresComboBox, 1, lRow);
      add(lExposureTextField.getTextField(), 2, lRow);
      lRow++;
    }
    
    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 4);
      add(lSeparator, 0, lRow);
      lRow++;
    }

    {
      Variable<Boolean> lUseAcqStateVariable =
                                             pInteractiveAcquisition.getUseCurrentAcquisitionStateVariable();

      mUseAcqStateToggleButton =
                               new VariableToggleButton("Using current Acquisition State",
                                                        "Not using current Acquisition State",
                                                        lUseAcqStateVariable);
      // lUseAcqStateToggleButton.setMinWidth(250);
      mUseAcqStateToggleButton.setMaxWidth(Double.MAX_VALUE);
      GridPane.setHgrow(mUseAcqStateToggleButton, Priority.ALWAYS);
      GridPane.setColumnSpan(mUseAcqStateToggleButton, 3);
      add(mUseAcqStateToggleButton, 0, lRow);

      lRow++;
    }

    {
      Variable<Boolean> lTriggerOnChangeVariable =
                                                 pInteractiveAcquisition.getTriggerOnChangeVariable();

      VariableToggleButton lTriggerOnChangeToggleButton =
                                                        new VariableToggleButton("Trigger-on-change active",
                                                                                 "Trigger-on-change inactive",
                                                                                 lTriggerOnChangeVariable);
      lTriggerOnChangeToggleButton.setMaxWidth(Double.MAX_VALUE);
      GridPane.setHgrow(lTriggerOnChangeToggleButton,
                        Priority.ALWAYS);
      GridPane.setColumnSpan(lTriggerOnChangeToggleButton, 3);
      add(lTriggerOnChangeToggleButton, 0, lRow);

      lRow++;
    }
    
    {
      Separator lSeparator = new Separator();
      lSeparator.setOrientation(Orientation.HORIZONTAL);
      GridPane.setColumnSpan(lSeparator, 4);
      add(lSeparator, 0, lRow);
      lRow++;
    }

    {

      CameraResolutionGrid.ButtonEventHandler lButtonHandler =
                                                             (w,
                                                              h) -> {
                                                               return event -> {
                                                                 pInteractiveAcquisition.getLightSheetMicroscope()
                                                                                        .setCameraWidthHeight(w,
                                                                                                              h);
                                                               };
                                                             };

      CameraResolutionGrid lGridPane =
                                     new CameraResolutionGrid(lButtonHandler,
                                                              7,
                                                              11);
      lGridPane.setAlignment(Pos.BASELINE_CENTER);
      GridPane.setHalignment(lGridPane, HPos.CENTER);
      GridPane.setHgrow(lGridPane, Priority.ALWAYS);
      GridPane.setColumnSpan(lGridPane, 3);
      add(lGridPane, 0, lRow);
      
      //setGridLinesVisible(true);

      lRow++;
    }

    
    {
      LightSheetOpticalSwitch lDevice = pInteractiveAcquisition.getLightSheetMicroscope().getDevice(LightSheetOpticalSwitch.class, 0);
      
      OpticalSwitchDevicePanel lLightSheetOpticalSwitchPanel = new OpticalSwitchDevicePanel(lDevice);
      
      
      GridPane.setHalignment(lLightSheetOpticalSwitchPanel, HPos.CENTER);
      GridPane.setHgrow(lLightSheetOpticalSwitchPanel, Priority.ALWAYS);
      GridPane.setColumnSpan(lLightSheetOpticalSwitchPanel, 3);
      add(lLightSheetOpticalSwitchPanel, 0, lRow);

      lRow++;
    }

  }

}
