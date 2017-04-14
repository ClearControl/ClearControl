package clearcontrol.microscope.timelapse.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.var.checkbox.VariableCheckBox;
import clearcontrol.gui.jfx.var.combo.EnumComboBoxVariable;
import clearcontrol.gui.jfx.var.combo.enums.TimeUnitEnum;
import clearcontrol.gui.jfx.var.datetime.DateTimePickerVariable;
import clearcontrol.gui.jfx.var.textfield.VariableNumberTextField;
import clearcontrol.microscope.timelapse.TimelapseInterface;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator;
import eu.hansolo.enzo.simpleindicator.SimpleIndicator.IndicatorStyle;

/**
 * Timelapse toolbar
 *
 * @author royer
 */
public class TimelapseToolbar extends CustomGridPane
{
  protected int mRow;

  /**
   * Instanciates a timelapse toolbar.
   * 
   * @param pTimelapseInterface
   *          timelapse device
   */
  public TimelapseToolbar(TimelapseInterface pTimelapseInterface)
  {
    setPrefSize(500, 200);

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
        pTimelapseInterface.getStartSignalBooleanVariable()
                           .setEdgeAsync(false, true);
      });

      Button lStopTimelapse = new Button("Stop Timelapse");
      lStopTimelapse.setAlignment(Pos.CENTER);
      lStopTimelapse.setMaxWidth(Double.MAX_VALUE);
      GridPane.setColumnSpan(lStopTimelapse, 2);
      lStopTimelapse.setOnAction((e) -> {
        pTimelapseInterface.getStopSignalBooleanVariable()
                           .setEdgeAsync(false, true);
      });

      GridPane.setRowSpan(lAcquisitionStateIndicator, 2);
      GridPane.setColumnSpan(lStartTimelapse, 3);
      GridPane.setColumnSpan(lStopTimelapse, 3);

      add(lAcquisitionStateIndicator, 0, mRow);
      add(lStartTimelapse, 1, mRow++);
      add(lStopTimelapse, 1, mRow++);
    }

    {
      VariableNumberTextField<Long> lIntervalField =
                                                   new VariableNumberTextField<Long>("Interval:",
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
      VariableNumberTextField<Long> lActualIntervalField =
                                                         new VariableNumberTextField<Long>("Actual Interval:",
                                                                                           pTimelapseInterface.getTimelapseTimerVariable()
                                                                                                              .get()
                                                                                                              .getActualAcquisitionIntervalVariable(),
                                                                                           0L,
                                                                                           Long.MAX_VALUE,
                                                                                           1L);

      EnumComboBoxVariable<TimeUnitEnum> lActualIntervalTimeUnitBox =
                                                                    new EnumComboBoxVariable<TimeUnitEnum>(pTimelapseInterface.getTimelapseTimerVariable()
                                                                                                                              .get()
                                                                                                                              .getActualAcquisitionIntervalUnitVariable(),
                                                                                                           TimeUnitEnum.values());

      lActualIntervalField.getTextField().setEditable(false);

      GridPane.setColumnSpan(lActualIntervalField.getLabel(), 1);
      GridPane.setColumnSpan(lActualIntervalField.getTextField(), 1);
      GridPane.setColumnSpan(lActualIntervalTimeUnitBox, 1);

      add(lActualIntervalField.getLabel(), 1, mRow);
      add(lActualIntervalField.getTextField(), 2, mRow);
      add(lActualIntervalTimeUnitBox, 3, mRow);
      mRow++;
    }

    {

      VariableCheckBox lLimitNumberOfTimePointsCheckBox =
                                                        new VariableCheckBox("",
                                                                             pTimelapseInterface.getEnforceMaxNumberOfTimePointsVariable());

      VariableNumberTextField<Long> lMaxNumberOfTimePointsField =
                                                                new VariableNumberTextField<Long>("Max timepoints:",
                                                                                                  pTimelapseInterface.getMaxNumberOfTimePointsVariable(),
                                                                                                  0L,
                                                                                                  Long.MAX_VALUE,
                                                                                                  1L);

      GridPane.setColumnSpan(lLimitNumberOfTimePointsCheckBox, 1);
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
      VariableCheckBox lLimitTimelapseDurationCheckBox =
                                                       new VariableCheckBox("",
                                                                            pTimelapseInterface.getEnforceMaxDurationVariable());

      VariableNumberTextField<Long> lMaxDurationField =
                                                      new VariableNumberTextField<Long>("Max Duration:",
                                                                                        pTimelapseInterface.getMaxDurationVariable(),
                                                                                        0L,
                                                                                        Long.MAX_VALUE,
                                                                                        1L);

      EnumComboBoxVariable<TimeUnitEnum> lMaxDurationTimeUnitBox =
                                                                 new EnumComboBoxVariable<TimeUnitEnum>(pTimelapseInterface.getMaxDurationUnitVariable(),
                                                                                                        TimeUnitEnum.values());

      GridPane.setColumnSpan(lLimitTimelapseDurationCheckBox.getCheckBox(),
                             1);
      GridPane.setColumnSpan(lMaxDurationField.getLabel(), 1);
      GridPane.setColumnSpan(lMaxDurationField.getTextField(), 1);
      GridPane.setColumnSpan(lMaxDurationTimeUnitBox, 1);

      add(lLimitTimelapseDurationCheckBox.getCheckBox(), 0, mRow);
      add(lMaxDurationField.getLabel(), 1, mRow);
      add(lMaxDurationField.getTextField(), 2, mRow);
      add(lMaxDurationTimeUnitBox, 3, mRow);
      mRow++;
    }

    {

      Label lStartDateTimeLabel = new Label("Start:");

      DateTimePickerVariable lTimelapseStartDeadline =
                                                     new DateTimePickerVariable(pTimelapseInterface.getStartDateTimeVariable());
      lTimelapseStartDeadline.setEditable(false);
      lTimelapseStartDeadline.getEditor().setEditable(false);
      lTimelapseStartDeadline.setDisable(true);
      lTimelapseStartDeadline.setStyle("-fx-opacity: 1;");

      GridPane.setColumnSpan(lStartDateTimeLabel, 1);
      GridPane.setColumnSpan(lTimelapseStartDeadline, 2);

      add(lStartDateTimeLabel, 1, mRow);
      add(lTimelapseStartDeadline, 2, mRow);
      mRow++;
    }

    {
      VariableCheckBox lEnforceMaxDateTimeCheckBox =
                                                   new VariableCheckBox("",
                                                                        pTimelapseInterface.getEnforceMaxDateTimeVariable());

      Label lStopDateTimeLabel = new Label("Stop:");

      DateTimePickerVariable lTimelapseStopDeadline =
                                                    new DateTimePickerVariable(pTimelapseInterface.getMaxDateTimeVariable());

      GridPane.setColumnSpan(lEnforceMaxDateTimeCheckBox.getCheckBox(),
                             1);
      GridPane.setColumnSpan(lStopDateTimeLabel, 1);
      GridPane.setColumnSpan(lTimelapseStopDeadline, 2);

      add(lEnforceMaxDateTimeCheckBox.getCheckBox(), 0, mRow);
      add(lStopDateTimeLabel, 1, mRow);
      add(lTimelapseStopDeadline, 2, mRow);
      mRow++;
    }

  }

}
