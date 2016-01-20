/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stripper.FileHandler;
import stripper.Path;
import stripper.Strip;

/**
 *
 * @author SJ
 */
public class PathMaker extends Application {

    private final ChoiceBox<Strip> startStripChoice = new ChoiceBox<Strip>(ModelProperties.getStripList());
    private final ChoiceBox<Strip> endStripChoice = new ChoiceBox<Strip>(ModelProperties.getStripList());

    private Label startXLbl = new Label("Start X :");
    private Label startYLbl = new Label("Start Y :");

    private Label endXLbl = new Label("End X :");
    private Label endYLbl = new Label("End Y :");
    private Label numOfDataPointsLbl = new Label("Amount of points :");

    private TextField startXF = new TextField();
    private TextField startYF = new TextField();
    private TextField endXF = new TextField();
    private TextField endYF = new TextField();
    private TextField numPointsF = new TextField();

    private Button saveBtn = new Button("OUTPUT DATA");

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox root = new VBox(10);

        HBox[] hBoxes = new HBox[6];

        for (int i = 0; i < 6; i++) {
            hBoxes[i] = new HBox();
        }

        hBoxes[0].getChildren().addAll(startXLbl, startXF);
        hBoxes[1].getChildren().addAll(startYLbl, startYF);
        hBoxes[2].getChildren().addAll(endXLbl, endXF);
        hBoxes[3].getChildren().addAll(endYLbl, endYF);
        hBoxes[4].getChildren().addAll(numOfDataPointsLbl, numPointsF);
        hBoxes[5].getChildren().addAll(startStripChoice, endStripChoice, saveBtn);

        root.getChildren().addAll(hBoxes);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Query values along a path");
        primaryStage.setScene(scene);
        primaryStage.show();

        saveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                int start = startStripChoice.getValue().getStripId();
                int end = endStripChoice.getValue().getStripId();
                int increment = 0;
                
                if (end - start != 0) {
                    increment = (end - start) / Math.abs(end - start); // will be +1 or -1
                }
                
                int count = 0;

                Path pTemp = new Path(new Point2D(Double.parseDouble(startXF.getText()), Double.parseDouble(startYF.getText())), new Point2D(Double.parseDouble(endXF.getText()), Double.parseDouble(endYF.getText())), Integer.parseInt(numPointsF.getText()), startStripChoice.getValue());

                String[][][] fileData = new String[Math.abs(end - start) + 1][pTemp.getNumOfDataPoints() + 2][8];

                do {
                    Path p = new Path(new Point2D(Double.parseDouble(startXF.getText()), Double.parseDouble(startYF.getText())), new Point2D(Double.parseDouble(endXF.getText()), Double.parseDouble(endYF.getText())), Integer.parseInt(numPointsF.getText()), startStripChoice.getValue());

                    String[][] data = new String[p.getNumOfDataPoints() + 2][8];

                    data[0][0] = startStripChoice.getValue().toString();

                    for (int i = 1; i < 8; i++) {
                        data[0][i] = " ";
                    }

                    data[1][0] = "X";
                    data[1][1] = "Y";
                    data[1][2] = "Mx";
                    data[1][3] = "My";
                    data[1][4] = "Mxy";
                    data[1][5] = "SigmaX (in-plane)";
                    data[1][6] = "SigmaY (in-plane)";
                    data[1][7] = "SigmaXY (in-plane)";

                    for (int i = 2; i < data.length; i++) {
                        data[i][0] = Double.toString(p.getxData()[i - 2]);
                        data[i][1] = Double.toString(p.getyData()[i - 2]);
                        data[i][2] = Double.toString(p.getQueryData()[i - 2][0].get(0));
                        data[i][3] = Double.toString(p.getQueryData()[i - 2][0].get(1));
                        data[i][4] = Double.toString(p.getQueryData()[i - 2][0].get(2));
                        data[i][5] = Double.toString(p.getQueryData()[i - 2][1].get(0));
                        data[i][6] = Double.toString(p.getQueryData()[i - 2][1].get(1));
                        data[i][7] = Double.toString(p.getQueryData()[i - 2][1].get(2));
                    }

                    fileData[count] = data;
                    count++;

                    start = start + increment;

                    startStripChoice.getSelectionModel().select(startStripChoice.getSelectionModel().getSelectedIndex() + increment);
                    

                } while (start != (end + increment));

                FileHandler fh = new FileHandler();
                try {
                    fh.writeCSV(fileData);
                } catch (IOException ex) {
                    System.out.println("File not found/available");
                }

            }
        });

    }

}
