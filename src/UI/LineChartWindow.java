package UI;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
//



public class LineChartWindow extends Application {

    private String windowTitle = "WindowTitle";
    private String chartTitle = "Title";
    private String xLabel = "xLabel";
    private String yLabel = "yLabel";
    private boolean autorange = false;
    private double lowerBound = 0;
    private double upperBound = 0;
    private double tickUnit = 0;
    ObservableList<XYChart.Series<Number, Number>> chartData;

    public static void main(String[] args) {

//Application.launch(args);
    }

    public LineChartWindow(String windowTitle , String chartTitle, String xLabel, String yLabel, double lowerBound, double upperBound, ObservableList<XYChart.Series<Number, Number>> chartData) {
        
        this.windowTitle = windowTitle;
        this.chartTitle = chartTitle;
        this.xLabel = xLabel;
        this.yLabel = yLabel;

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        this.chartData = chartData;

    }

    @Override
    public void start(Stage stage) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xLabel);

// Customize the x-axis, so points are scattred uniformly
        xAxis.setAutoRanging(autorange);
        xAxis.setLowerBound(lowerBound);
        xAxis.setUpperBound(upperBound);

        xAxis.setTickUnit((upperBound - lowerBound)/10.0);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yLabel);
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle(chartTitle);

// Set the data for the chart
//ObservableList<XYChart.Series<Number,Number>> chartData = XYChartDataUtil.getDataList();
        chart.setData(chartData);
        StackPane root = new StackPane(chart);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.show();
    }
}
