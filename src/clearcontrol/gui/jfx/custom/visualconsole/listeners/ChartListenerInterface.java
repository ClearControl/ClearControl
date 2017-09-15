package clearcontrol.gui.jfx.custom.visualconsole.listeners;

import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;

/**
 * Chart listeenr interface
 *
 * @author royer
 */
public interface ChartListenerInterface
{

  /**
   * Adds a point to the chart of given name, possibly clearing the chart just
   * before.
   * 
   * @param pTabName
   *          name of tab to sue to display data point
   * @param pSeriesName
   *          series name
   * @param pXAxisName
   *          X axis name
   * @param pYAxisName
   *          Y axis name
   * @param pChartType
   *          chart type
   * 
   */
  void configureChart(String pTabName,
                      String pSeriesName,
                      String pXAxisName,
                      String pYAxisName,
                      ChartType pChartType);

  /**
   * Adds a point to the chart of given name, possibly clearing the chart just
   * before.
   * 
   * @param pTabName
   *          name of tab to sue to display data point
   * @param pSeriesName
   *          series name
   * @param pClear
   *          true for clearing chart before adding the first point
   * @param pX
   *          x coordinate
   * @param pY
   *          y coordinate
   */
  void addPoint(String pTabName,
                String pSeriesName,
                boolean pClear,
                double pX,
                double pY);

}
