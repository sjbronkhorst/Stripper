/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stripper.BucklingCurve;
import stripper.DSM.DSMCalcs;

/**
 *
 * @author SJ
 */
public class DSMWindow extends Application {

    private final ChoiceBox<BucklingCurve> flexCurveChoice = new ChoiceBox<>(XYChartDataUtil.getCurveList());
    private final ChoiceBox<BucklingCurve> axialCurveChoice = new ChoiceBox<>(XYChartDataUtil.getCurveList());

    Label myLabel = new Label("My = fy * Z = STILL INCORRECT");
    Label mcrlLabel = new Label("Mcrl = ");
    Label mcrdLabel = new Label("Mcrd = ");
    Label mcreLabel = new Label("Mcre = ");
    Label cbLabel = new Label("Cb = ");
    Label phibLabel = new Label("φb = ");

    TextField myTf = new TextField("");
    TextField mcrlTf = new TextField("");
    TextField mcrdTf = new TextField("");
    TextField mcreTf = new TextField("");
    TextField cbTf = new TextField("1");
    TextField phibTf = new TextField("0.9");

    Label pyLabel = new Label("Py = fy * A = STILL INCORRECT");
    Label pcrlLabel = new Label("Pcrl = ");
    Label pcrdLabel = new Label("Pcrd = ");
    Label pcreLabel = new Label("Pcre = ");

    TextField pyTf = new TextField("");
    TextField pcrlTf = new TextField("");
    TextField pcrdTf = new TextField("");
    TextField pcreTf = new TextField("");

    TextArea calcArea = new TextArea();

    Button flexuralCalcBtn = new Button("Calculate");
    Button axialCalcBtn = new Button("Calculate");

    CheckBox flexBracedCheck = new CheckBox("Fully braced");

    CheckBox axialBracedCheck = new CheckBox("Fully braced");

    Tab beamTab = new Tab("Beam");
    Tab columnTab = new Tab("Column");
    TabPane tabPane = new TabPane();

    @Override
    public void start(Stage primaryStage) throws Exception {

        cbTf.setTooltip(new Tooltip("Bending coefficent dependent on moment gradient"));
        phibTf.setTooltip(new Tooltip("φb = 0.9 for pre-qualified sections (DSM Section\n"
                + "1.1.1.2) else, φb = 0.8 "));

        GridPane flexuralPane = new GridPane();

        flexuralPane.add(myLabel, 0, 0);
        flexuralPane.add(myTf, 1, 0);

        flexuralPane.add(mcrlLabel, 0, 1);
        flexuralPane.add(mcrlTf, 1, 1);

        flexuralPane.add(mcrdLabel, 0, 2);
        flexuralPane.add(mcrdTf, 1, 2);

        flexuralPane.add(mcreLabel, 0, 3);
        flexuralPane.add(mcreTf, 1, 3);

        flexuralPane.add(cbLabel, 0, 4);
        flexuralPane.add(cbTf, 1, 4);

        flexuralPane.add(phibLabel, 0, 5);
        flexuralPane.add(phibTf, 1, 5);

        flexuralPane.add(flexBracedCheck, 0, 6);
        flexuralPane.add(flexuralCalcBtn, 1, 6);

        GridPane axialPane = new GridPane();

        axialPane.add(pyLabel, 0, 0);
        axialPane.add(pyTf, 1, 0);

        axialPane.add(pcrlLabel, 0, 1);
        axialPane.add(pcrlTf, 1, 1);

        axialPane.add(pcrdLabel, 0, 2);
        axialPane.add(pcrdTf, 1, 2);

        axialPane.add(pcreLabel, 0, 3);
        axialPane.add(pcreTf, 1, 3);

        axialPane.add(axialBracedCheck, 0, 4);
        axialPane.add(axialCalcBtn, 1, 4);

        flexuralCalcBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                calcArea.clear();
                
                DSMCalcs d = new DSMCalcs();

                d.setMy(Double.parseDouble(myTf.getText()));
                d.setMcrl(Double.parseDouble(mcrlTf.getText()));
                d.setMcrd(Double.parseDouble(mcrdTf.getText()));
                d.setMcre(Double.parseDouble(mcreTf.getText()));
                d.setCb(Double.parseDouble(cbTf.getText()));
                d.setPhiB(Double.parseDouble(phibTf.getText()));
                
                //d.setTextArea(calcArea);
                d.getNominalFlexuralStrength(flexBracedCheck.isSelected());
            }
        });

        flexCurveChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                if (flexCurveChoice.getSelectionModel().selectedIndexProperty().get() >= 0) {
                    BucklingCurve bc = flexCurveChoice.getItems().get((int) newValue);

                    myTf.setText(" ");
                    mcrlTf.setText(" " + bc.getLocalFactor());
                    mcrdTf.setText(" " + bc.getDistortionalFactor());
                    mcreTf.setText(" " + bc.getGlobalFactor());
                }
            }
        });

        axialCurveChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                if (flexCurveChoice.getSelectionModel().selectedIndexProperty().get() >= 0) {
                    BucklingCurve bc = flexCurveChoice.getItems().get((int) newValue);

                    pyTf.setText(" ");
                    pcrlTf.setText(" " + bc.getLocalFactor());
                    pcrdTf.setText(" " + bc.getDistortionalFactor());
                    pcreTf.setText(" " + bc.getGlobalFactor());
                }
            }
        });

        myTf.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {

                BucklingCurve bc = flexCurveChoice.getValue();

                if (bc != null) {

                    if (myTf.getText().length() > 0) {
                        double My = 0;
                        try {
                            My = Double.parseDouble(myTf.getText());
                        } catch (Exception e) {

                        }

                        mcrlTf.setText(" " + My * bc.getLocalFactor());
                        mcrdTf.setText(" " + My * bc.getDistortionalFactor());
                        mcreTf.setText(" " + My * bc.getGlobalFactor());
                    }
                }
            }
        });

        VBox beamTopBox = new VBox(5);
        VBox columnTopBox = new VBox(5);

        beamTopBox.getChildren().addAll(flexCurveChoice, flexuralPane);
        columnTopBox.getChildren().addAll(axialCurveChoice, axialPane);

        flexuralPane.setMinHeight(100);
        axialPane.setMinHeight(100);

        beamTab.setContent(beamTopBox);
        columnTab.setContent(columnTopBox);

        tabPane.getTabs().addAll(beamTab, columnTab);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        BorderPane root = new BorderPane(calcArea, tabPane, null, null, null);

        Scene scene = new Scene(root, 300, 250);
        calcArea.maxHeight(2000);

        primaryStage.setTitle("DSM Calc Sheet");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
