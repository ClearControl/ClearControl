package clearcontrol.microscope.timelapse.gui;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.gui.jfx.var.combo.ClassComboBoxVariable;
import clearcontrol.gui.jfx.var.combo.EnumComboBoxVariable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;
import clearcontrol.gui.jfx.var.datetime.DateTimePickerVariable;
import clearcontrol.gui.jfx.var.file.VariableFileChooser;
import clearcontrol.gui.jfx.var.lcd.VariableLCD;
import clearcontrol.gui.jfx.var.textfield.NumberVariableTextField;
import clearcontrol.gui.jfx.var.textfield.StringVariableTextField;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import eu.hansolo.enzo.lcd.Lcd;
import eu.hansolo.enzo.lcd.LcdBuilder;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator.IndicatorStyle;

/**
 * Timelapse toolbar
 *
 * @author royer
 */
public class TimelapseToolbar extends CustomGridPane
{
  TimelapseInterface mTimelapseInterface;
  /**
   * Instanciates a timelapse toolbar.
   * 
   * @param pTimelapseInterface
   *          timelapse device
   */
  public TimelapseToolbar(TimelapseInterface pTimelapseInterface)
  {
    mTimelapseInterface = pTimelapseInterface;
    setPrefSize(500, 300);

    mRow = 0;

    {
      SimpleIndicator lAcquisitionStateIndicator =
                                                 new SimpleIndicator();
      lAcquisitionStateIndicator.indicatorStyleProperty()
                                .set(IndicatorStyle.RED);
      pTimelapseInterface.getIsRunningVariable()
                         .addSetListener((o, n) -> {
                           lAcquisitionStateIndicator.onProperty()
                                                     .set(n);
                         });

      lAcquisitionStateIndicator.setMinSize(50, 50);

      Button lStartTimelapse = new Button("Start Timelapse");
      lStartTimelapse.setAlignment(Pos.CENTER);
      lStartTimelapse.setMaxWidth(Double.MAX_VALUE);
      GridPane.setColumnSpan(lStartTimelapse, 2);
      lStartTimelapse.setOnAction((e) -> {
        pTimelapseInterface.startTimelapse();

      });

      Button lStopTimelapse = new Button("Stop Timelapse");
      lStopTimelapse.setAlignment(Pos.CENTER);
      lStopTimelapse.setMaxWidth(Double.MAX_VALUE);
      GridPane.setColumnSpan(lStopTimelapse, 2);
      lStopTimelapse.setOnAction((e) -> {
        pTimelapseInterface.stopTimelapse();
      });

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
                                                           pTimelapseInterface.getTimePointCounterVariable());

      GridPane.setRowSpan(lAcquisitionStateIndicator, 2);
      GridPane.setColumnSpan(lStartTimelapse, 2);
      GridPane.setColumnSpan(lStopTimelapse, 2);
      GridPane.setHalignment(lVariableLCD, HPos.CENTER);
      GridPane.setColumnSpan(lVariableLCD, 1);
      GridPane.setRowSpan(lVariableLCD, 2);

      add(lAcquisitionStateIndicator, 0, mRow);
      add(lVariableLCD, 3, mRow);
      add(lStartTimelapse, 1, mRow++);
      add(lStopTimelapse, 1, mRow++);

    }

    addSeparator();

