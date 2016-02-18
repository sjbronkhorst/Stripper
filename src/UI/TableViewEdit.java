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
import stripper.FileHandler;
import stripper.Path;

import stripper.Strip;

public class TableViewEdit extends Application {

    ModelViewPane mvp = new ModelViewPane(Defaults.getBaseModel());

    Boolean disCalced = false;

    LoadPane lp ;

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

    HomeMenuBar menuBar ;

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

        TableView<UIStrip> stripTable = new TableView<>(StripTableUtil.getStripList());
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

        Slider slider = new Slider(0, 100, 50);

        slider.setMajorTickUnit(10);
        slider.setMinorTickCount(1);
        slider.showTickMarksProperty().set(true);
        slider.snapToTicksProperty().set(true);
        slider.showTickLabelsProperty().set(true);

        slider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

//                if (slider.isFocused()) {
//
//                    if(disCalced && (Defaults.getBaseModel().bucklingCurve.dataPoints.get(slider.getValue()) != null))
//                    {
//                        
//                        ModelProperties.setDisplacedState(ModelProperties.bucklingCurve.dataPoints.get(slider.getValue()));
//                        
//                        
//                        
//                    }
//                   
//                    
//                    
//                       
//                    }
//                    
//                    draw();
               
            }
        });

        setLengthBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Defaults.getBaseModel().setModelLength(Double.parseDouble(modelLengthField.textProperty().get()));
                Defaults.getBaseModel().getFourierSeries().setLength(Defaults.getBaseModel().getModelLength());

                draw();

                TableViewEdit.println("Model length has been set to " + Double.toString(Defaults.getBaseModel().getModelLength()));
                
            }
        });
        
        modelPropertiesBtn.setOnAction(new EventHandler<ActionEvent>() 
        {

           @Override
           public void handle(ActionEvent event) {
               
               println("x Bar = " + Defaults.getBaseModel().getXBar());
               println("z Bar = " + Defaults.getBaseModel().getZBar());
           }
       });

        setThicknessBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                for (UIStrip s : StripTableUtil.getStripList()) {
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

                    XYChartDataUtil.addSeries(s.getxData(), s.getyData(), "Displacement");

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

                Defaults.getBaseModel().getStripList().clear();

                for (UIStrip s : StripTableUtil.getStripList()) {

                    Defaults.getBaseModel().addStrip(s);
                }
                
                Defaults.getBaseModel().getNodeList().clear();
                for(Node n : NodeTableUtil.getNodeList())
                {
                    Defaults.getBaseModel().getNodeList().add(n);
                }

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
                
                Defaults.getBaseModel().getStripList().clear();
                for (UIStrip s : StripTableUtil.getStripList()) {
                    Defaults.getBaseModel().addStrip(s);
                    
                    if(s.getF1() == 0.0 && s.getF2() == 0.0 )
                    {
                        noEdgeLoads = true;
                    }
                    
                }
                
                Defaults.getBaseModel().getNodeList().clear();
                for(Node n : NodeTableUtil.getNodeList())
                {
                    Defaults.getBaseModel().getNodeList().add(n);
                }
                
                
                
                
                if(noEdgeLoads)
                {
                println("ERROR : Some strips have no edge loads specified ! ");
                }
                else
                {
                   

                BucklingEquation b = new BucklingEquation(Defaults.getBaseModel());

                
                
                
                
                
                BucklingDataPoint[] buckleData = new  BucklingDataPoint[1];
                        
                      buckleData[0] =  b.getBucklingData();
                
                
                
                String[][] stringData = new String[buckleData.length + 1][Defaults.getBaseModel().getFourierTerms()+2];
                BucklingCurve bc = new BucklingCurve();

                stringData[0][0] = "Length";
                stringData[0][stringData[0].length - 1] = "Minimum stress (Signature curve)";

               
                for (int i = 1; i < stringData[0].length-1; i++) {
                    stringData[0][i] = "Buckling stress for half wave";
                }

                for (int i = 0; i < buckleData.length; i++) {
                    
                    
                    
                    for (int j = 0; j < Defaults.getBaseModel().getFourierTerms(); j++) {
                                               
                        stringData[i + 1][j+1] = Double.toString(buckleData[i].getSystemLoadFactor(j));

                    }
                    
                   
                    stringData[i + 1][0] = Double.toString(buckleData[i].getPhysicalLength());
                    stringData[i + 1][Defaults.getBaseModel().getFourierTerms()+1] = Double.toString(buckleData[i].getMinLoadFactor());
                    bc.addDataPoint(buckleData[i]);
                    

                }

                FileHandler f = new FileHandler();
                try {
                    f.writeCSV(stringData);
                } catch (IOException ex) {
                    Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                }

                XYChartDataUtil.addSeries(bc.getPhysicalLengths(), bc.getLoadFactors(), "Signature curve");
                LineChartWindow chart = new LineChartWindow("Minimum buckling stress vs physical length", "", "Length", "Stress", 0, Defaults.getBaseModel().getModelLength(), 0, (bc.getLoadFactors()[0]), XYChartDataUtil.getDataList());

                
                Defaults.getBaseModel().bucklingCurve = bc;  // WRONG a model only has one buckling point bacause it can only have one physical length
                
                
                slider.setMin(Defaults.getBaseModel().getModelLength()/Double.parseDouble(bucklePoints.getText()));
                slider.setMax(Defaults.getBaseModel().getModelLength());
                slider.setValue(Defaults.getBaseModel().getModelLength()/Double.parseDouble(bucklePoints.getText()));
               
                   
                
                Stage s = new Stage();

                s.getIcons().add(ic);
                chart.start(s);
                slider.setMinorTickCount(0);
                slider.setMajorTickUnit(Defaults.getBaseModel().getModelLength()/Double.parseDouble(bucklePoints.getText()));
                disCalced = true;
                
                
                
                }
            }
        });

        stripPropertyBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                UIStrip s = stripTable.getSelectionModel().getSelectedItem();

                TableViewEdit.println("Strip " + s.getStripId() + " properties :");
                TableViewEdit.println("width = " + s.getStripWidth());
                TableViewEdit.println("thickness = " + s.getStripThickness());

            }
        });

        stripRemoveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                UIStrip s = stripTable.getSelectionModel().getSelectedItem();

                if (s != null) {
                    StripTableUtil.removeStrip(s);
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

                UIStrip s = new UIStrip();
                //////////////////////////////////////////////////////////
                StripTableUtil.addStrip(s);
                draw();
                TableViewEdit.println("Strip " + s.getStripId() + " added.");

            }
        });

        addBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Node n = new Node(0, 0, Defaults.getBaseModel());
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
        
        if(Defaults.bucklingAnalysis)
        {
            rightBox.getChildren().addAll(mvp.getPane(), buckleLable, bucklePoints, buckleBtn, textArea,slider);
        }
        else
        {
            rightBox.getChildren().addAll(mvp.getPane(), calcBtn, progInd, textArea/*, slider*/);
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

    public void addStripIdColumn(TableView<UIStrip> table) {
// Id column is non-editable
        table.getColumns().add(StripTableUtil.getIDColumn());
    }

    public void addNode1Column(TableView<UIStrip> table) {

        TableColumn<UIStrip, Integer> fNameCol = StripTableUtil.getNode1Column();

        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<UIStrip, Integer>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {

            try {
                int row = e.getTablePosition().getRow();

                UIStrip strip = e.getRowValue();

                TableViewEdit.println("First node changed for Strip "
                        + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

                StripTableUtil.getStripList().get(row).setNode1(NodeTableUtil.getNodeMap().get(e.getNewValue()));
                TableViewEdit.println("New node id " + NodeTableUtil.getNodeMap().get(e.getNewValue()).getNodeId());

                draw();
            } catch (Exception ex) {
                TableViewEdit.println("Node not found, create it and try again");
            }

        });

        table.getColumns().add(fNameCol);
    }

    public void addNode2Column(TableView<UIStrip> table) {

        TableColumn<UIStrip, Integer> fNameCol = StripTableUtil.getNode2Column();

        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<UIStrip, Integer>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            try {
                int row = e.getTablePosition().getRow();
                UIStrip strip = e.getRowValue();

                TableViewEdit.println("Second node changed for Strip "
                        + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

                StripTableUtil.getStripList().get(row).setNode2(NodeTableUtil.getNodeMap().get(e.getNewValue()));

                draw();
            } catch (Exception ex) {
                TableViewEdit.println("Node not found, create it and try again");
            }

        });

        table.getColumns().add(fNameCol);
    }

    public void addThicknessColumn(TableView<UIStrip> table) {

        TableColumn<UIStrip, Double> fNameCol = StripTableUtil.getStripThicknessColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<UIStrip, Double>forTableColumn(converter));

        table.getColumns().add(fNameCol);

        fNameCol.setOnEditStart(e -> {
            TableViewEdit.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            try {
                int row = e.getTablePosition().getRow();
                UIStrip strip = e.getRowValue();

                TableViewEdit.println("Thickness changed for Strip "
                        + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

                StripTableUtil.getStripList().get(row).setStripThickness(e.getNewValue());

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
