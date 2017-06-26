package clearcontrol.gui.jfx.custom.multichart;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Path;

import org.controlsfx.control.CheckListView;

/**
 * MultiChart allows the display of multiple series in a single chart and the
 * possibility to turn on and off display of individual series. It can use line,
 * Area and Scatter Charts.
 * 
 * @author royer
 */
public class MultiChart extends HBox
{

  private NumberAxis mXAxis, mYAxis;
  private XYChart<Number, Number> mXYChart;
  private CheckListView<MultiChartListItem> mCheckListView;
  private ObservableList<MultiChartListItem> mMultiChartItemList;
  private volatile double mXMin, mXMax, mYMin, mYMax;

  /**
   * Creates a MultiChart of a given type.
   * 
   * @param pChartClass
   *          class of chart: can be LineChart.class, AreaChart.class or
   *          ScatterChart.class
   */
  public MultiChart(Class<?> pChartClass)
  {
    super();

    setMaxWidth(Double.MAX_VALUE);

    mMultiChartItemList = FXCollections.observableArrayList();
    mCheckListView = new CheckListView<>(mMultiChartItemList);

    mCheckListView.setOnMouseClicked((e) -> {
      if (e.getClickCount() == 2)
      {
        MultiChartListItem lCurrentItemSelected =
                                                mCheckListView.getSelectionModel()
                                                              .getSelectedItem();
        mCheckListView.getCheckModel().clearChecks();
        mCheckListView.getCheckModel().check(lCurrentItemSelected);
      }
    });

    mCheckListView.getSelectionModel()
                  .getSelectedItems()
                  .addListener(new ListChangeListener<MultiChartListItem>()
                  {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends MultiChartListItem> pChange)
                    {
                      int lNumberOfItems = mMultiChartItemList.size();
                      for (int i = 0; i < lNumberOfItems; i++)
                      {

                        boolean lSelected =
                                          mCheckListView.getSelectionModel()
                                                        .isSelected(i);

                        setGlow(mXYChart, i, lSelected);

                      }
                    }
                  });

