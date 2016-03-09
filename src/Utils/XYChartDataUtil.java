package Utils;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import fsm.BucklingCurve;
import fsm.BucklingDataPoint;


@SuppressWarnings("unchecked")
public class XYChartDataUtil {

    private static ObservableList<XYChart.Series<Number, Number>> seriesList = FXCollections.<XYChart.Series<Number, Number>>observableArrayList();
    private static ObservableList<BucklingCurve> curves = FXCollections.<BucklingCurve>observableArrayList();

    
    private static int seriesNum = 0;
    public static XYChart.Series<Number, Number> vCrossHair = new XYChart.Series<>();
    public static XYChart.Series<Number, Number> hCrossHair = new XYChart.Series<>();

//    public static void addSeries(double[] xData, double[] yData) {
//
//        XYChart.Series<Number, Number> series = new XYChart.Series<>();
//        series.setName("Curve " + Integer.toString(seriesNum));
//
//        seriesNum++;
//
//        for (int i = 0; i < xData.length; i++) {
//            series.getData().add(new XYChart.Data<>(xData[i], yData[i]));
//
//        }
//
//       
//        data.add(series);
//    }

    public static void addCurve(BucklingCurve b) {

        curves.add(b);
        
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Curve " + Integer.toString(seriesNum));

        seriesNum++;

        for (BucklingDataPoint p : b.getBucklingDataPoints())
        {
            series.getData().add(new XYChart.Data<>(p.getPhysicalLength(), p.getMinLoadFactor()));

        }

        seriesList.add(series);
     

    }

//    public static ObservableList<XYChart.Series<Number, Number>> getDataList() {
//        return data;
//    }
    
   public static ObservableList<BucklingCurve> getCurveList() {
        return curves;
    }
   
   public static ObservableList<XYChart.Series<Number, Number>> getSeriesList()
   {
       return seriesList;
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

        if (!seriesList.contains(vCrossHair) == true) {
            seriesList.add(vCrossHair);
        }

        if (!seriesList.contains(hCrossHair) == true) {
            seriesList.add(hCrossHair);
        }

    }

    public static TableColumn<BucklingCurve, String> getCurveNameColumn() {
        TableColumn<BucklingCurve, String> idColumn = new TableColumn("Curve name");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        return idColumn;

    }

//    public static TableColumn<Number, Number> getXColumn() {
//        TableColumn<Number, Number> idColumn = new TableColumn("X");
//
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("physicalLength"));
//
//        return idColumn;
//
//    }

}
