package clearcontrol.gui.jfx.custom.multichart.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import clearcontrol.gui.jfx.custom.multichart.MultiChart;

/**
 * Mulichart demo
 *
 * @author royer
 */
public class MultiChartDemo extends Application
{

  @Override
  public void start(Stage stage)
  {
    HBox root = new HBox();
    Scene scene = new Scene(root, 1400, 400);
    stage.setScene(scene);
    stage.setTitle("MultiChartDemo");

    {
      MultiChart lMultiLineChart = new MultiChart(LineChart.class);
      lMultiLineChart.setChartTitle("Demo LineChart");

      lMultiLineChart.setXAxisLabel("x axis");
      lMultiLineChart.setYAxisLabel("y axis");

      ObservableList<Data<Number, Number>> lSeries1 =
                                                    lMultiLineChart.addSeries("series 1");
      MultiChart.addData(lSeries1, 0, 0);
      MultiChart.addData(lSeries1, 1, 1);
      MultiChart.addData(lSeries1, 2, 2);

      ObservableList<Data<Number, Number>> lSeries2 =
                                                    lMultiLineChart.addSeries("series 2");
      MultiChart.addData(lSeries2, 0, 4.6);
      MultiChart.addData(lSeries2, 1.5, 1.3);
      MultiChart.addData(lSeries2, 2.2, 3.9);

      ObservableList<Data<Number, Number>> lSeries3 =
                                                    lMultiLineChart.addSeries("series 3");
      MultiChart.addData(lSeries3, 1, 44);
      MultiChart.addData(lSeries3, 3, 15);
      MultiChart.addData(lSeries3, 5, 36);

      lMultiLineChart.setLineWidth(0, 1);
      lMultiLineChart.setLineWidth(1, 2);
      lMultiLineChart.setLineWidth(2, 4);
      // lMultiChart.setMarkerRadius(1);

      for (int i = 0; i < 5; i++)
      {
        ObservableList<Data<Number, Number>> lSeriesK =
                                                      lMultiLineChart.addSeries("series "
                                                                                + i);

        for (int j = 0; j < 100; j++)
          MultiChart.addData(lSeriesK,
                             0.01 * j,
                             0.005 * i * j + Math.random());

      }

      lMultiLineChart.setDisplayMarkers(false);

      root.getChildren().add(lMultiLineChart);
    }

    {
      MultiChart lMultiAreaChart = new MultiChart(AreaChart.class);
      lMultiAreaChart.setChartTitle("Demo AreaChart");

      lMultiAreaChart.setXAxisLabel("x axis");
      lMultiAreaChart.setYAxisLabel("y axis");

      ObservableList<Data<Number, Number>> lSeries1 =
                                                    lMultiAreaChart.addSeries("series 1");
      MultiChart.addData(lSeries1, 0, 0);
      MultiChart.addData(lSeries1, 1, 1);
      MultiChart.addData(lSeries1, 2, 2);

      ObservableList<Data<Number, Number>> lSeries2 =
                                                    lMultiAreaChart.addSeries("series 2");
      MultiChart.addData(lSeries2, 0, 4.6);
      MultiChart.addData(lSeries2, 1.5, 1.3);
      MultiChart.addData(lSeries2, 2.2, 3.9);

      ObservableList<Data<Number, Number>> lSeries3 =
                                                    lMultiAreaChart.addSeries("series 3");
      MultiChart.addData(lSeries3, 2, 3);
      MultiChart.addData(lSeries3, 3, 4);
      MultiChart.addData(lSeries3, 4, 5);

      lMultiAreaChart.setLineWidth(0, 1);
      lMultiAreaChart.setLineWidth(1, 2);
      lMultiAreaChart.setLineWidth(2, 4);

      lMultiAreaChart.setDisplayMarkers(false);

      root.getChildren().add(lMultiAreaChart);
    }

    {
      MultiChart lMultiScatterChart =
                                    new MultiChart(ScatterChart.class);
      lMultiScatterChart.setChartTitle("Demo ScatterChart");

      lMultiScatterChart.setXAxisLabel("x axis");
      lMultiScatterChart.setYAxisLabel("y axis");

      ObservableList<Data<Number, Number>> lSeries1 =
                                                    lMultiScatterChart.addSeries("series 1");
      MultiChart.addData(lSeries1, 0, 0);
      MultiChart.addData(lSeries1, 1, 1);
      MultiChart.addData(lSeries1, 2, 2);

      ObservableList<Data<Number, Number>> lSeries2 =
                                                    lMultiScatterChart.addSeries("series 2");
      MultiChart.addData(lSeries2, 0, 4.6);
      MultiChart.addData(lSeries2, 1.5, 1.3);
      MultiChart.addData(lSeries2, 2.2, 3.9);

      ObservableList<Data<Number, Number>> lSeries3 =
                                                    lMultiScatterChart.addSeries("series 3");
      MultiChart.addData(lSeries3, 1, 7);
      MultiChart.addData(lSeries3, 2, 5);
      MultiChart.addData(lSeries3, 3, 4);

      lMultiScatterChart.setLineWidth(0, 1);
      lMultiScatterChart.setLineWidth(1, 2);
      lMultiScatterChart.setLineWidth(2, 4);

      lMultiScatterChart.setDisplayMarkers(false);

      root.getChildren().add(lMultiScatterChart);
    }

    stage.show();
  }

  /**
   * Main
   * 
   * @param args
   *          NA
   */
  public static void main(String[] args)
  {
    launch(args);
  }
}
