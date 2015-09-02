/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 *
 * @author SJ
 */
public class PointLoadTableUtil 
{
     private static ObservableList<PointLoad> pointLoads = FXCollections.<PointLoad>observableArrayList();

   
    
    public static void setStrip(UIStrip strip)
    {
        
        pointLoads.clear();
        
        for(PointLoad p : strip.getPointLoadList())
        {
        pointLoads.add(p);
        }
        
        
        
    }
    
    public static ObservableList<PointLoad> getLoadList()
    {
        return pointLoads;
    }
    
    public static void addPointLoad(PointLoad p , UIStrip strip)
    {
        pointLoads.add(p);
        strip.getPointLoadList().add(p);
    }
    
    public static void removePointLoad(PointLoad p , UIStrip strip)
    {
        pointLoads.remove(p);
        strip.getPointLoadList().remove(p);
    }
    
    public static TableColumn<PointLoad, Integer> getIDColumn() {
        TableColumn<PointLoad, Integer> idColumn = new TableColumn("Point Load ID");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("pointLoadId"));

        return idColumn;

    }
    
    public static TableColumn<PointLoad, Integer> getStripIDColumn() {
        TableColumn<PointLoad, Integer> idColumn = new TableColumn("Strip ID");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("stripId"));

        return idColumn;

    }
    
    
     public static TableColumn<PointLoad, Double> getXCoordColumn() {
        TableColumn<PointLoad, Double> idColumn = new TableColumn("Local X-Coord");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("xCoord"));

        return idColumn;

    }
     
     public static TableColumn<PointLoad, Double> getYCoordColumn() {
        TableColumn<PointLoad, Double> idColumn = new TableColumn("Local Y-Coord");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("yCoord"));

        return idColumn;

    }
     
     public static TableColumn<PointLoad, Double> getMagnitudeColumn() {
        TableColumn<PointLoad, Double> idColumn = new TableColumn("Magnitude");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("magnitude"));

        return idColumn;

    }
     
     
     
     
     
     
     
     
     
    
    
    
    
    
    
}
