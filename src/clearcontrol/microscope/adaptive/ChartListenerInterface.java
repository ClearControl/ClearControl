package clearcontrol.microscope.adaptive;

import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;

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
   * @param pModule
   *          module from which this chart data originates
   * 
   * @param pName
   *          chart name/id
   * @param pClear
   *          true for clearing chart before adding the first point
   * @param pXAxisName
   *          X axis name
   * @param pYAxisName
   *          Y axis name
   * @param pX
   *          x coordinate
   * @param pY
   *          y coordinate
   */
  void addPoint(AdaptationModuleInterface<?> pModule,
                String pName,
                boolean pClear,
                String pXAxisName,
                String pYAxisName,
                double pX,
                double pY);

}
