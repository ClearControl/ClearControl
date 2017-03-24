package clearcontrol.microscope.lightsheet.acquisition.gui.jfx;

import java.util.concurrent.ConcurrentHashMap;

import clearcontrol.core.variable.Variable;
import clearcontrol.core.variable.bounded.BoundedVariable;
import clearcontrol.gui.jfx.custom.gridpane.CustomGridPane;
import clearcontrol.gui.jfx.custom.multichart.MultiChart;
import clearcontrol.gui.jfx.var.rangeslider.VariableRangeSlider;
import clearcontrol.gui.jfx.var.slider.VariableSlider;
import clearcontrol.gui.jfx.var.textfield.VariableNumberTextField;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.acquisition.InterpolatedAcquisitionState;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.GridPane;

/**
 * Acquisition state panel
 *
 * @author royer
 */
public class AcquisitionStatePanel extends CustomGridPane
{

  /**
   * Prefered width
   */
  public static final double cPrefWidth = 0;

  /**
   * Prefered height
   */
  public static final double cPrefHeight = 0;

  private ConcurrentHashMap<String, ObservableList<Data<Number, Number>>> mNameToDataMap =
                                                                                         new ConcurrentHashMap<>();

  /**
   * Acquisition state
   * 
   * @param pAcquisitionState
   *          acquisition state
   */
  public AcquisitionStatePanel(InterpolatedAcquisitionState pAcquisitionState)
  {
    super();

    BoundedVariable<Number> lStageXVariable =
                                            pAcquisitionState.getStageXVariable();
    BoundedVariable<Number> lStageYVariable =
                                            pAcquisitionState.getStageYVariable();
    BoundedVariable<Number> lStageZVariable =
                                            pAcquisitionState.getStageZVariable();

    VariableSlider<Number> lStageXSlider =
                                         new VariableSlider<Number>("stageX",
                                                                    lStageXVariable,
                                                                    5);

    VariableSlider<Number> lStageYSlider =
                                         new VariableSlider<Number>("stageY",
                                                                    lStageYVariable,
                                                                    5);

    VariableSlider<Number> lStageZSlider =
                                         new VariableSlider<Number>("stageZ",
                                                                    lStageZVariable,
                                                                    5);

    // Collecting variables:

    BoundedVariable<Number> lXLow =
                                  pAcquisitionState.getStackXLowVariable();
    BoundedVariable<Number> lXHigh =
                                   pAcquisitionState.getStackXHighVariable();

    BoundedVariable<Number> lYLow =
                                  pAcquisitionState.getStackYLowVariable();
    BoundedVariable<Number> lYHigh =
                                   pAcquisitionState.getStackYHighVariable();

    BoundedVariable<Number> lZLow =
                                  pAcquisitionState.getStackZLowVariable();
    BoundedVariable<Number> lZHigh =
                                   pAcquisitionState.getStackZHighVariable();

    Variable<Number> lZStep =
                            pAcquisitionState.getStackZStepVariable();

    // Creating elements:

    VariableRangeSlider<Number> lXRangeSlider =
                                              new VariableRangeSlider<>("X-range",
                                                                        lXLow,
                                                                        lXHigh,
                                                                        lXLow.getMinVariable(),
                                                                        lXHigh.getMaxVariable(),
                                                                        0,
                                                                        5);

    VariableRangeSlider<Number> lYRangeSlider =
                                              new VariableRangeSlider<>("Y-range",
                                                                        lYLow,
                                                                        lYHigh,
                                                                        lYLow.getMinVariable(),
                                                                        lYHigh.getMaxVariable(),
                                                                        0,
                                                                        5);

    VariableRangeSlider<Number> lZRangeSlider =
                                              new VariableRangeSlider<>("Z-range",
                                                                        lZLow,
                                                                        lZHigh,
                                                                        lZLow.getMinVariable(),
                                                                        lZHigh.getMaxVariable(),
                                                                        lZStep,
                                                                        5);

    VariableNumberTextField<Number> lZStepTextField =
                                                    new VariableNumberTextField<Number>("Z-step",
                                                                                        lZStep,
                                                                                        0,
                                                                                        Double.POSITIVE_INFINITY,
                                                                                        0);
    lZStepTextField.getTextField().setPrefWidth(50);

    MultiChart lMultiChart = new MultiChart(LineChart.class);
    lMultiChart.setLegendVisible(false);

    int lNumberOfDetectionArms =
                               pAcquisitionState.getNumberOfDetectionArms();
    int lNumberOfIlluminationArms =
                                  pAcquisitionState.getNumberOfLightSheets();

    for (int d = 0; d < lNumberOfDetectionArms; d++)
    {
      mNameToDataMap.put("DZ" + d, lMultiChart.addSeries("DZ" + d));
    }

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
    {
      mNameToDataMap.put("IX" + i, lMultiChart.addSeries("IX" + i));
      mNameToDataMap.put("IY" + i, lMultiChart.addSeries("IY" + i));
      mNameToDataMap.put("IZ" + i, lMultiChart.addSeries("IZ" + i));
      mNameToDataMap.put("IA" + i, lMultiChart.addSeries("IA" + i));
      mNameToDataMap.put("IB" + i, lMultiChart.addSeries("IB" + i));
      mNameToDataMap.put("IH" + i, lMultiChart.addSeries("IH" + i));
      mNameToDataMap.put("IW" + i, lMultiChart.addSeries("IW" + i));
      mNameToDataMap.put("IP" + i, lMultiChart.addSeries("IP" + i));
    }

    updateChart(pAcquisitionState, lMultiChart);

    // Laying out components:

    add(lStageXSlider.getLabel(), 0, 0);
    add(lStageXSlider.getTextField(), 1, 0);
    add(lStageXSlider.getSlider(), 2, 0);

    add(lStageYSlider.getLabel(), 0, 1);
    add(lStageYSlider.getTextField(), 1, 1);
    add(lStageYSlider.getSlider(), 2, 1);

    add(lStageZSlider.getLabel(), 0, 2);
    add(lStageZSlider.getTextField(), 1, 2);
    add(lStageZSlider.getSlider(), 2, 2);

    add(lXRangeSlider.getLabel(), 0, 3);
    add(lXRangeSlider.getLowTextField(), 1, 3);
    add(lXRangeSlider.getRangeSlider(), 2, 3);
    add(lXRangeSlider.getHighTextField(), 3, 3);
    add(lZStepTextField.getLabel(), 5, 3);
    add(lZStepTextField.getTextField(), 6, 3);

    add(lYRangeSlider.getLabel(), 0, 4);
    add(lYRangeSlider.getLowTextField(), 1, 4);
    add(lYRangeSlider.getRangeSlider(), 2, 4);
    add(lYRangeSlider.getHighTextField(), 3, 4);

    add(lZRangeSlider.getLabel(), 0, 5);
    add(lZRangeSlider.getLowTextField(), 1, 5);
    add(lZRangeSlider.getRangeSlider(), 2, 5);
    add(lZRangeSlider.getHighTextField(), 3, 5);

    add(lMultiChart, 0, 6);
    GridPane.setColumnSpan(lMultiChart, 8);

    // Update events:

    pAcquisitionState.addChangeListener((e) -> {
      if (isVisible())
      {
        Platform.runLater(() -> {
          updateChart(pAcquisitionState, lMultiChart);
        });
      }

    });
  }

