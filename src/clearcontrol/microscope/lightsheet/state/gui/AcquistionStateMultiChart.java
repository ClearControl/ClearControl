package clearcontrol.microscope.lightsheet.state.gui;

import java.util.concurrent.ConcurrentHashMap;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;

import clearcontrol.gui.jfx.custom.multichart.MultiChart;
import clearcontrol.microscope.lightsheet.LightSheetDOF;
import clearcontrol.microscope.lightsheet.state.InterpolatedAcquisitionState;

/**
 * Multichart displaying the different acquisition DOFs versus imaging depth
 *
 * @author royer
 */
public class AcquistionStateMultiChart extends MultiChart
{

  private ConcurrentHashMap<String, ObservableList<Data<Number, Number>>> mNameToDataMap =
                                                                                         new ConcurrentHashMap<>();

  /**
   * Instantiates an acquisition multichart for a given acquisition state
   * 
   * @param pAcquisitionState
   *          acquisition state
   */
  public AcquistionStateMultiChart(InterpolatedAcquisitionState pAcquisitionState)
  {
    super(LineChart.class);

    setLegendVisible(false);
    setMaxWidth(Double.MAX_VALUE);
    setMaxHeight(Double.MAX_VALUE);

    int lNumberOfDetectionArms =
                               pAcquisitionState.getNumberOfDetectionArms();
    int lNumberOfIlluminationArms =
                                  pAcquisitionState.getNumberOfLightSheets();

    for (int d = 0; d < lNumberOfDetectionArms; d++)
    {
      mNameToDataMap.put("DZ" + d, addSeries("DZ" + d));
    }

    for (int i = 0; i < lNumberOfIlluminationArms; i++)
    {
      mNameToDataMap.put("IX" + i, addSeries("IX" + i));
      mNameToDataMap.put("IY" + i, addSeries("IY" + i));
      mNameToDataMap.put("IZ" + i, addSeries("IZ" + i));
      mNameToDataMap.put("IA" + i, addSeries("IA" + i));
      mNameToDataMap.put("IB" + i, addSeries("IB" + i));
      mNameToDataMap.put("IH" + i, addSeries("IH" + i));
      mNameToDataMap.put("IW" + i, addSeries("IW" + i));
      mNameToDataMap.put("IP" + i, addSeries("IP" + i));
    }

    updateChart(pAcquisitionState);
  }

  /**
   * Updates this chart with the given acquisition state
   * 
   * @param pAcquisitionState
   *          acquisition state to use for updating this multichart
   */
  public void updateChart(InterpolatedAcquisitionState pAcquisitionState)
  {

    try
    {
      int lDepth = pAcquisitionState.getNumberOfZPlanesVariable()
                                    .get()
                                    .intValue();
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

      updateMinMax();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }

  }

}
