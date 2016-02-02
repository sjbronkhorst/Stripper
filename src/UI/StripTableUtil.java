/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;




/**
 *
 * @author SJ
 */
public class StripTableUtil {

    private static ObservableList<UIStrip> strips = FXCollections.<UIStrip>observableArrayList();
    private static Map<Integer , UIStrip> stripMap = new HashMap();

    public static ObservableList<UIStrip> getStripList() {
        return strips;
    }

    public static void addStrip(UIStrip n) {
        strips.add(n);
        stripMap.put(n.getStripId(), n);
        
    }
    
    public static void removeStrip(UIStrip n)
    {
        strips.remove(n);
        stripMap.remove(n);
                
    }
    
    public static void clearStrips()
    {
        int i = strips.size();
        
        
        for (int j = 0;  j < i;j ++)
        {
            stripMap.remove(strips.get(0).getStripId(), strips.get(0));
            strips.remove(0);
            
        }
        
    }
    
   public static Map<Integer , UIStrip> getStripMap()
    {
        return stripMap;
    }
    
    

    public static TableColumn<UIStrip, Integer> getIDColumn() {
        TableColumn<UIStrip, Integer> idColumn = new TableColumn("Strip ID");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("stripId"));

        return idColumn;

    }

    public static TableColumn<UIStrip, Integer> getNode1Column() {
        TableColumn<UIStrip, Integer> node1Column = new TableColumn("Node 1");

        node1Column.setCellValueFactory(new PropertyValueFactory<>("node1Id"));

        return node1Column;

    }

    public static TableColumn<UIStrip, Integer> getNode2Column() {
        TableColumn<UIStrip, Integer> node2Column = new TableColumn("Node 2");

        node2Column.setCellValueFactory(new PropertyValueFactory<>("node2Id"));

        return node2Column;

    }
    
     public static TableColumn<UIStrip, Double> getStripThicknessColumn() {
        TableColumn<UIStrip, Double> tColumn = new TableColumn("t");

        tColumn.setCellValueFactory(new PropertyValueFactory<>("thickness"));

        return tColumn;

    }
    
    public static TableColumn<UIStrip, Double> getUDLZColumn() {
        TableColumn<UIStrip, Double> udlZColumn = new TableColumn("Load - Z");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("udlZ"));

        return udlZColumn;

    }
    
    public static TableColumn<UIStrip, Double> getUDLXColumn() {
        TableColumn<UIStrip, Double> udlZColumn = new TableColumn("Load - X");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("udlX"));

        return udlZColumn;

    }
    public static TableColumn<UIStrip, Double> getUDLYColumn() {
        TableColumn<UIStrip, Double> udlZColumn = new TableColumn("Load - Y");

        udlZColumn.setCellValueFactory(new PropertyValueFactory<>("udlY"));

        return udlZColumn;

    }

}
