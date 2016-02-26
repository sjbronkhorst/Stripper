package UI;

import com.sun.javafx.css.Style;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import stripper.Node;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import stripper.BucklingCurve;
import stripper.BucklingDataPoint;
import stripper.BucklingEquation;
import stripper.EigenValueFixer;
import stripper.FileHandler;
import stripper.Path;

import stripper.Strip;

public class TableViewEdit extends Application {

    ModelViewPane mvp = new ModelViewPane(Defaults.getBaseModel());

    Boolean disCalced = false;

    LoadPane lp;

    TextField modelLengthField = new TextField();

    TextField thicknessField = new TextField();

    private double biggestX = 0;
    private double biggestY = 0;

    Button calcBtn = new Button("Calculate Stresses");
    Button plotBtn = new Button("Plot");
    Button buckleBtn = new Button("Calculate Buckling data");
    Label buckleLable = new Label("Buckling curve steps :");
    TextField bucklePoints = new TextField("100");
    Button setLengthBtn = new Button("set");
    Button setThicknessBtn = new Button("set");
    Button modelPropertiesBtn = new Button("Model properties");

    ProgressBar progInd = new ProgressBar(0);

    SystemEquation s;

    HomeMenuBar menuBar;

    TabPane tabPane = new TabPane();
    Tab geometryTab = new Tab("Geometry");
    Tab loadTab = new Tab("Loads");

    private static final TextArea textArea = new TextArea();

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        AnalysisChoicePrompt acp = new AnalysisChoicePrompt(stage);

        println("Buckling analysis mode : " + Boolean.toString(acp.getResult()));
        Defaults.bucklingAnalysis = acp.getResult();
        lp = new LoadPane(this, Defaults.bucklingAnalysis);
        menuBar = new HomeMenuBar(this, !Defaults.bucklingAnalysis);

        progInd.setStyle("-fx-progress-color: blue;");

        //progInd.setMinSize(50, 50);
        //String filePath = "C:/Users/SJ/Desktop/file.txt";
        //String filePath = "C:/Users/SJ/Desktop/icon.png";
        Image ic = new Image("file:///C:/Users/SJ/Desktop/icon.png");

        stage.getIcons().add(ic);

        TableView<Node> table = new TableView<>(NodeTableUtil.getNodeList());
// Make the TableView editable
        table.setEditable(true);
        // Add columns with appropriate editing features
        addIdColumn(table);
        addXCoordColumn(table);
        addZCoordColumn(table);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //TableView<UIStrip> stripTable = new TableView<>(UIStripTableUtil.getStripList());
        TableView<Strip> stripTable = new TableView<>(Defaults.getBaseModel().getStripList());
        
       
        
        stripTable.setEditable(true);
        addStripIdColumn(stripTable);
        addNode1Column(stripTable);
        addNode2Column(stripTable);
        addThicknessColumn(stripTable);

        stripTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button addBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        Button nodeDofBtn = new Button("DOF");

        Button stripAddBtn = new Button("Add");
        Button stripRemoveBtn = new Button("Remove");
        Button stripPropertyBtn = new Button("Properties");

       

        

        setLengthBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Defaults.getBaseModel().setModelLength(Double.parseDouble(modelLengthField.textProperty().get()));
                Defaults.getBaseModel().getFourierSeries().setLength(Defaults.getBaseModel().getModelLength());

                draw();

                TableViewEdit.println("Model length has been set to " + Double.toString(Defaults.getBaseModel().getModelLength()));

            }
        });

        modelPropertiesBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                println("x Bar = " + Defaults.getBaseModel().getXBar());
                println("z Bar = " + Defaults.getBaseModel().getZBar());
            }
        });

        setThicknessBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

//                for (UIStrip s : UIStripTableUtil.getStripList()) {
//                    s.setStripThickness(Double.parseDouble(thicknessField.textProperty().get()));
//                }
                
                for (Strip s : Defaults.getBaseModel().getStripList()) {
                    s.setStripThickness(Double.parseDouble(thicknessField.textProperty().get()));
                }

                draw();
                TableViewEdit.println("Thickness has been set");
            }
        });

        plotBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (disCalced) {

                    XYChartDataUtil.addSeries(s.getxData(), s.getyData());

                    LineChartWindow chart = new LineChartWindow("Displacement along member", "Displacement along member", "Distance along member", "Displacement", 0, Double.parseDouble(modelLengthField.textProperty().get()), XYChartDataUtil.getDataList());

                    Stage s = new Stage();

                    s.getIcons().add(ic);
                    chart.start(s);
                    
                } else {
                    TableViewEdit.println("Nothing to plot");
                }

            }
        });

        calcBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                
                
                //Defaults.getBaseModel().getNodeList().clear();
