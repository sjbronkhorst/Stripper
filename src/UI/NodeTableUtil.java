/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.HashMap;
import java.util.Map;
import stripper.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author SJ
 */
public class NodeTableUtil {

    private static ObservableList<Node> nodes = Defaults.getBaseModel().getNodeList();
    
    private static Map<Integer , Node> nodeMap = Defaults.getBaseModel().getNodeMap();

    public static ObservableList<Node> getNodeList() {
        return nodes;
    }

    public static void addNode(Node n) {
        nodes.add(n);
        nodeMap.put(n.getNodeId(), n);
    }
    
    public static void removeNode(Node n)
    {
        nodes.remove(n);
        nodeMap.remove(n.getNodeId());
    }
    
    public static void clearNodes()
    {
        int i = nodes.size();
        
        
        for (int j = 0;  j < i;j ++)
        {
            nodeMap.remove(nodes.get(0).getNodeId(), nodes.get(0));
            nodes.remove(0);
            
        }
        
        
        
    }

    public static TableColumn<Node, Integer> getIDColumn() {
        TableColumn<Node, Integer> idColumn = new TableColumn("Node ID");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("nodeId"));

        return idColumn;

    }

    public static TableColumn<Node, Double> getXCoordColumn() {
        TableColumn<Node, Double> xColumn = new TableColumn("X-Coord");

        xColumn.setCellValueFactory(new PropertyValueFactory<>("xCoord"));

        return xColumn;

    }

    public static TableColumn<Node, Double> getZCoordColumn() {
        TableColumn<Node, Double> zColumn = new TableColumn("Z-Coord");

        zColumn.setCellValueFactory(new PropertyValueFactory<>("zCoord"));

        return zColumn;

    }
    
    public static Map<Integer , Node> getNodeMap()
    {
        return nodeMap;
    }

}
