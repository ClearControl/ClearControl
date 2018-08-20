package clearcontrol.gui.jfx.custom.visualconsole;

import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

import clearcontrol.gui.jfx.custom.labelgrid.LabelGrid;
import clearcontrol.gui.jfx.custom.multichart.MultiChart;
import clearcontrol.gui.jfx.custom.visualconsole.VisualConsoleInterface.ChartType;
import clearcontrol.gui.jfx.custom.visualconsole.listeners.ChartListenerInterface;
import clearcontrol.gui.jfx.custom.visualconsole.listeners.LabelGridListener;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Visual Console Panel displays debug/monitoring information in various ways
 * (grids, charts, ...)
 *
 * @author royer
 */
public class VisualConsolePanel extends BorderPane implements
                                ChartListenerInterface,
                                LabelGridListener
{

  private TabPane mTabPane;

  ConcurrentHashMap<String, Tab> mTabNameToTabMap =
                                                  new ConcurrentHashMap<>();

  ConcurrentHashMap<String, MultiChart> mTabNameToMultiChartMap =
                                                                new ConcurrentHashMap<>();

  ConcurrentHashMap<Pair<String, String>, ObservableList<Data<Number, Number>>> mTabNameAndSeriesNameToSeriesMap =
                                                                                                                 new ConcurrentHashMap<>();

  private ConcurrentHashMap<String, LabelGrid> mTabNameToLabelGridMap =
                                                                      new ConcurrentHashMap<>();

  /**
   * Instantiates a visual console
   * 
   * @param pVisualConsoleInterface
   *          adaptor
   */
  public VisualConsolePanel(VisualConsoleInterface pVisualConsoleInterface)
  {
    super();

    mTabPane = new TabPane();

    setCenter(mTabPane);

    pVisualConsoleInterface.addChartListener(this);
    pVisualConsoleInterface.addLabelGridListener(this);

    final ContextMenu lContextMenu = new ContextMenu();
    final MenuItem lClearItem = new MenuItem("Clear");
    lClearItem.setOnAction((e) -> clear());

    lContextMenu.getItems().addAll(lClearItem);

    mTabPane.setContextMenu(lContextMenu);
  }

  protected Tab getTab(TabPane lTabPane, String pTabName)
  {
    Tab lTab = mTabNameToTabMap.get(pTabName);

    if (lTab == null)
    {
      lTab = new Tab(pTabName);
      lTab.setClosable(false);
      lTabPane.getTabs().add(lTab);
      mTabNameToTabMap.put(pTabName, lTab);
    }
    return lTab;
  }

  public void clear()
  {
    mTabPane.getTabs().clear();
    mTabNameToTabMap.clear();
    mTabNameToMultiChartMap.clear();
    mTabNameAndSeriesNameToSeriesMap.clear();
    mTabNameToLabelGridMap.clear();
  }

  @Override
  public void addEntry(String pTabName,
                       boolean pClear,
                       String pColumnName,
                       String pRowName,
                       int pFontSize,
                       int pX,
                       int pY,
                       String pString)
  {
    Platform.runLater(() -> {

      String lTabName = pTabName + " grid";

      LabelGrid lLabelGrid = mTabNameToLabelGridMap.get(lTabName);

      if (lLabelGrid == null)
      {
        Tab lTab = getTab(mTabPane, lTabName);

        lLabelGrid = new LabelGrid();

        lTab.setContent(lLabelGrid);

        mTabNameToLabelGridMap.put(lTabName, lLabelGrid);

      }

      lLabelGrid.setColumnName(pX, pColumnName + pX);
      lLabelGrid.setRowName(pY, pRowName + pY);

      if (pClear)
        lLabelGrid.clear();

      Label lLabel = lLabelGrid.getLabel(pX, pY);

      lLabel.setFont(new Font(lLabel.getFont().getName(), pFontSize));

      lLabel.setText(pString);

    });

  }

  @Override
  public void configureChart(String pTabName,
                             String pSeriesName,
                             String pXAxisName,
                             String pYAxisName,
                             ChartType pChartType)
  {
    Platform.runLater(() -> {

      String lTabName = pTabName + " chart";

      MultiChart lMultiChart = mTabNameToMultiChartMap.get(lTabName);

      if (lMultiChart == null)
      {

        Tab lTab = getTab(mTabPane, lTabName);

        if (pChartType == ChartType.Line)
          lMultiChart = new MultiChart(LineChart.class);
        else if (pChartType == ChartType.Area)
          lMultiChart = new MultiChart(AreaChart.class);
        else if (pChartType == ChartType.Scatter)
          lMultiChart = new MultiChart(ScatterChart.class);

        lMultiChart.setChartTitle(lTabName);
        lMultiChart.setLegendVisible(false);
        lMultiChart.setXAxisLabel(pXAxisName);
        lMultiChart.setYAxisLabel(pYAxisName);
        lTab.setContent(lMultiChart);
        mTabNameToMultiChartMap.put(lTabName, lMultiChart);

      }

    });

  }

  @Override
  public void addPoint(String pTabName,
                       String pSeriesName,
                       boolean pClear,
                       double pX,
                       double pY)
  {
    Platform.runLater(() -> {

      String lTabName = pTabName + " chart";

      MultiChart lMultiChart = mTabNameToMultiChartMap.get(lTabName);

      ObservableList<Data<Number, Number>> lSeries =
                                                   mTabNameAndSeriesNameToSeriesMap.get(Pair.of(lTabName,
                                                                                                pSeriesName));
      if (lMultiChart == null)
      {
        return;
      }
      if (lSeries == null)
      {
        lSeries = lMultiChart.addSeries(pSeriesName);
        mTabNameAndSeriesNameToSeriesMap.put(Pair.of(lTabName,
                                                     pSeriesName),
                                             lSeries);
      }

      if (pClear)
        lSeries.clear();

      MultiChart.addData(lSeries, pX, pY);
    });

  }

}