//                Node.clearNumbering();
//                for (Node n : NodeTableUtil.getNodeList()) {
//                    Defaults.getBaseModel().addNode(n);
//                }
                
                
                //Defaults.getBaseModel().getStripList().clear();
//                for (UIStrip s : StripTableUtil.getStripList()) {
//
//                    Defaults.getBaseModel().addStrip(s);
//                }

                

                s = new SystemEquation(Defaults.getBaseModel());

                // Task<Void> task = new Task<Void>() {
                //    @Override
                //    protected Void call() throws Exception {
                progInd.progressProperty().bind(s.progressProperty());

                s.computeParameterVector();

                //s.setDisplacedState((int) slider.getValue());
                //TableViewEdit.println(Integer.toString((int) slider.getValue()));
                disCalced = true;

                draw();

                // for (Strip s : ModelProperties.getStripList()) {
                //TableViewEdit.println(s.getPlaneStressVectorAt(s.getStripWidth() / 2.0, 50).get(1));
//                    TableViewEdit.println(s.getPlaneStressVectorAt(s.getStripWidth(), 50).get(1));
                //   for (int y = 0; y < 101; y++) {
                //      TableViewEdit.println(Double.toString(s.getBendingStressVectorAt(s.getStripWidth() / 2.0, y).get(1)));
                //   }
                //   }
//                        
//                        
                //    return null;
                // }
                //  };
                // Thread t = new Thread(task);
                //  t.start();
            }
        });

        buckleBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //  ModelProperties.setModelLength(Double.parseDouble(modelLengthField.textProperty().get()));
                boolean noEdgeLoads = false;

                //Defaults.getBaseModel().getNodeList().clear();
//                Node.clearNumbering();
//                for (Node n : NodeTableUtil.getNodeList()) {
//                    Defaults.getBaseModel().addNode(n);
//                }
                
                
               // Defaults.getBaseModel().getStripList().clear();
