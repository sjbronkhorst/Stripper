/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stripper.BucklingCurve;

/**
 *
 * @author SJ
 */
public class DSMWindow extends Application {

    private final ChoiceBox<BucklingCurve> curveChoice = new ChoiceBox<>(XYChartDataUtil.getCurveList());
    Label localLabel = new Label("");
    Label globalLabel = new Label("");
    Label distortionalLabel = new Label("");

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox root = new VBox(curveChoice, localLabel, distortionalLabel, globalLabel);

        curveChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

               
                 if (curveChoice.getSelectionModel().selectedIndexProperty().get() >= 0) {
               BucklingCurve bc = curveChoice.getItems().get((int) newValue);
                localLabel.setText("Local " + bc.getLocalFactor());
                distortionalLabel.setText("Distortional " + bc.getDistortionalFactor());
                globalLabel.setText("Global " + bc.getGlobalFactor());
               }
            }
        });

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("DSM Data");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
