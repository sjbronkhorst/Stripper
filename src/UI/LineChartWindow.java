package UI;

import fsm.Model;
import Utils.XYChartDataUtil;
import Utils.DrawingHandler;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.ImageStorage.ImageType;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import fsm.BucklingCurve;
import fsm.BucklingDataPoint;
import DSM.DSMCalcs;
import Utils.FileHandler;
import Utils.MyMath;
import fsm.Node;
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
    ObservableList<XYChart.Series<Number, Number>> chartData = XYChartDataUtil.getSeriesList();
    TableView<BucklingCurve> seriesTable = new TableView<>(XYChartDataUtil.getCurveList());
    TableView<BucklingDataPoint> seriesDataTable = new TableView<>();

    ModelViewPane mvp = new ModelViewPane(Defaults.getBaseModel());
    Pane viewPane = mvp.getPane();

    private Button userScaleBtn = new Button("Set Scale");

    private TextField yUpperTf = new TextField();
    private TextField yLowerTf = new TextField();
    private TextField xUpperTf = new TextField();
    private TextField xLowerTf = new TextField();

    NumberAxis yAxis = new NumberAxis();

    
    public LineChartWindow(String windowTitle, String chartTitle, String xLabel, String yLabel, double lowerBound, double upperBound) {

        this.windowTitle = windowTitle;
        this.chartTitle = chartTitle;
        this.xLabel = xLabel;
        this.yLabel = yLabel;

        this.xLowerBound = lowerBound;
        this.xUpperBound = upperBound;

        this.chartData = chartData;

    }

    public LineChartWindow(String windowTitle, String chartTitle, String xLabel, String yLabel, double xLowerBound, double xUpperBound, double yLowerBound, double yUpperBound) {

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

        TableColumn<BucklingDataPoint, String> xCol = new TableColumn<>("Length");
        xCol.setCellValueFactory(new PropertyValueFactory<>("physicalLength"));

        TableColumn<BucklingDataPoint, String> yCol = new TableColumn<>("Load Factor");
        yCol.setCellValueFactory(new PropertyValueFactory<>("minLoadFactor"));

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

                if (event.getButton() == MouseButton.SECONDARY) {

                    MenuItem local = new MenuItem("Set as Local buckling value");
                    MenuItem distortional = new MenuItem("Set as Distortional buckling value");
                    MenuItem global = new MenuItem("Set as Global buckling value");
                    MenuItem cancel = new MenuItem("Cancel...");
                    
                    
                    BucklingCurve bc= seriesTable.getSelectionModel().getSelectedItem();

                    System.out.println(seriesTable.getSelectionModel().getSelectedItem().toString());
                    System.out.println(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor());

                    local.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {

                            seriesTable.getSelectionModel().getSelectedItem().setLocalFactor(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor());

                            bc.localImage = DrawingHandler.createBucklingCurveSnapShot(chart, mvp.box2D, "Local buckling factor = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor(), 2), "Physical length = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength(), 2));
                           
                        }
                    });

                    distortional.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            seriesTable.getSelectionModel().getSelectedItem().setDistortionalFactor(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor());
                            bc.distortionalImage = DrawingHandler.createBucklingCurveSnapShot(chart, mvp.box2D, "Distortional buckling factor = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor(), 2), "Physical length = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength(), 2));
                            
                        }
                    });

                    global.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                            seriesTable.getSelectionModel().getSelectedItem().setGlobalFactor(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor());
                            bc.globalImage = DrawingHandler.createBucklingCurveSnapShot(chart, mvp.box2D, "Global buckling factor = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor(), 2), "Physical length = " + MyMath.round(seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength(), 2));
                            
                        }
                    });

                    ContextMenu c = new ContextMenu(local, distortional, global, cancel);
                    c.show(seriesDataTable, Side.LEFT, event.getX(), event.getY());

                }

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

// Set the data for the chart
//ObservableList<XYChart.Series<Number,Number>> chartData = XYChartDataUtil.getDataList();
        chart.setData(chartData);
        chart.setAnimated(false);
        chart.getStylesheets().add("Style.css");
        chart.getStyleClass().addAll("chart");

        yAxis.getStylesheets().add("Style.css");
        yAxis.getStyleClass().addAll("axis");

        xAxis.getStylesheets().add("Style.css");
        xAxis.getStyleClass().addAll("axis");

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
        stage.setMaximized(true);

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
        chart.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                WritableImage img = chart.snapshot(new SnapshotParameters(), null);

                FileChooser fileDialog = new FileChooser();
                fileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
                File file = fileDialog.showSaveDialog(null);

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
                } catch (Exception s) {
                }
            }
        });
    }

    public void doUpdateDataTable() {
        seriesDataTable.getItems().clear();

        for (BucklingDataPoint dataPoint : seriesTable.getSelectionModel().getSelectedItem().getBucklingDataPoints()) {
            seriesDataTable.getItems().add(dataPoint);
        }

    }

    public void doSetCrossHair() {
        XYChartDataUtil.setCrossHair(seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength(), seriesDataTable.getSelectionModel().getSelectedItem().getMinLoadFactor());

        String bcName = seriesTable.getSelectionModel().getSelectedItem().getName();

        BucklingCurve bc = Defaults.getBucklinCurve(bcName);

        if (bc != (null)) {

            if (seriesDataTable.getSelectionModel().getSelectedItem() != null) {

                double tableSelection = (Double) seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength();
                Model m = bc.getModel(tableSelection);

                if (m != null) {

                    m.setDisplacedState(bc.getPoint((Double) seriesDataTable.getSelectionModel().getSelectedItem().getPhysicalLength()));
                    mvp.setModel(m);
                    mvp.draw();
                }

            }
        }

    }

    public void addNameColumn(TableView<BucklingCurve> table) {
// Name column is non-editable
        table.getColumns().add(XYChartDataUtil.getCurveNameColumn());
    }

}
