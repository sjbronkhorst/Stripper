package UI;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import stripper.materials.Material;
import stripper.materials.Material_Steel;

public class TableViewEdit extends Application {
    
    ModelViewPane mvp = new ModelViewPane();
    
    LoadPane lp = new LoadPane(this);
    
    TextField modelLengthField = new TextField();
    TextField thicknessField = new TextField();
    
    private double biggestX = 0;
    private double biggestY = 0;
    
    Button calcBtn = new Button("Calculate");
    
    ProgressBar progInd = new ProgressBar(0);
    
    SystemEquation s;
    
    HomeMenuBar menuBar = new HomeMenuBar(this);
    
    TabPane tabPane = new TabPane();
    Tab geometryTab = new Tab("Geometry");
    Tab loadTab = new Tab("Loads");
    
    
    public static void main(String[] args) {
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
        
        TableView<Strip> stripTable = new TableView<>(StripTableUtil.getStripList());
        stripTable.setEditable(true);
        addStripIdColumn(stripTable);
        addNode1Column(stripTable);
        addNode2Column(stripTable);
        
        stripTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        Button addBtn = new Button("Add");
        Button removeBtn = new Button("Remove");
        
        Button stripAddBtn = new Button("Add");
        Button stripRemoveBtn = new Button("Remove");
        Button stripPropertyBtn = new Button("Properties");
        
        Button plotBtn = new Button("Plot");
        
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
                
                LineChartWindow chart = new LineChartWindow("Displacement along member", "Displacement along member", "Distance along member", "Displacement", 0, Double.parseDouble(modelLengthField.textProperty().get()), XYChartDataUtil.getDataList());
                
                Stage s = new Stage();
                
                s.getIcons().add(ic);
                chart.start(s);
                
            }
        });
        
        calcBtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                
                for (Strip s : StripTableUtil.getStripList()) {
                    
                    Material mat = new Material_Steel();
                    ModelProperties.setModelMaterial(mat);
                    s.setProperties(ModelProperties.getModelMaterial(), Double.parseDouble(thicknessField.textProperty().get()), Double.parseDouble(modelLengthField.textProperty().get()));
                    //s.getStiffnessMatrix(1, 1).printf("k" + Integer.toString(s.getStripId()));
                    //s.getMembraneStiffnessMatrix(1, 1).printf("M");
                    // s.getRotationMatrix().printf("R");
                    //s.getRotatedLoadVector(1).printf("P"+Integer.toString(s.getStripId()));

                }
                
                s = new SystemEquation(StripTableUtil.getStripList(), NodeTableUtil.getNodeList());
                
                Task<Void> task = new Task<Void>() {
                    
                    @Override
                    protected Void call() throws Exception {
                        
                        s.getDisplacementVector()[50].printf("U ");
                        s.setDisplacedState((int) slider.getValue());
                        System.out.println((int) slider.getValue());
                        draw();
                        
                        return null;
                    }
                };
                progInd.progressProperty().bind(s.progressProperty());
                
                new Thread(task).start();

                
                
                
                
                
            }
        });
        
        stripPropertyBtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                
                Strip s = stripTable.getSelectionModel().getSelectedItem();
                
                System.out.println("Strip " + s.getStripId() + " properties :");
                System.out.println("width = " + s.getStripWidth());
                
            }
        });
        
        stripRemoveBtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                Strip s = stripTable.getSelectionModel().getSelectedItem();
                
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
                Strip s = new Strip();
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
        
        loadTab.setContent(lp.getPane());
        
        VBox tableBox = new VBox(10);
        HBox nodeBox = new HBox(10);
        HBox stripBox = new HBox(10);
        
        VBox nodeControlBox = new VBox(10);
        VBox stripControlBox = new VBox(10);
        
        VBox rightBox = new VBox();
        
        rightBox.getChildren().addAll(mvp.getPane(), modelLengthField, thicknessField, calcBtn, progInd, plotBtn, slider);
        
        rightBox.setStyle("-fx-padding: 0;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 0;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: black;");
        
        //progInd.setPrefWidth(2000);
       
                
        
                
        
        nodeBox.getChildren().addAll(table, nodeControlBox);
        addBtn.setMinWidth(130);
        removeBtn.setMinWidth(130);
        
        nodeControlBox.getChildren().addAll(addBtn, removeBtn);
        
        stripControlBox.getChildren().addAll(stripAddBtn, stripRemoveBtn, stripPropertyBtn);
        
        stripAddBtn.setMinWidth(130);
        stripRemoveBtn.setMinWidth(130);
        
        stripBox.getChildren().addAll(stripTable, stripControlBox);
        
        Label nodeLabel = new Label("Nodes :");
        Label stripLabel = new Label("Strips :");
        
        tableBox.getChildren().addAll(nodeLabel, nodeBox, stripLabel, stripBox);
        
        geometryTab.setContent(tableBox);
        //loadTab.setContent(loadTable);

        tabPane.getTabs().addAll(geometryTab, loadTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //tableBox.setMinHeight(1200);
        //tableBox.setMinWidth(400);
        SplitPane sp = new SplitPane();
        
        sp.getItems().addAll(tabPane, rightBox);
        
        BorderPane root = new BorderPane(sp);
        tableBox.setStyle("-fx-padding: 10;"
                + "-fx-border-style: solid inside;"
                + "-fx-border-width: 0;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;"
                + "-fx-border-color: blue;");
        
        root.setTop(menuBar.getMenuBar());
        //root.setLeft(tabPane);

        tableBox.prefHeightProperty().bind(root.heightProperty());

        //root.getChildren().addAll(menuBar, tableBox, rightBox);
//        root.setStyle("-fx-padding: 10;"
//                + "-fx-border-style: solid inside;"
//                + "-fx-border-width: 2;"
//                + "-fx-border-insets: 5;"
//                + "-fx-border-radius: 5;"
//                + "-fx-border-color: blue;");
        root.setPrefSize(800, 600);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Stripper - FSM by SJ Bronkhorst");
        stage.show();
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
    
    public void addStripIdColumn(TableView<Strip> table) {
// Id column is non-editable
        table.getColumns().add(StripTableUtil.getIDColumn());
    }
    
    public void addNode1Column(TableView<Strip> table) {
        
        TableColumn<Strip, Integer> fNameCol = StripTableUtil.getNode1Column();
        
        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Integer>forTableColumn(converter));
        
        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });
        
        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();
            
            Strip strip = e.getRowValue();
            
            System.out.println("First node changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());
            
            StripTableUtil.getStripList().get(row).setNode1(NodeTableUtil.getNodeMap().get(e.getNewValue()));
            System.out.println("New node id " + NodeTableUtil.getNodeMap().get(e.getNewValue()).getNodeId());
            
            draw();
        });
        
        table.getColumns().add(fNameCol);
    }
    
    public void addNode2Column(TableView<Strip> table) {
        
        TableColumn<Strip, Integer> fNameCol = StripTableUtil.getNode2Column();
        
        IntegerStringConverter converter = new IntegerStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Integer>forTableColumn(converter));
        
        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });
        
        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();
            Strip strip = e.getRowValue();
            
            System.out.println("Second node changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());
            
            StripTableUtil.getStripList().get(row).setNode2(NodeTableUtil.getNodeMap().get(e.getNewValue()));
            
            draw();
        });
        
        table.getColumns().add(fNameCol);
    }
    
    public void draw() {
        mvp.draw();
    }

