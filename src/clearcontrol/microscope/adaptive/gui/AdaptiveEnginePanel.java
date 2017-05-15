package clearcontrol.microscope.adaptive.gui;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

import clearcontrol.gui.jfx.custom.labelgrid.LabelGrid;
import clearcontrol.gui.jfx.custom.multichart.MultiChart;
import clearcontrol.microscope.adaptive.AdaptiveEngine;
import clearcontrol.microscope.adaptive.modules.AdaptationModuleInterface;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

/**
 * Adaptor Panel
 *
 * @author royer
 */
public class AdaptiveEnginePanel extends BorderPane
{
  ConcurrentHashMap<String, Tab> mModuleToTabMap =
                                                 new ConcurrentHashMap<>();

  ConcurrentHashMap<AdaptationModuleInterface<?>, MultiChart> mModuleToMultiChartMap =
                                                                                     new ConcurrentHashMap<>();

  ConcurrentHashMap<Pair<AdaptationModuleInterface<?>, String>, ObservableList<Data<Number, Number>>> mModuleAndNameToSeriesMap =
                                                                                                                                new ConcurrentHashMap<>();

  private ConcurrentHashMap<Pair<AdaptationModuleInterface<?>, String>, LabelGrid> mModuleAndNameToLabelGridMap =
                                                                                                                new ConcurrentHashMap<>();

  private double mFontSize = 9;

  /**
   * Instantiates a panel given an adaptive engine
   * 
   * @param pAdaptiveEngine
   *          adaptor
   */
  public AdaptiveEnginePanel(AdaptiveEngine<?> pAdaptiveEngine)
  {
    super();

    TabPane lTabPane = new TabPane();

    setCenter(lTabPane);

    pAdaptiveEngine.addChartListener((m, n, c, sx, sy, x, y) ->

    {
      Platform.runLater(() -> {
        MultiChart lMultiChart = mModuleToMultiChartMap.get(m);

        if (lMultiChart == null)
        {
          Tab lTab = getTab(lTabPane, m.getName() + " chart");

          lMultiChart = new MultiChart(LineChart.class);
          lMultiChart.setChartTitle(m.getName());
          lMultiChart.setLegendVisible(false);
          lMultiChart.setXAxisLabel(sx);
          lMultiChart.setYAxisLabel(sy);
          lTab.setContent(lMultiChart);
          mModuleToMultiChartMap.put(m, lMultiChart);

        }

        ObservableList<Data<Number, Number>> lSeries =
                                                     mModuleAndNameToSeriesMap.get(Pair.of(m,
                                                                                           n));

        if (lSeries == null)
        {
          lSeries = lMultiChart.addSeries(n);
          mModuleAndNameToSeriesMap.put(Pair.of(m, n), lSeries);
        }

        if (c)
          lSeries.clear();

        MultiChart.addData(lSeries, x, y);
      });

    });

    pAdaptiveEngine.addLabelGridListener((m, n, c, sx, sy, x, y, s) ->

    {
      Platform.runLater(() -> {
        LabelGrid lLabelGrid =
                             mModuleAndNameToLabelGridMap.get(Pair.of(m,
                                                                      n));

        if (lLabelGrid == null)
        {
          Tab lTab = getTab(lTabPane, m.getName() + " grid");

          lLabelGrid = new LabelGrid();

          lTab.setContent(lLabelGrid);

          mModuleAndNameToLabelGridMap.put(Pair.of(m, n), lLabelGrid);

        }

        if (c)
          lLabelGrid.clear();


        lLabelGrid.setColumnName(x, sx + x);
        lLabelGrid.setRowName(y, sy + y);

        Label lLabel = lLabelGrid.getLabel(x, y);
        lLabel.setText(s);
        lLabel.setFont(new Font(lLabel.getFont().getName(),
                                mFontSize));
      });

    });
    /**/

  }

  protected Tab getTab(TabPane lTabPane, String pTabName)
  {
    Tab lTab = mModuleToTabMap.get(pTabName);

    if (lTab == null)
    {
      lTab = new Tab(pTabName);
      lTab.setClosable(false);
      lTabPane.getTabs().add(lTab);
      mModuleToTabMap.put(pTabName, lTab);
    }
    return lTab;
  }

}
