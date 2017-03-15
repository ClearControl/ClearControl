package clearcontrol.gui.jfx.sandbox;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class LineChartSample extends Application
{

  @Override
  public void start(Stage stage)
  {
    stage.setTitle("Line Chart Sample");
    // defining the axes
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();

    xAxis.setLabel("Number of Month");
    xAxis.setSide(Side.TOP);
    xAxis.setAnimated(false);

    yAxis.setAnimated(false);

    // creating the chart
    final LineChart<Number, Number> lLineChart =
                                               new LineChart<Number, Number>(xAxis,
                                                                             yAxis);

    lLineChart.setCreateSymbols(false);

    lLineChart.setTitle("Stock Monitoring, 2010");
    // defining a series
    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName("My portfolio");

    // populating the series with data
    series.getData().add(new XYChart.Data<Number, Number>(1, 23));
    series.getData().add(new XYChart.Data<Number, Number>(2, 14));
    series.getData().add(new XYChart.Data<Number, Number>(3, 15));
    series.getData().add(new XYChart.Data<Number, Number>(4, 24));
    series.getData().add(new XYChart.Data<Number, Number>(5, 34));
    series.getData().add(new XYChart.Data<Number, Number>(6, 36));
    series.getData().add(new XYChart.Data<Number, Number>(7, 22));
    series.getData().add(new XYChart.Data<Number, Number>(8, 45));
    series.getData().add(new XYChart.Data<Number, Number>(9, 43));
    series.getData().add(new XYChart.Data<Number, Number>(10, 17));
    series.getData().add(new XYChart.Data<Number, Number>(11, 29));
    series.getData().add(new XYChart.Data<Number, Number>(12, 25));

    Scene scene = new Scene(lLineChart, 800, 600);
    lLineChart.getData().add(series);

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args)
  {
    launch(args);
  }
}
