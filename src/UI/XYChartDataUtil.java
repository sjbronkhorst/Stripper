package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

@SuppressWarnings("unchecked")
public class XYChartDataUtil {

    private static ObservableList<XYChart.Series<Number, Number>> data = FXCollections.<XYChart.Series<Number, Number>>observableArrayList();
    private static int seriesNum = 0;
    public static XYChart.Series<Number, Number> vCrossHair = new XYChart.Series<>();
    public static XYChart.Series<Number, Number> hCrossHair = new XYChart.Series<>();

    public static void addSeries(double[] xData, double[] yData) {

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Curve " + Integer.toString(seriesNum));

        seriesNum++;

        for (int i = 0; i < xData.length; i++) {
            series.getData().add(new XYChart.Data<>(xData[i], yData[i]));

        }

        data.add(series);
    }

    public static ObservableList<XYChart.Series<Number, Number>> getDataList() {
        return data;
    }

    public static void setCrossHair(Number x, Number y) {
        vCrossHair.setName("Vertical Crosshair");
        hCrossHair.setName("Horizontal Crosshair");

        vCrossHair.getData().clear();
        hCrossHair.getData().clear();

        vCrossHair.getData().add(new XYChart.Data<Number, Number>(x, y));
        vCrossHair.getData().add(new XYChart.Data<Number, Number>(x, 0));

        hCrossHair.getData().add(new XYChart.Data<Number, Number>(x, y));
        hCrossHair.getData().add(new XYChart.Data<Number, Number>(0, y));

        if (!data.contains(vCrossHair) == true) {
            data.add(vCrossHair);
        }

        if (!data.contains(hCrossHair) == true) {
            data.add(hCrossHair);
        }

    }

    public static TableColumn<XYChart.Series<Number, Number>, String> getSeriesNameColumn() {
        TableColumn<XYChart.Series<Number, Number>, String> idColumn = new TableColumn("Series");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        return idColumn;

    }

    public static TableColumn<Number, Number> getXColumn() {
        TableColumn<Number, Number> idColumn = new TableColumn("X");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("X"));

        return idColumn;

    }

}