    {
      NumberVariableTextField<Long> lIntervalField =
                                                   new NumberVariableTextField<Long>("Interval:",
                                                                                     pTimelapseInterface.getTimelapseTimerVariable()
                                                                                                        .get()
                                                                                                        .getAcquisitionIntervalVariable(),
                                                                                     0L,
                                                                                     Long.MAX_VALUE,
                                                                                     1L);

      EnumComboBoxVariable<TimeUnitEnum> lIntervalTimeUnitBox =
                                                              new EnumComboBoxVariable<TimeUnitEnum>(pTimelapseInterface.getTimelapseTimerVariable()
                                                                                                                        .get()
                                                                                                                        .getAcquisitionIntervalUnitVariable(),
                                                                                                     TimeUnitEnum.values());

      GridPane.setColumnSpan(lIntervalField.getLabel(), 1);
      GridPane.setColumnSpan(lIntervalField.getTextField(), 1);
      GridPane.setColumnSpan(lIntervalTimeUnitBox, 1);

      add(lIntervalField.getLabel(), 1, mRow);
      add(lIntervalField.getTextField(), 2, mRow);
      add(lIntervalTimeUnitBox, 3, mRow);
      mRow++;
    }




    {

      VariableCheckBox lLimitNumberOfTimePointsCheckBox =
                                                        new VariableCheckBox("",
                                                                             pTimelapseInterface.getEnforceMaxNumberOfTimePointsVariable());

      NumberVariableTextField<Long> lMaxNumberOfTimePointsField =
                                                                new NumberVariableTextField<Long>("Max timepoints:",
                                                                                                  pTimelapseInterface.getMaxNumberOfTimePointsVariable(),
                                                                                                  0L,
                                                                                                  Long.MAX_VALUE,
                                                                                                  1L);

      GridPane.setHalignment(lLimitNumberOfTimePointsCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lLimitNumberOfTimePointsCheckBox.getCheckBox(),
                             1);
      GridPane.setColumnSpan(lMaxNumberOfTimePointsField.getLabel(),
                             1);
      GridPane.setColumnSpan(lMaxNumberOfTimePointsField.getTextField(),
                             2);

      add(lLimitNumberOfTimePointsCheckBox.getCheckBox(), 0, mRow);
      add(lMaxNumberOfTimePointsField.getLabel(), 1, mRow);
      add(lMaxNumberOfTimePointsField.getTextField(), 2, mRow);
      mRow++;
    }





    {
      VariableFileChooser lRootFolderChooser =
                                             new VariableFileChooser("Folder:",
                                                                     pTimelapseInterface.getRootFolderVariable(),
                                                                     true);

      GridPane.setColumnSpan(lRootFolderChooser.getLabel(), 1);
      GridPane.setColumnSpan(lRootFolderChooser.getTextField(), 2);
      GridPane.setColumnSpan(lRootFolderChooser.getButton(), 1);

      add(lRootFolderChooser.getLabel(), 0, mRow);
      add(lRootFolderChooser.getTextField(), 1, mRow);
      add(lRootFolderChooser.getButton(), 3, mRow);
      mRow++;
    }

    {

      StringVariableTextField lPostFixTextField =
                                                new StringVariableTextField("Name:",
                                                                            pTimelapseInterface.getDataSetNamePostfixVariable());

      ClassComboBoxVariable lStackSinkComboBox =
                                               new ClassComboBoxVariable(pTimelapseInterface.getCurrentFileStackSinkTypeVariable(),
                                                                         pTimelapseInterface.getFileStackSinkTypeList(),
                                                                         100);

      GridPane.setColumnSpan(lPostFixTextField.getLabel(), 1);
      GridPane.setColumnSpan(lPostFixTextField.getTextField(), 2);
      GridPane.setColumnSpan(lStackSinkComboBox, 1);

      add(lPostFixTextField.getLabel(), 0, mRow);
      add(lPostFixTextField.getTextField(), 1, mRow);
      add(lStackSinkComboBox, 3, mRow);

      mRow++;
    }






