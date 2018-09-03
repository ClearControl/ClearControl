package clearcontrol.gui.jfx.custom.visualconsole;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import clearcontrol.gui.jfx.custom.visualconsole.listeners.ChartListenerInterface;
import clearcontrol.gui.jfx.custom.visualconsole.listeners.LabelGridListener;

/**
 * 
 *
 * @author royer
 */
public interface VisualConsoleInterface
{
  /**
   * Chart listener list map
   */
  static final ConcurrentHashMap<VisualConsoleInterface, CopyOnWriteArrayList<ChartListenerInterface>> mChartListenerListMap =
                                                                                                                             new ConcurrentHashMap<>();

  /**
   * Label Grid listener list map
   */
  static final ConcurrentHashMap<VisualConsoleInterface, CopyOnWriteArrayList<LabelGridListener>> mLabelGridListenerListMap =
                                                                                                                            new ConcurrentHashMap<>();

  /**
   * Adds a chart listener
   * 
   * @param pChartListener
   *          chart listener
   */
  public default void addChartListener(ChartListenerInterface pChartListener)
  {
    CopyOnWriteArrayList<ChartListenerInterface> lCopyOnWriteArrayList =
                                                                       mChartListenerListMap.get(this);
    if (lCopyOnWriteArrayList == null)
    {
      lCopyOnWriteArrayList =
                            new CopyOnWriteArrayList<ChartListenerInterface>();
      mChartListenerListMap.put(this, lCopyOnWriteArrayList);
    }

    lCopyOnWriteArrayList.add(pChartListener);
  }

  @SuppressWarnings("javadoc")
  public static enum ChartType
  {
   Line, Scatter, Area
  }

  /**
   * Configure chart
   * 
   * @param pTabName
   *          tab name
   * @param pSeriesName
   *          series name
   * @param pXAxisName
   *          X axis name
   * @param pYAxisName
   *          Y axis name
   * @param pChartType
   *          cart type
   * 
   */
  public default void configureChart(String pTabName,
                                     String pSeriesName,
                                     String pXAxisName,
                                     String pYAxisName,
                                     ChartType pChartType)
  {
    CopyOnWriteArrayList<ChartListenerInterface> lCopyOnWriteArrayList =
                                                                       mChartListenerListMap.get(this);
    if (lCopyOnWriteArrayList != null)
      for (ChartListenerInterface lChartListenerInterface : lCopyOnWriteArrayList)
        lChartListenerInterface.configureChart(pTabName,
                                               pSeriesName,
                                               pXAxisName,
                                               pYAxisName,
                                               pChartType);

  }

  /**
   * Adds a data point to this channel
   * 
   * @param pTabName
   *          tab name
   * @param pSeriesName
   *          series name
   * @param pClear
   *          true for clearing before first point
   * @param x
   *          x coordinate
   * @param y
   *          y coordinate
   */
  public default void addPoint(String pTabName,
                               String pSeriesName,
                               boolean pClear,
                               double x,
                               double y)
  {
    CopyOnWriteArrayList<ChartListenerInterface> lCopyOnWriteArrayList =
                                                                       mChartListenerListMap.get(this);
    if (lCopyOnWriteArrayList != null)
      for (ChartListenerInterface lChartListenerInterface : lCopyOnWriteArrayList)
        lChartListenerInterface.addPoint(pTabName,
                                         pSeriesName,
                                         pClear,
                                         x,
                                         y);

  }

  /**
   * Adds a label grid listener
   * 
   * @param pLabelGridListener
   *          label grid listener
   */
  public default void addLabelGridListener(LabelGridListener pLabelGridListener)
  {
    CopyOnWriteArrayList<LabelGridListener> lCopyOnWriteArrayList =
                                                                  mLabelGridListenerListMap.get(this);

    if (lCopyOnWriteArrayList == null)
    {
      lCopyOnWriteArrayList =
                            new CopyOnWriteArrayList<LabelGridListener>();
      mLabelGridListenerListMap.put(this, lCopyOnWriteArrayList);
    }

    lCopyOnWriteArrayList.add(pLabelGridListener);
  }

  /**
   * Notifies label grid listeners of a new entry
   * 
   * 
   * @param pTabName
   *          tab name
   * @param pClear
   *          true for clearing before adding entry
   * @param pColumnName
   *          column name
   * @param pRowName
   *          row name
   * @param pFontSize
   *          font size
   * @param x
   *          x coordinate in grid
   * @param y
   *          y coordinate in grid
   * @param pString
   *          string to put at given grid coordinate
   */
  public default void addEntry(String pTabName,
                               boolean pClear,
                               String pColumnName,
                               String pRowName,
                               int pFontSize,
                               int x,
                               int y,
                               String pString)
  {
    CopyOnWriteArrayList<LabelGridListener> lCopyOnWriteArrayList =
                                                                  mLabelGridListenerListMap.get(this);

    if (lCopyOnWriteArrayList != null)
      for (LabelGridListener lLabelGridListener : lCopyOnWriteArrayList)
        lLabelGridListener.addEntry(pTabName,
                                    pClear,
                                    pColumnName,
                                    pRowName,
                                    pFontSize,
                                    x,
                                    y,
                                    pString);

  }

}