//    public void addLastNameColumn(TableView<Node> table) {
//// Last Name is a String, editable column
//        TableColumn<Node, String> lNameCol = NodeTableUtil.getLastNameColumn();
//// Use a TextFieldTableCell, so it can be edited
//        lNameCol.setCellFactory(TextFieldTableCell.<Node>forTableColumn());
//        table.getColumns().add(lNameCol);
//    }
//
//    public void addBirthDateColumn(TableView<Node> table) {
//// Birth Date is a LocalDate, editable column
//        TableColumn<Node, LocalDate> birthDateCol
//                = NodeTableUtil.getBirthDateColumn();
//// Use a TextFieldTableCell, so it can be edited
//        LocalDateStringConverter converter = new LocalDateStringConverter();
//        birthDateCol.setCellFactory(
//                TextFieldTableCell.<Node, LocalDate>forTableColumn(converter));
//        table.getColumns().add(birthDateCol);
//    }
//
//    public void addBabyColumn(TableView<Node> table) {
//// Baby? is a Boolean, non-editable column
//        TableColumn<Node, Boolean> babyCol = new TableColumn<>("Baby?");
//        babyCol.setEditable(false);
//// Set a cell value factory
//        babyCol.setCellValueFactory(cellData -> {
//            Node p = cellData.getValue();
//            Boolean v = (p.getAgeCategory() == Node.AgeCategory.BABY);
//            return new ReadOnlyBooleanWrapper(v);
//        });
//// Use a CheckBoxTableCell to display the boolean value
//        babyCol.setCellFactory(
//                CheckBoxTableCell.<Node>forTableColumn(babyCol));
//        table.getColumns().add(babyCol);
//    }
//
//    public void addGenderColumn(TableView<Node> table) {
//// Gender is a String, editable, ComboBox column
//        TableColumn<Node, String> genderCol = new TableColumn<>("Gender");
//        genderCol.setMinWidth(80);
//// By default, all cells are have null values
//        genderCol.setCellValueFactory(
//                cellData -> new ReadOnlyStringWrapper(null));
//// Set a ComboBoxTableCell, so you can selects a value from a list
//        genderCol.setCellFactory(
//                ComboBoxTableCell.<Node, String>forTableColumn("Male", "Female"));
//// Add an event handler to handle the edit commit event.
//// It displays the selected value on the standard output
//        genderCol.setOnEditCommit(e -> {
//            int row = e.getTablePosition().getRow();
//            Node person = e.getRowValue();
//            System.out.println("Gender changed for "
//                    + person.getFirstName() + " " + person.getLastName()
//                    + " at row " + (row + 1) + " to " + e.getNewValue());
//        });
//        table.getColumns().add(genderCol);
//    }
}
