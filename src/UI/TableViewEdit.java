package UI;

import com.sun.javafx.css.Style;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import stripper.Node;
import javafx.application.Application;
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
import stripper.BucklingEquation;
import stripper.FileHandler;
import stripper.Path;

import stripper.Strip;
import stripper.series.BucklingSeries_CC;

public class TableViewEdit extends Application {

    ModelViewPane mvp = new ModelViewPane();

    Boolean disCalced = false;

    LoadPane lp = new LoadPane(this);

    TextField modelLengthField = new TextField();

    TextField thicknessField = new TextField();

    private double biggestX = 0;
    private double biggestY = 0;

    Button calcBtn = new Button("Calculate Stresses");
    Button plotBtn = new Button("Plot");
    Button buckleBtn = new Button("Calculate Buckling data");

    ProgressBar progInd = new ProgressBar(0);

    SystemEquation s;

    HomeMenuBar menuBar = new HomeMenuBar(this);

    TabPane tabPane = new TabPane();
    Tab geometryTab = new Tab("Geometry");
    Tab loadTab = new Tab("Loads");

    public static void main(String[] args) {

        System.out.println("bla bla");
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        
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

                if (slider.isFocused()) {

                    //System.out.println("Changed from " + oldValue + " to " + newValue);
                    s.setDisplacedState(newValue.intValue());
                    draw();
                }
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
                    System.out.println("Nothing to plot");
                }

            }
        });

        calcBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                ModelProperties.getStripList().clear();
                for (UIStrip s : StripTableUtil.getStripList()) {
                    ModelProperties.addStrip(s);
                }

                for (Strip s : ModelProperties.getStripList()) {

                    s.setProperties(ModelProperties.getModelMaterial(), Double.parseDouble(thicknessField.textProperty().get()), Double.parseDouble(modelLengthField.textProperty().get()), ModelProperties.getFourierSeries());

                    //s.getStiffnessMatrix(1, 1).printf("k" + Integer.toString(s.getStripId()));
                    //s.getMembraneStiffnessMatrix(1, 1).printf("M");
                    // s.getRotationMatrix().printf("R");
                    //s.getRotatedLoadVector(1).printf("P"+Integer.toString(s.getStripId()));
                }

                ModelProperties.setModelLength(Double.parseDouble(modelLengthField.textProperty().get()));
                ModelProperties.getFourierSeries().setLength(ModelProperties.getModelLength());

                s = new SystemEquation(ModelProperties.getStripList(), NodeTableUtil.getNodeList());

                // Task<Void> task = new Task<Void>() {
                //    @Override
                //    protected Void call() throws Exception {
                progInd.progressProperty().bind(s.progressProperty());

                s.getDisplacementVector()[50].printf("U ");

                s.setDisplacedState((int) slider.getValue());
                System.out.println((int) slider.getValue());

                disCalced = true;

                draw();

                ModelProperties.getStripList().get(0).getNode1().getDisplacementContributionVectorAt(0, 50).printf(ModelProperties.getStripList().get(0).getNode1().toString() + " U m = 1 at 50%");

                ModelProperties.getStripList().get(0).getDisplacementVectorAt(50).printf(ModelProperties.getStripList().get(0).toString());

                for (Strip s : ModelProperties.getStripList()) {
                    //System.out.println(s.getPlaneStressVectorAt(s.getStripWidth() / 2.0, 50).get(1));
                  
//                    System.out.println(s.getPlaneStressVectorAt(s.getStripWidth(), 50).get(1));

                    for (int y = 0; y < 101; y++) {
                        System.out.println(s.getBendingStressVectorAt( s.getStripWidth()/2.0 , y).get(1));
                    }
                    
                    
                    Path p = new Path(new Point2D(50,0), new Point2D(50,1000), 101, s);
                }
                
                
                

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
                

                ModelProperties.setModelLength(Double.parseDouble(modelLengthField.textProperty().get()));

                ModelProperties.getStripList().clear();
                for (UIStrip s : StripTableUtil.getStripList()) {
                    ModelProperties.addStrip(s);
                }

                for (Strip s : ModelProperties.getStripList()) {

                    s.setProperties(ModelProperties.getModelMaterial(), Double.parseDouble(thicknessField.textProperty().get()), Double.parseDouble(modelLengthField.textProperty().get()), ModelProperties.getFourierSeries());
                    s.setEdgeTraction(0.001, 0.001);
                }

                BucklingEquation b = new BucklingEquation(ModelProperties.getStripList(), NodeTableUtil.getNodeList());

                double[][] buckleData = b.getBucklingCurve(100);
                String[][] stringData = new String[buckleData.length + 1][buckleData[0].length];
                
                

                stringData[0][0] = "Length";
                stringData[0][stringData[0].length - 1] = "Minimum stress (Signature curve)";

                double[] xData = new double[buckleData.length];
                double[] yData = new double[buckleData.length];

                for (int i = 1; i < stringData[0].length - 1; i++) {
                    stringData[0][i] = "Buckling stress for half wave";
                }

                for (int i = 0; i < buckleData.length; i++) {
                    for (int j = 0; j < buckleData[0].length; j++) {
                        stringData[i + 1][j] = Double.toString(buckleData[i][j]);

                        
                    }
                    
                    xData[i] = buckleData[i][0];
                    yData[i] = buckleData[i][buckleData[0].length-1];
                    
                }

                FileHandler f = new FileHandler();
                try {
                    f.writeCSV(stringData);
                } catch (IOException ex) {
                    Logger.getLogger(TableViewEdit.class.getName()).log(Level.SEVERE, null, ex);
                }

                XYChartDataUtil.addSeries(xData, yData, "Signature curve");
                LineChartWindow chart = new LineChartWindow("Minimum buckling stress vs physical length", "","Length","Stress",ModelProperties.getModelLength()/10,ModelProperties.getModelLength(),0,(yData[0]+ yData[yData.length-1])/100,XYChartDataUtil.getDataList());

                Stage s = new Stage();

                s.getIcons().add(ic);
                chart.start(s);

            }
        });

        stripPropertyBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                UIStrip s = stripTable.getSelectionModel().getSelectedItem();

                System.out.println("Strip " + s.getStripId() + " properties :");
                System.out.println("width = " + s.getStripWidth());

            }
        });

        stripRemoveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                UIStrip s = stripTable.getSelectionModel().getSelectedItem();

                if (s != null) {
                    StripTableUtil.removeStrip(s);
                    System.out.println("Strip " + s.getStripId() + " removed.");
                } else {
                    System.out.println("ERROR : No nodes selected !");
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
                System.out.println("Strip " + s.getStripId() + " added.");

            }
        });

        addBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Node n = new Node(0, 0);
                NodeTableUtil.addNode(n);
                draw();
                System.out.println("Node " + n.getNodeId() + " added.");

            }
        });

        removeBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Node n = table.getSelectionModel().getSelectedItem();

                if (n != null) {
                    NodeTableUtil.removeNode(n);
                    System.out.println("Node " + n.getNodeId() + " removed.");
                } else {
                    System.out.println("ERROR : No nodes selected !");
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

                    System.out.println("Editing " + n.toString() + " DOF");

                } else {
                    System.out.println("ERROR : No nodes selected !");
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

        rightBox.getChildren().addAll(mvp.getPane(), calcBtn, buckleBtn, progInd, plotBtn /*, slider*/);
        

        
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

        tableBox.getChildren().addAll(nodeLabel, nodeBox, stripLabel, stripBox,lengthLabel, modelLengthField,thicknessLabel,thicknessField);

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
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();
            Node node = e.getRowValue();

            System.out.println("X-Coordinate changed for Node "
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

            System.out.println("Z-Coordinate changed for Node "
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
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            
            try{
            int row = e.getTablePosition().getRow();

            UIStrip strip = e.getRowValue();

            System.out.println("First node changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

            StripTableUtil.getStripList().get(row).setNode1(NodeTableUtil.getNodeMap().get(e.getNewValue()));
            System.out.println("New node id " + NodeTableUtil.getNodeMap().get(e.getNewValue()).getNodeId());

            draw();
            }
            catch(Exception ex)
            {
                System.out.println("Node not found, create it and try again");
            }
            
            
        });

        table.getColumns().add(fNameCol);
    }

    public void addNode2Column(TableView<UIStrip> table) {

        TableColumn<UIStrip, Integer> fNameCol = StripTableUtil.getNode2Column();

        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<UIStrip, Integer>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            try{
            int row = e.getTablePosition().getRow();
            UIStrip strip = e.getRowValue();

            System.out.println("Second node changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

            StripTableUtil.getStripList().get(row).setNode2(NodeTableUtil.getNodeMap().get(e.getNewValue()));

            draw();
            }
            catch(Exception ex)
            {
                System.out.println("Node not found, create it and try again");
            }
            
            
        });

        table.getColumns().add(fNameCol);
    }

    public void draw() {
        mvp.draw();
    }


}