  private void updateChart(InterpolatedAcquisitionState pAcquisitionState,
                           MultiChart pMultiChart)
  {

    try
    {
      int lDepth = pAcquisitionState.getStackDepth();
      int lNumberOfDetectionArms =
                                 pAcquisitionState.getNumberOfDetectionArms();
      int lNumberOfIlluminationArms =
                                    pAcquisitionState.getNumberOfLightSheets();

      for (int d = 0; d < lNumberOfDetectionArms; d++)
      {
        ObservableList<Data<Number, Number>> lData =
                                                   mNameToDataMap.get("DZ"
                                                                      + d);
        lData.clear();
        for (int zi = 0; zi < lDepth; zi++)
        {
          MultiChart.addData(lData,
                             pAcquisitionState.getZRamp(zi),
                             pAcquisitionState.get(LightSheetDOF.DZ,
                                                   zi,
                                                   d));
        }
      }

      for (LightSheetDOF lLightSheetDOF : LightSheetDOF.values())
        if (lLightSheetDOF != LightSheetDOF.DZ)
        {
          for (int i = 0; i < lNumberOfIlluminationArms; i++)
          {
            ObservableList<Data<Number, Number>> lData =
                                                       mNameToDataMap.get(lLightSheetDOF.toString()
                                                                          + i);
            lData.clear();
            for (int zi = 0; zi < lDepth; zi++)
            {
              MultiChart.addData(lData,
                                 pAcquisitionState.getZRamp(zi),
                                 pAcquisitionState.get(lLightSheetDOF,
                                                       zi,
                                                       i));
            }
          }
        }

      pMultiChart.updateMinMax();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }
}