//                for (UIStrip s : StripTableUtil.getStripList()) {
//
//                    Defaults.getBaseModel().addStrip(s);
//                }

                if (noEdgeLoads) {
                    println("ERROR : Some strips have no edge loads specified ! ");
                } else {

                    int steps = Integer.parseInt(bucklePoints.getText());
                    BucklingEquation[] b = new BucklingEquation[steps];
                    Model[] models = new Model[steps];

                    for (int i = 0; i < steps; i++) {

                        models[i] = new Model(Defaults.getBaseModel());

                        
                        
                        Node.clearNumbering();
                        for (Node n : NodeTableUtil.getNodeList()) {
                            models[i].addNode(n);
                        }
                        
                        
//                        for (UIStrip s : UIStripTableUtil.getStripList()) {
//
//                            models[i].addStrip(s);
//
//                        }
                        
                        Strip.clearNumbering();
                         for (Strip s : Defaults.getBaseModel().getStripList()) {

                            models[i].addStrip(s);

                        }

                        

                        models[i].setModelLength(((double) (i + 1) / steps) * Defaults.getBaseModel().getModelLength());

                        b[i] = new BucklingEquation(models[i]);

                       models[i].setBucklePoint(b[i].getBucklingData());

                    }
                 
            
                    String[][] stringData = new String[models.length + 1][Defaults.getBaseModel().getFourierTerms() + 2];
                    

                    stringData[0][0] = "Length";
                    stringData[0][stringData[0].length - 1] = "Minimum stress (Signature curve)";

                    for (int i = 1; i < stringData[0].length - 1; i++) {
                        stringData[0][i] = "Buckling stress for half wave";
                    }
                    
                    BucklingCurve bc = new BucklingCurve();
                   

                    for (int i = 0; i < models.length; i++) {

                        for (int j = 0; j < Defaults.getBaseModel().getFourierTerms(); j++) {

                            stringData[i + 1][j + 1] = Double.toString(models[i].getBucklePoint().getSystemLoadFactor(j));

                        }

                        stringData[i + 1][0] = Double.toString(models[i].getBucklePoint().getPhysicalLength());
                        stringData[i + 1][Defaults.getBaseModel().getFourierTerms() + 1] = Double.toString(models[i].getBucklePoint().getMinLoadFactor());
                        
                        bc.addModel(models[i]);

                    }

                    FileHandler f = new FileHandler();
                    try {
                        f.writeCSV(stringData);
                    } catch (IOException ex) {
                        Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    XYChartDataUtil.addSeries(bc.getPhysicalLengths(), bc.getLoadFactors());
                    LineChartWindow chart = new LineChartWindow("Minimum buckling stress vs physical length", "", "Length", "Stress", 0, Defaults.getBaseModel().getModelLength(), 0, (bc.getLoadFactors()[0]), XYChartDataUtil.getDataList());

                    Defaults.bucklingCurveList.add(bc);

                   
                    Stage s = new Stage();

                    s.getIcons().add(ic);
                    chart.start(s);
                  
                    
                    disCalced = true;

                }
            }
        });

        stripPropertyBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Strip s = stripTable.getSelectionModel().getSelectedItem();

                TableViewEdit.println("Strip " + s.getStripId() + " properties :");
                TableViewEdit.println("width = " + s.getStripWidth());
                TableViewEdit.println("thickness = " + s.getStripThickness());

            }
        });

        stripRemoveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Strip s = stripTable.getSelectionModel().getSelectedItem();

                if (s != null) {
                    Defaults.getBaseModel().removeStrip(s);
                    TableViewEdit.println("Strip " + s.getStripId() + " removed.");
                } else {
                    TableViewEdit.println("ERROR : No nodes selected !");
                }
                draw();

            }
        });

        stripAddBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

               Strip s = Defaults.getBaseModel().getFourierSeries().getStrip(Defaults.getBaseModel());
                //////////////////////////////////////////////////////////
                //UIStripTableUtil.addStrip(s);
                               
                Defaults.getBaseModel().addStrip(s);
                
                draw();
                TableViewEdit.println("Strip " + s.getStripId() + " added.");

            }
        });

        addBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Node n = new Node(0, 0, Defaults.getBaseModel());
                Defaults.getBaseModel().addNode(n);
                NodeTableUtil.addNode(n);
                draw();
                TableViewEdit.println("Node " + n.getNodeId() + " added.");

            }
        });

        removeBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Node n = table.getSelectionModel().getSelectedItem();

                if (n != null) {
                    NodeTableUtil.removeNode(n);
                    TableViewEdit.println("Node " + n.getNodeId() + " removed.");
                } else {
                    TableViewEdit.println("ERROR : No nodes selected !");
                }
                draw();

            }
        });

        nodeDofBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Node n = table.getSelectionModel().getSelectedItem();

                if (n != null) {
                    dofPicker d = new dofPicker();
                    d.setNode(n);
                    Stage s = new Stage();
                    try {
                        d.start(s);
                    } catch (Exception ex) {
                        Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    TableViewEdit.println("Editing " + n.toString() + " DOF");

                } else {
                    TableViewEdit.println("ERROR : No nodes selected !");
                }

            }
        });

        loadTab.setContent(lp.getPane());

        VBox tableBox = new VBox(10);
        HBox nodeBox = new HBox(10);
        HBox stripBox = new HBox(10);

        VBox nodeControlBox = new VBox(10);
        VBox stripControlBox = new VBox(10);

        VBox rightBox = new VBox(10);

        textArea.setMinHeight(100);
        bucklePoints.setMaxWidth(100);

        if (Defaults.bucklingAnalysis) {
            rightBox.getChildren().addAll(mvp.getPane(), buckleLable, bucklePoints, buckleBtn, textArea);
        } else {
            rightBox.getChildren().addAll(mvp.getPane(), calcBtn, progInd, textArea);
        }

        rightBox.setStyle("-fx-padding: 0;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 0;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: black;");

        //rightBox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        nodeBox.getChildren().addAll(table, nodeControlBox);
        addBtn.setMinWidth(130);
        removeBtn.setMinWidth(130);
        nodeDofBtn.setMinWidth(130);

        nodeControlBox.getChildren().addAll(addBtn, removeBtn, nodeDofBtn);

        stripControlBox.getChildren().addAll(stripAddBtn, stripRemoveBtn, stripPropertyBtn);

        stripAddBtn.setMinWidth(130);
        stripRemoveBtn.setMinWidth(130);

        stripBox.getChildren().addAll(stripTable, stripControlBox);

        Label nodeLabel = new Label("Nodes :");
        Label stripLabel = new Label("Strips :");
        Label lengthLabel = new Label("Model length :");
        Label thicknessLabel = new Label("Plate thickness :");

        tableBox.getChildren().addAll(nodeLabel, nodeBox, stripLabel, stripBox, lengthLabel, modelLengthField, setLengthBtn, thicknessLabel, thicknessField, setThicknessBtn, modelPropertiesBtn);

        geometryTab.setContent(tableBox);

        tabPane.getTabs().addAll(geometryTab, loadTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        SplitPane sp = new SplitPane();
        sp.setDividerPosition(0, 0.3);

        sp.getItems().addAll(tabPane, rightBox);

        BorderPane root = new BorderPane(sp);
        tableBox.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 0;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: blue;");

        root.getStylesheets().add("Style.css");

        root.setTop(menuBar.getMenuBar());

        tabPane.prefHeightProperty().bind(root.heightProperty());

        root.setPrefSize(1200, 600);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Stripper - FSM by SJ Bronkhorst");
        stage.show();
        draw();
    }

    public void addIdColumn(TableView<Node> table) {
// Id column is non-editable
        table.getColumns().add(NodeTableUtil.getIDColumn());
    }

    public void addXCoordColumn(TableView<Node> table) {

        TableColumn<Node, Double> fNameCol = NodeTableUtil.getXCoordColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Node, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();
            Node node = e.getRowValue();

            TableViewEdit.println("X-Coordinate changed for Node "
                    + node.getNodeId() + " at row " + (row + 1) + " to " + e.getNewValue());

            NodeTableUtil.getNodeList().get(row).setXCoord(e.getNewValue());
            draw();
        });

        table.getColumns().add(fNameCol);
    }

    public void addZCoordColumn(TableView<Node> table) {

        TableColumn<Node, Double> fNameCol = NodeTableUtil.getZCoordColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Node, Double>forTableColumn(converter));

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();
            Node node = e.getRowValue();

            TableViewEdit.println("Z-Coordinate changed for Node "
                    + node.getNodeId() + " at row " + (row + 1) + " to " + e.getNewValue());

            NodeTableUtil.getNodeList().get(row).setZCoord(e.getNewValue());
            draw();
        });

        table.getColumns().add(fNameCol);
    }

    public void addStripIdColumn(TableView<Strip> table) {
// Id column is non-editable
        table.getColumns().add(StripTableUtil.getIDColumn());
    }

    public void addNode1Column(TableView<Strip> table) {

        TableColumn<Strip, Integer> fNameCol = StripTableUtil.getNode1Column();

        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Integer>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {

            try {
                int row = e.getTablePosition().getRow();

                Strip strip = e.getRowValue();

                TableViewEdit.println("First node changed for Strip "
                        + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

                //UIStripTableUtil.getStripList().get(row).setNode1(NodeTableUtil.getNode(e.getNewValue()));
                
                Defaults.getBaseModel().getStripList().get(row).setNode1(NodeTableUtil.getNode(e.getNewValue()));
                TableViewEdit.println("New node id " + NodeTableUtil.getNode(e.getNewValue()).getNodeId());

                draw();
            } catch (Exception ex) {
                TableViewEdit.println("Node not found, create it and try again");
            }

        });

        table.getColumns().add(fNameCol);
    }

    public void addNode2Column(TableView<Strip> table) {

        TableColumn<Strip, Integer> fNameCol = StripTableUtil.getNode2Column();

        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Integer>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            try {
                int row = e.getTablePosition().getRow();
                Strip strip = e.getRowValue();

                TableViewEdit.println("Second node changed for Strip "
                        + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

                //UIStripTableUtil.getStripList().get(row).setNode2(NodeTableUtil.getNode(e.getNewValue()));
                
                Defaults.getBaseModel().getStripList().get(row).setNode2(NodeTableUtil.getNode(e.getNewValue()));
                TableViewEdit.println("New node id " + NodeTableUtil.getNode(e.getNewValue()).getNodeId());

                draw();
            } catch (Exception ex) {
                TableViewEdit.println("Node not found, create it and try again");
            }

        });

        table.getColumns().add(fNameCol);
    }

    public void addThicknessColumn(TableView<Strip> table) {

        TableColumn<Strip, Double> fNameCol = StripTableUtil.getStripThicknessColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Double>forTableColumn(converter));

        table.getColumns().add(fNameCol);

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            try {
                int row = e.getTablePosition().getRow();
                Strip strip = e.getRowValue();

                TableViewEdit.println("Thickness changed for Strip "
                        + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

                //UIStripTableUtil.getStripList().get(row).setStripThickness(e.getNewValue());
                Defaults.getBaseModel().getStripList().get(row).setStripThickness(e.getNewValue());
                

                draw();
            } catch (Exception ex) {
                TableViewEdit.println("Node not found, create it and try again");
            }

        });

    }

    public static void println(String s) {
        Platform.runLater(new Runnable() {//in case you call from other thread
            @Override
            public void run() {

                textArea.setText(textArea.getText() + s + "\n");

                textArea.selectPositionCaret(textArea.getLength() - 3);
                textArea.deselect(); //removes the highlighting

                System.out.println(s);//for echo if you want
            }
        });
    }

    public void draw() {
        mvp.draw();
    }

}