    mCheckListView.getCheckModel()
                  .getCheckedItems()
                  .addListener(new ListChangeListener<MultiChartListItem>()
                  {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends MultiChartListItem> pChange)
                    {
                      clearMinMax();
                      for (MultiChartListItem lItem : mMultiChartItemList)
                      {
                        boolean lChecked =
                                         mCheckListView.getCheckModel()
                                                       .isChecked(lItem);

                        Node lNode = lItem.getSeries().getNode();
                        if (lNode != null)
                          lNode.setVisible(lChecked);

                        for (Data<Number, Number> lData : lItem.getSeries()
                                                               .getData())
                          if (lData.getNode() != null)
                            lData.getNode().setVisible(lChecked);

                        if (lChecked)
                          adjustMinMax(lItem);
                      }
                      applyMinMax();
                    }
                  });

    Platform.runLater(() -> mCheckListView.getCheckModel()
                                          .checkAll());

    mXAxis = new NumberAxis();
    mYAxis = new NumberAxis();

    mXAxis.setAnimated(false);
    mYAxis.setAnimated(false);

    mXAxis.setAutoRanging(false);
    mYAxis.setAutoRanging(false);

    if (pChartClass == LineChart.class)
      mXYChart = new LineChart<Number, Number>(mXAxis, mYAxis);
    else if (pChartClass == AreaChart.class)
      mXYChart = new AreaChart<Number, Number>(mXAxis, mYAxis);
    else if (pChartClass == ScatterChart.class)
      mXYChart = new ScatterChart<Number, Number>(mXAxis, mYAxis);
    else
      throw new UnsupportedOperationException("Can't create chart for: "
                                              + pChartClass);

    mXYChart.setAnimated(false);
    mXYChart.setLegendVisible(true);
    mXYChart.setCursor(Cursor.CROSSHAIR);
    setDisplayMarkers(false);

    mXYChart.setOnMouseClicked((e) -> {
      if (e.getClickCount() == 2)
      {
        double lX = mXAxis.getValueForDisplay(e.getX()).doubleValue();
        double lY = mYAxis.getValueForDisplay(e.getY()).doubleValue();

        String lText = String.format("(%g,%g)", lX, lY);

        Bounds lChartBoundsInLocal = mXYChart.getBoundsInLocal();
        Bounds lChartBoundsInScreen =
                                    mXYChart.localToScreen(lChartBoundsInLocal);

        Tooltip lToolTip = new Tooltip(lText);
        lToolTip.setAutoHide(true);
        lToolTip.show(mXYChart,
                      lChartBoundsInScreen.getMinX() + e.getX(),
                      lChartBoundsInScreen.getMinY() + e.getY());
      }
    });

    // System.out.println(mXYChart.getStyle());

    // mLineChart.setStyle(".chart-line-symbol {-fx-background-radius: 0px;}");
    // mLineChart.setStyle(".default-color0.chart-symbol { -fx-background-color:
    // #860061, white; -fx-background-insets: 0, 2; -fx-background-radius: 5px;
    // -fx-padding: 5px;}");

    // tabB.setStyle("-fx-border-color:red; -fx-background-color: blue;");
    HBox.setHgrow(mXYChart, Priority.ALWAYS);
    getChildren().addAll(mCheckListView, mXYChart);
  }

  /**
   * Returns the check list view (list of chart series)
   * 
   * @return check list view
   */
  public CheckListView<MultiChartListItem> getCheckListView()
  {
    return mCheckListView;
  }

  /**
   * Returns the XY chart
   * 
   * @return XY chart
   */
  public XYChart<Number, Number> getXYChart()
  {
    return mXYChart;
  }

  /**
   * Sets the chart title.
   * 
   * @param pChartTitle
   *          chart title string
   */
  public void setChartTitle(String pChartTitle)
  {
    mXYChart.setTitle(pChartTitle);
  }

  /**
   * Sets whether markers should be set for already added series.
   * 
   * @param pDisplayMarkers
   *          true to add markers, false otherwise
   */
  public void setDisplayMarkers(boolean pDisplayMarkers)
  {
    if (mXYChart instanceof LineChart<?, ?>)
      ((LineChart<?, ?>) mXYChart).setCreateSymbols(pDisplayMarkers);
  }

  /**
   * Sets X axis label.
   * 
   * @param pLabelString
   *          X axis label
   */
  public void setXAxisLabel(String pLabelString)
  {
    mXAxis.setLabel(pLabelString);
  }

  /**
   * Sets Y axis label.
   * 
   * @param pLabelString
   *          Y axis label
   */
  public void setYAxisLabel(String pLabelString)
  {
    mYAxis.setLabel(pLabelString);
  }

  /**
   * Sets X axis display side
   * 
   * @param pAxisSide
   *          side
   */
  public void setXAxisSide(Side pAxisSide)
  {
    mXAxis.setSide(pAxisSide);
  }

  /**
   * Sets y axis display side.
   * 
   * @param pAxisSide
   *          on which side to display Y axis
   */
  public void setYAxisSide(Side pAxisSide)
  {
    mYAxis.setSide(pAxisSide);
  }

  /**
   * Set line width for a given series.
   * 
   * @param pIndex
   *          series index (adding order)
   * @param pWidth
   *          line width
   */
  public void setLineWidth(int pIndex, double pWidth)
  {
    MultiChartListItem lItem = mMultiChartItemList.get(pIndex);
    {
      Node lNode = lItem.getSeries().getNode();
      if (lNode != null)
        lNode.setStyle(String.format("-fx-stroke-width: %gpx;",
                                     pWidth));
    }
  }

  /**
   * Sets the marker radius for a given series. TODO: currently not working
   * 
   * @param pIndex
   *          series index
   * @param pRadius
   *          radius
   */
  public void setMarkerRadius(int pIndex, double pRadius)
  {
    MultiChartListItem lItem = mMultiChartItemList.get(pIndex);
    for (Data<Number, Number> lData : lItem.getSeries().getData())
      if (lData.getNode() != null)
        lData.getNode()
             .setStyle(String.format(".chart-line-symbol {-fx-background-radius: %g;}",
                                     pRadius));
  }

  /**
   * Sets teh visibility flag of the legend.
   * 
   * @param pDisplaylegend
   *          true for visible legend, false otherwise
   */
  public void setLegendVisible(boolean pDisplaylegend)
  {
    mXYChart.setLegendVisible(pDisplaylegend);
  }

  /**
   * Removes all series from chart.
   */
  public void clear()
  {
    mMultiChartItemList.clear();
    mXYChart.getData().clear();
    clearMinMax();
  }

  /**
   * Adds series to chart
   * 
   * @param pSeriesLabel
   *          series label
   * @return observable list to which data points should be added.
   */
  public ObservableList<Data<Number, Number>> addSeries(String pSeriesLabel)
  {
    XYChart.Series<Number, Number> lSeries = new XYChart.Series<>();
    lSeries.setName(pSeriesLabel);

    MultiChartListItem lMultiChartListItem =
                                           new MultiChartListItem(pSeriesLabel,
                                                                  lSeries);

    mMultiChartItemList.add(lMultiChartListItem);
    mXYChart.getData().add(lSeries);

    Runnable lUpdateMinMax = () -> {
      adjustMinMax(lMultiChartListItem);
      applyMinMax();
    };

    Platform.runLater(lUpdateMinMax);

    lSeries.getData()
           .addListener((ListChangeListener<? super Data<Number, Number>>) (c) -> lUpdateMinMax.run());

    return lSeries.getData();
  }

  /**
   * Returns the list that holds the data for a given chart.
   * 
   * @param pChartIndex
   *          Chart's index
   * @return data
   */
  public ObservableList<Data<Number, Number>> getDataFor(int pChartIndex)
  {
    return mMultiChartItemList.get(pChartIndex).getSeries().getData();
  }

  /**
   * Convenience method that adds data points to observable list.
   * 
   * @param pList
   *          observable list
   * @param pX
   *          x value
   * @param pY
   *          y value
   */
  public static void addData(ObservableList<Data<Number, Number>> pList,
                             Number pX,
                             Number pY)
  {
    pList.add(new Data<Number, Number>(pX, pY));
  }

  /**
   * Updates the min max bounds.Should be called after a substancial update to
   * the data.
   */
  public void updateMinMax()
  {
    clearMinMax();
    for (MultiChartListItem lMultiChartListItem : mMultiChartItemList)
    {
      adjustMinMax(lMultiChartListItem);
    }
    applyMinMax();
  }

  private void setGlow(XYChart<Number, Number> lineChart,
                       int pIndex,
                       boolean pGlow)
  {
    // make the first series in the chart glow when you mouse near it.
    Node lNode =
               lineChart.lookup(".chart-series-line.series" + pIndex);
    if (lNode != null && lNode instanceof Path)
    {
      /*System.out.println("series " + pIndex
      										+ " applying glow state :"
      										+ pGlow);/**/
      final Path lPath = (Path) lNode;
      final Glow lGlow = new Glow(0.8);
      lPath.setEffect(pGlow ? lGlow : null);
    }
  }

  private void clearMinMax()
  {
    mXMin = Double.POSITIVE_INFINITY;
    mXMax = Double.NEGATIVE_INFINITY;
    mYMin = Double.POSITIVE_INFINITY;
    mYMax = Double.NEGATIVE_INFINITY;
  }

  private void adjustMinMax(MultiChartListItem pItem)
  {
    ObservableList<Data<Number, Number>> lDatas = pItem.getSeries()
                                                       .getData();

    for (Data<Number, Number> lDataPoint : lDatas)
    {
      mXMin = Math.min(mXMin, lDataPoint.getXValue().doubleValue());
      mXMax = Math.max(mXMax, lDataPoint.getXValue().doubleValue());

      mYMin = Math.min(mYMin, lDataPoint.getYValue().doubleValue());
      mYMax = Math.max(mYMax, lDataPoint.getYValue().doubleValue());
    }
  }

  private void applyMinMax()
  {

    if (Double.isFinite(mXMin) && Double.isFinite(mXMax)
        && Double.isFinite(mYMin)
        && Double.isFinite(mYMax))
    {

      mXAxis.setLowerBound(mXMin);
      mXAxis.setUpperBound(mXMax);
      mYAxis.setLowerBound(mYMin);
      mYAxis.setUpperBound(mYMax);

      double lTickUnitX = roundPOT((mXMax - mXMin) / 10);
      double lTickUnitY = roundPOT((mYMax - mYMin) / 10);

      mXAxis.setTickUnit(lTickUnitX);
      mYAxis.setTickUnit(lTickUnitY);
    }
  }

  private double roundPOT(double pValue)
  {
    double lRounded = Math.pow(10, Math.ceil(Math.log10(pValue)));
    return lRounded;
  }

}
