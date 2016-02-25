package UI;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import stripper.BucklingCurve;
import stripper.Node;
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
    TableView<XYChart.Series<Number, Number>> seriesTable = new TableView<>(XYChartDataUtil.getDataList());
    TableView<XYChart.Data<Number, Number>> seriesDataTable = new TableView<>();

    ModelViewPane mvp = new ModelViewPane(Defaults.getBaseModel());
    Pane viewPane = mvp.getPane();

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
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    stop();
                } catch (Exception ex) {
                    Logger.getLogger(LineChartWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);

// Customize the x-axis, so points are scattred uniformly
        xAxis.setAutoRanging(autorange);
        xAxis.setLowerBound(xLowerBound);
        xAxis.setUpperBound(xUpperBound);

        xAxis.setTickUnit((xUpperBound - xLowerBound) / 10.0);

        seriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addNameColumn(seriesTable);

        TableColumn<XYChart.Data<Number, Number>, String> xCol = new TableColumn<>("X");
        xCol.setCellValueFactory(new PropertyValueFactory<>("xValue"));

        TableColumn<XYChart.Data<Number, Number>, String> yCol = new TableColumn<>("Y");
        yCol.setCellValueFactory(new PropertyValueFactory<>("yValue"));

        seriesDataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        seriesDataTable.getColumns().addAll(xCol, yCol);

        seriesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                doUpdateDataTable();
            }
        });

        seriesTable.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                doUpdateDataTable();
            }
        });

        seriesDataTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                doSetCrossHair();
            }
        });

        seriesDataTable.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                doSetCrossHair();
            }
        });

        if (yHasBounds) {
            yAxis.setAutoRanging(autorange);
            yAxis.setLowerBound(yLowerBound);
            yAxis.setUpperBound(yUpperBound);

            yAxis.setTickUnit((yUpperBound - yLowerBound) / 10.0);
        }

        yAxis.setLabel(yLabel);

        chart.setTitle(chartTitle);
        chart.setCreateSymbols(false);

        chart.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("X = " + event.getSceneX() + "Y = " + event.getSceneY());
            }
        });

// Set the data for the chart
//ObservableList<XYChart.Series<Number,Number>> chartData = XYChartDataUtil.getDataList();
        chart.setData(chartData);

        yUpperTf.setText(Double.toString(yUpperBound));
        yLowerTf.setText(Double.toString(yLowerBound));
        xUpperTf.setText(Double.toString(xUpperBound));
        xLowerTf.setText(Double.toString(xLowerBound));

        VBox scaleBox = new VBox(yUpperTf, yLowerTf, xUpperTf, xLowerTf, userScaleBtn);

        viewPane.setMaxSize(300, 300);
        viewPane.setPrefSize(300, 300);

        seriesTable.setPrefSize(300, 150);
        seriesDataTable.setPrefSize(300, 300);

        GridPane rightBox = new GridPane();
        rightBox.add(seriesTable, 0, 0);
        rightBox.add(seriesDataTable, 0, 1);
        rightBox.add(viewPane, 0, 2);

        BorderPane root = new BorderPane(chart, null, rightBox, scaleBox, null);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(windowTitle);
        stage.show();
        stage.setFullScreen(true);

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

    public void doUpdateDataTable() {
        seriesDataTable.getItems().clear();

        for (XYChart.Data<Number, Number> dataPoint : seriesTable.getSelectionModel().getSelectedItem().getData()) {
            seriesDataTable.getItems().add(dataPoint);
        }

      
       

    }

    public void doSetCrossHair() {
        XYChartDataUtil.setCrossHair(seriesDataTable.getSelectionModel().getSelectedItem().getXValue(), seriesDataTable.getSelectionModel().getSelectedItem().getYValue());

        String bcName = seriesTable.getSelectionModel().getSelectedItem().getName();

        BucklingCurve bc = Defaults.getBucklinCurve(bcName);
        

        if (bc != (null)) {

            if (seriesDataTable.getSelectionModel().getSelectedItem() != null) {

                double tableSelection = (Double) seriesDataTable.getSelectionModel().getSelectedItem().getXValue();
                Model m = bc.getModel(tableSelection);

                if (m != null) {

                    
                   
                                        m.setDisplacedState(bc.getPoint((Double) seriesDataTable.getSelectionModel().getSelectedItem().getXValue()));
                    mvp.setModel(m);
                     mvp.draw();
                }

            }
        }
        
       
    }

    public void addNameColumn(TableView<XYChart.Series<Number, Number>> table) {
// Name column is non-editable
        table.getColumns().add(XYChartDataUtil.getSeriesNameColumn());
    }

}
