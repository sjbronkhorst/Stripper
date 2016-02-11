package UI;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//

public class LineChartWindow extends Application {

    private String windowTitle = "WindowTitle";
    private String chartTitle = "Title";
    private String xLabel = "xLabel";
    private String yLabel = "yLabel";
    private boolean autorange = false;
    private boolean yHasBounds = false;
    private double xLowerBound = 0;
    private double xUpperBound = 0;
    private double yLowerBound = 0;
    private double yUpperBound = 0;
    private double tickUnit = 0;
    ObservableList<XYChart.Series<Number, Number>> chartData;
    
    private Button userScaleBtn = new Button("Set Scale");
    
    private TextField yUpperTf = new TextField();
    private TextField yLowerTf = new TextField();
    private TextField xUpperTf = new TextField();
    private TextField xLowerTf = new TextField();
    
    NumberAxis yAxis = new NumberAxis();

    public static void main(String[] args) {

//Application.launch(args);
    }

    public LineChartWindow(String windowTitle, String chartTitle, String xLabel, String yLabel, double lowerBound, double upperBound, ObservableList<XYChart.Series<Number, Number>> chartData) {

        this.windowTitle = windowTitle;
        this.chartTitle = chartTitle;
        this.xLabel = xLabel;
        this.yLabel = yLabel;

        this.xLowerBound = lowerBound;
        this.xUpperBound = upperBound;

        this.chartData = chartData;

    }

    public LineChartWindow(String windowTitle, String chartTitle, String xLabel, String yLabel, double xLowerBound, double xUpperBound, double yLowerBound, double yUpperBound, ObservableList<XYChart.Series<Number, Number>> chartData) {

        this.windowTitle = windowTitle;
        this.chartTitle = chartTitle;
        this.xLabel = xLabel;
        this.yLabel = yLabel;

        this.xLowerBound = xLowerBound;
        this.xUpperBound = xUpperBound;
        yHasBounds = true;
        this.yLowerBound = yLowerBound;
        this.yUpperBound = yUpperBound;

        this.chartData = chartData;

    }

    @Override
    public void start(Stage stage) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(xLabel);

// Customize the x-axis, so points are scattred uniformly
        xAxis.setAutoRanging(autorange);
        xAxis.setLowerBound(xLowerBound);
        xAxis.setUpperBound(xUpperBound);

        xAxis.setTickUnit((xUpperBound - xLowerBound) / 10.0);

        

        if (yHasBounds) {
            yAxis.setAutoRanging(autorange);
            yAxis.setLowerBound(yLowerBound);
            yAxis.setUpperBound(yUpperBound);

            yAxis.setTickUnit((yUpperBound - yLowerBound) / 10.0);
        }

        yAxis.setLabel(yLabel);
        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
       
        chart.setTitle(chartTitle);
        chart.setCreateSymbols(false);
              
       

// Set the data for the chart
//ObservableList<XYChart.Series<Number,Number>> chartData = XYChartDataUtil.getDataList();
        chart.setData(chartData);
        
        yUpperTf.setText(Double.toString(yUpperBound));
        yLowerTf.setText(Double.toString(yLowerBound));
        xUpperTf.setText(Double.toString(xUpperBound));
        xLowerTf.setText(Double.toString(xLowerBound));
        
        
        VBox scaleBox = new VBox(yUpperTf,yLowerTf,xUpperTf,xLowerTf,userScaleBtn);
        BorderPane root = new BorderPane(chart,null,null,scaleBox,null);
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.show();
        
        
        
        userScaleBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                
               yAxis.setUpperBound(Double.parseDouble(yUpperTf.getText()));
                yUpperBound = Double.parseDouble(yUpperTf.getText());
               
                yAxis.setLowerBound(Double.parseDouble(yLowerTf.getText()));
                yLowerBound = Double.parseDouble(yLowerTf.getText());
               
                xAxis.setUpperBound(Double.parseDouble(xUpperTf.getText()));
                xUpperBound = Double.parseDouble(xUpperTf.getText());
               
                xAxis.setLowerBound(Double.parseDouble(xLowerTf.getText()));
                xLowerBound = Double.parseDouble(xLowerTf.getText());
                
                
                yAxis.setTickUnit((yUpperBound - yLowerBound) / 10.0);
                
                
                
            }
        });
    }
}
