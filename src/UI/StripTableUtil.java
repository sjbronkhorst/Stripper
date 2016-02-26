/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import stripper.Strip;




/**
 *
 * @author SJ
 */
public class StripTableUtil {

    private static ObservableList<Strip> strips = Defaults.getBaseModel().getStripList();
   

   

  
    
  
    
   
    
  
    

    public static TableColumn<Strip, Integer> getIDColumn() {
        TableColumn<Strip, Integer> idColumn = new TableColumn("Strip ID");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("stripId"));

        return idColumn;

    }

    public static TableColumn<Strip, Integer> getNode1Column() {
        TableColumn<Strip, Integer> node1Column = new TableColumn("Node 1");

        node1Column.setCellValueFactory(new PropertyValueFactory<>("node1Id"));

        return node1Column;

    }

    public static TableColumn<Strip, Integer> getNode2Column() {
        TableColumn<Strip, Integer> node2Column = new TableColumn("Node 2");

        node2Column.setCellValueFactory(new PropertyValueFactory<>("node2Id"));

        return node2Column;

    }
    
     public static TableColumn<Strip, Double> getStripThicknessColumn() {
        TableColumn<Strip, Double> tColumn = new TableColumn("t");

        tColumn.setCellValueFactory(new PropertyValueFactory<>("thickness"));

        return tColumn;

    }
    
    public static TableColumn<Strip, Double> getUDLZColumn() {
        TableColumn<Strip, Double> udlZColumn = new TableColumn("Load - Z");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("udlZ"));

        return udlZColumn;

    }
    
    public static TableColumn<Strip, Double> getUDLXColumn() {
        TableColumn<Strip, Double> udlZColumn = new TableColumn("Load - X");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("udlX"));

        return udlZColumn;

    }
    public static TableColumn<Strip, Double> getUDLYColumn() {
        TableColumn<Strip, Double> udlZColumn = new TableColumn("Load - Y");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("udlY"));

        return udlZColumn;

    }
    
    public static TableColumn<Strip, Double> getF1Column() {
        TableColumn<Strip, Double> udlZColumn = new TableColumn("f1");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("f1"));

        return udlZColumn;

    }
    
     public static TableColumn<Strip, Double> getF2Column() {
        TableColumn<Strip, Double> udlZColumn = new TableColumn("f2");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("f2"));

        return udlZColumn;

    }
     
     
}
