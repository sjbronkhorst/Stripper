/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javax.swing.plaf.basic.BasicTreeUI;
import stripper.Strip;

/**
 *
 * @author SJ
 * 
 * 
 * 
 * 
 * 
 */
public class LoadPane 
{
    
    private ChoiceBox<Strip> stripChoice = new ChoiceBox<>(StripTableUtil.getStripList());
    private Label titleLabel = new Label("Select a Strip to edit its point loads :");
    private Label udlLabel = new Label("UDL (in element local coordinates) :");
    private Label udlZLabel = new Label("Z-magnitude");
    private Label udlXLabel = new Label("X-magnitude");
    private Label udlYLabel = new Label("Y-magnitude");
    
    private TextField udlZTextF = new TextField();
    private TextField udlXTextF = new TextField();
    private TextField udlYTextF = new TextField();
    
    private TableView<Strip> loadTable = new TableView<>(StripTableUtil.getStripList());
    
    private TableView<PointLoad> pointLoadTable = new TableView<>(PointLoadTableUtil.getLoadList());
    
    private TableViewEdit viewer;
    
    private Button pointLoadAddBtn = new Button("Add");
    private Button pointLoadRemoveBtn = new Button("Remove");
        

    public LoadPane(TableViewEdit viewer) {
        
        this.viewer = viewer;
        
        loadTable.setEditable(true);
        
        
        addStripIdColumn(loadTable);
        addUdlX(loadTable);
        addUdlY(loadTable);
        addUdlZ(loadTable);
        
        loadTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        pointLoadTable.setEditable(true);
        addPointLoadIdColumn(pointLoadTable);
        addPointLoadXCoord(pointLoadTable);
        addPointLoadYCoord(pointLoadTable);
        
        addPointLoadMagnitude(pointLoadTable);
        pointLoadTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        
        pointLoadAddBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                PointLoad p = new PointLoad();
                PointLoadTableUtil.addPointLoad(p,stripChoice.getValue());
                viewer.draw();
                System.out.println("PointLoad " + p.getID() + " added to " + stripChoice.getValue().toString());

            }
        });
        
         pointLoadRemoveBtn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                
                PointLoad p = pointLoadTable.getSelectionModel().getSelectedItem();
                
                if (p != null) {
                    PointLoadTableUtil.removePointLoad(p,stripChoice.getValue());
                    System.out.println("PointLoad " + p.getID() + " removed from " + stripChoice.getValue().toString());
                } else {
                    System.out.println("ERROR : No pointloads selected !");
                }
                viewer.draw();

            }
        });
        
         
         stripChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                
            PointLoadTableUtil.setStrip(stripChoice.getItems().get((int)newValue));
            
                
                //System.out.println("Index :" + stripChoice.getItems().get((int)newValue).toString());
                
            }
        });
       
    
    }
    
    public VBox getPane()
    {
        VBox pane = new VBox(10);
        
        pane.getChildren().addAll(udlLabel , loadTable,titleLabel,stripChoice,pointLoadTable, pointLoadAddBtn,pointLoadRemoveBtn );
        return pane;
    }
            
    
    public void addUdlZ(TableView<Strip> table) {

        TableColumn<Strip, Double> fNameCol = StripTableUtil.getUDLZColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();

            Strip strip = e.getRowValue();

            System.out.println("Z UDL changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

            StripTableUtil.getStripList().get(row).setUdlZ(e.getNewValue());
            
            viewer.draw();
        });

        table.getColumns().add(fNameCol);
    }
    public void addUdlX(TableView<Strip> table) {

        TableColumn<Strip, Double> fNameCol = StripTableUtil.getUDLXColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();

            Strip strip = e.getRowValue();

            System.out.println("X UDL changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

            StripTableUtil.getStripList().get(row).setUdlX(e.getNewValue());
            
            viewer.draw();
        });

        table.getColumns().add(fNameCol);
    }
    
     public void addUdlY(TableView<Strip> table) {

        TableColumn<Strip, Double> fNameCol = StripTableUtil.getUDLYColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<Strip, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();

            Strip strip = e.getRowValue();

            System.out.println("Y UDL changed for Strip "
                    + strip.getStripId() + " at row " + (row + 1) + " to " + e.getNewValue());

            StripTableUtil.getStripList().get(row).setUdlY(e.getNewValue());
            
            viewer.draw();
        });

        table.getColumns().add(fNameCol);
    }
    
      public void addStripIdColumn(TableView<Strip> table) {
// Id column is non-editable
        table.getColumns().add(StripTableUtil.getIDColumn());
    }
      
       public void addPointLoadIdColumn(TableView<PointLoad> table) {
// Id column is non-editable
        table.getColumns().add(PointLoadTableUtil.getIDColumn());
    }
      
    public void addPointLoadMagnitude(TableView<PointLoad> table) {

        TableColumn<PointLoad, Double> fNameCol = PointLoadTableUtil.getMagnitudeColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<PointLoad, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();

            PointLoad p = e.getRowValue();

            System.out.println("Magnitude changed for PointLoad "
                    + p.getID()+ " at row " + (row + 1) + " to " + e.getNewValue());

            PointLoadTableUtil.getLoadList().get(row).setMagnitude(e.getNewValue());
            
            viewer.draw();
        });

        table.getColumns().add(fNameCol);
    }
    
     public void addPointLoadXCoord(TableView<PointLoad> table) {

        TableColumn<PointLoad, Double> fNameCol = PointLoadTableUtil.getXCoordColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<PointLoad, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();

            PointLoad p = e.getRowValue();

            System.out.println("XCoord changed for PointLoad "
                    + p.getID()+ " at row " + (row + 1) + " to " + e.getNewValue());

            PointLoadTableUtil.getLoadList().get(row).setXCoord(e.getNewValue());
            
            viewer.draw();
        });

        table.getColumns().add(fNameCol);
    }
    
     public void addPointLoadYCoord(TableView<PointLoad> table) {

        TableColumn<PointLoad, Double> fNameCol = PointLoadTableUtil.getYCoordColumn();

        DoubleStringConverter converter = new DoubleStringConverter();
        fNameCol.setCellFactory(TextFieldTableCell.<PointLoad, Double>forTableColumn(converter));

        fNameCol.setOnEditStart(e -> {
            System.out.println("Press Enter to save changes, Esc to cancel");
        });

        fNameCol.setOnEditCommit(e -> {
            int row = e.getTablePosition().getRow();

            PointLoad p = e.getRowValue();

            System.out.println("YCoord changed for PointLoad "
                    + p.getID()+ " at row " + (row + 1) + " to " + e.getNewValue());

            PointLoadTableUtil.getLoadList().get(row).setYCoord(e.getNewValue());
            
            viewer.draw();
        });

        table.getColumns().add(fNameCol);
    }
    
    
    
    
    
    
    
    
}