    {

      VariableCheckBox lSaveStacksCheckBox =
          new VariableCheckBox("Save stacks",
                               pTimelapseInterface.getSaveStacksVariable());

      GridPane.setColumnSpan(lSaveStacksCheckBox.getLabel(), 1);
      GridPane.setHalignment(lSaveStacksCheckBox.getCheckBox(),
                             HPos.RIGHT);

      add(lSaveStacksCheckBox.getCheckBox(), 0, mRow);
      add(lSaveStacksCheckBox.getLabel(), 1, mRow);

      mRow++;
    }

  }

  public CustomGridPane buildAdvancedOptionsGripPane() {
    CustomGridPane lAdvancedOptionsGridPane = new CustomGridPane();


    {
      NumberVariableTextField<Long> lActualIntervalField =
          new NumberVariableTextField<Long>("Actual Interval:",
                                            mTimelapseInterface.getTimelapseTimerVariable()
                                                               .get()
                                                               .getActualAcquisitionIntervalVariable(),
                                            0L,
                                            Long.MAX_VALUE,
                                            1L);

      EnumComboBoxVariable<TimeUnitEnum> lActualIntervalTimeUnitBox =
          new EnumComboBoxVariable<TimeUnitEnum>(mTimelapseInterface.getTimelapseTimerVariable()
                                                                    .get()
                                                                    .getActualAcquisitionIntervalUnitVariable(),
                                                 TimeUnitEnum.values());

      lActualIntervalField.getTextField().setEditable(false);

      GridPane.setColumnSpan(lActualIntervalField.getLabel(), 1);
      GridPane.setColumnSpan(lActualIntervalField.getTextField(), 1);
      GridPane.setColumnSpan(lActualIntervalTimeUnitBox, 1);

      lAdvancedOptionsGridPane.add(lActualIntervalField.getLabel(), 1, mRow);
      lAdvancedOptionsGridPane.add(lActualIntervalField.getTextField(), 2, mRow);
      lAdvancedOptionsGridPane.add(lActualIntervalTimeUnitBox, 3, mRow);
      mRow++;
    }

    lAdvancedOptionsGridPane.addSeparator();


    {
      VariableCheckBox lLimitTimelapseDurationCheckBox =
          new VariableCheckBox("",
                               mTimelapseInterface.getEnforceMaxDurationVariable());

      NumberVariableTextField<Long> lMaxDurationField =
          new NumberVariableTextField<Long>("Max Duration:",
                                            mTimelapseInterface.getMaxDurationVariable(),
                                            0L,
                                            Long.MAX_VALUE,
                                            1L);

      EnumComboBoxVariable<TimeUnitEnum> lMaxDurationTimeUnitBox =
          new EnumComboBoxVariable<TimeUnitEnum>(mTimelapseInterface.getMaxDurationUnitVariable(),
                                                 TimeUnitEnum.values());

      GridPane.setHalignment(lLimitTimelapseDurationCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lLimitTimelapseDurationCheckBox.getCheckBox(),
                             1);
      GridPane.setColumnSpan(lMaxDurationField.getLabel(), 1);
      GridPane.setColumnSpan(lMaxDurationField.getTextField(), 1);
      GridPane.setColumnSpan(lMaxDurationTimeUnitBox, 1);

      lAdvancedOptionsGridPane.add(lLimitTimelapseDurationCheckBox.getCheckBox(), 0, mRow);
      lAdvancedOptionsGridPane.add(lMaxDurationField.getLabel(), 1, mRow);
      lAdvancedOptionsGridPane.add(lMaxDurationField.getTextField(), 2, mRow);
      lAdvancedOptionsGridPane.add(lMaxDurationTimeUnitBox, 3, mRow);
      mRow++;
    }

    {

      Label lStartDateTimeLabel = new Label("Start:");

      DateTimePickerVariable lTimelapseStartDeadline =
          new DateTimePickerVariable(mTimelapseInterface.getStartDateTimeVariable());
      lTimelapseStartDeadline.setEditable(false);
      lTimelapseStartDeadline.getEditor().setEditable(false);
      lTimelapseStartDeadline.setDisable(true);
      lTimelapseStartDeadline.setStyle("-fx-opacity: 1;");

      GridPane.setColumnSpan(lStartDateTimeLabel, 1);
      GridPane.setColumnSpan(lTimelapseStartDeadline, 2);

      lAdvancedOptionsGridPane.add(lStartDateTimeLabel, 1, mRow);
      lAdvancedOptionsGridPane.add(lTimelapseStartDeadline, 2, mRow);
      mRow++;
    }

    {
      VariableCheckBox lEnforceMaxDateTimeCheckBox =
          new VariableCheckBox("",
                               mTimelapseInterface.getEnforceMaxDateTimeVariable());

      Label lStopDateTimeLabel = new Label("Stop:");

      DateTimePickerVariable lTimelapseStopDeadline =
          new DateTimePickerVariable(mTimelapseInterface.getMaxDateTimeVariable());

      GridPane.setHalignment(lEnforceMaxDateTimeCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lEnforceMaxDateTimeCheckBox.getCheckBox(),
                             1);
      GridPane.setColumnSpan(lStopDateTimeLabel, 1);
      GridPane.setColumnSpan(lTimelapseStopDeadline, 2);

      lAdvancedOptionsGridPane.add(lEnforceMaxDateTimeCheckBox.getCheckBox(), 0, mRow);
      lAdvancedOptionsGridPane.add(lStopDateTimeLabel, 1, mRow);
      lAdvancedOptionsGridPane.add(lTimelapseStopDeadline, 2, mRow);
      mRow++;
    }

    lAdvancedOptionsGridPane.addSeparator();





    lAdvancedOptionsGridPane.addSeparator();

    {
      VariableCheckBox lAutoPilotCheckBox =
          new VariableCheckBox("AutoPilot",
                               mTimelapseInterface.getAdaptiveEngineOnVariable());

      GridPane.setHalignment(lAutoPilotCheckBox.getCheckBox(),
                             HPos.RIGHT);
      GridPane.setColumnSpan(lAutoPilotCheckBox.getLabel(), 1);
      GridPane.setColumnSpan(lAutoPilotCheckBox.getCheckBox(), 1);

      GridPane.setColumnSpan(lAutoPilotCheckBox.getLabel(), 3);
      lAdvancedOptionsGridPane.add(lAutoPilotCheckBox.getCheckBox(), 0, mRow);
      lAdvancedOptionsGridPane.add(lAutoPilotCheckBox.getLabel(), 1, mRow);

      mRow++;
    }

    {
      NumberVariableTextField<Integer> lMinStepsTextField =
          new NumberVariableTextField<>("Min & max steps per tp:",
                                        mTimelapseInterface.getMinAdaptiveEngineStepsVariable());

      NumberVariableTextField<Integer> lMaxStepsTextField =
          new NumberVariableTextField<>("",
                                        mTimelapseInterface.getMaxAdaptiveEngineStepsVariable());

      lMinStepsTextField.getTextField().setPrefWidth(50);
      lMaxStepsTextField.getTextField().setPrefWidth(50);

      GridPane.setHalignment(lMinStepsTextField.getLabel(),
                             HPos.RIGHT);

      HBox lHBox = new HBox(lMinStepsTextField.getTextField(),
                            new Label("    "),
                            lMaxStepsTextField.getTextField());

      GridPane.setColumnSpan(lHBox, 2);
      GridPane.setHgrow(lHBox, Priority.ALWAYS);

      GridPane.setHalignment(lHBox, HPos.CENTER);

      lAdvancedOptionsGridPane.add(lMinStepsTextField.getLabel(), 1, mRow);
      lAdvancedOptionsGridPane.add(lHBox, 2, mRow);

      mRow++;
    }

    {

      TitledPane lTitledPane =
          new TitledPane("Advanced options",
                         lAdvancedOptionsGridPane);
      lTitledPane.setAnimated(false);
      lTitledPane.setExpanded(false);
      GridPane.setColumnSpan(lTitledPane, 4);
      add(lTitledPane, 0, mRow);
      mRow++;

    }
    return lAdvancedOptionsGridPane;
  }



}
