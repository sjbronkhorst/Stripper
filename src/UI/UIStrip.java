/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stripper.Node;

/**
 *
 * @author SJ
 */
public class UIStrip {

    private Node node1;
    private Node node2;
    private static AtomicInteger stripSequence = new AtomicInteger(0);
    private final ReadOnlyIntegerWrapper stripId = new ReadOnlyIntegerWrapper(this, "stripId", stripSequence.incrementAndGet());
    private final ReadOnlyIntegerWrapper node1Id = new ReadOnlyIntegerWrapper(this, "node1Id", 0);
    private final ReadOnlyIntegerWrapper node2Id = new ReadOnlyIntegerWrapper(this, "node2Id", 0);
    private ObservableList<UI.PointLoad> pointLoads = FXCollections.<UI.PointLoad>observableArrayList();
    private final ReadOnlyDoubleWrapper udlX = new ReadOnlyDoubleWrapper(this, "udlX", 0.0);
    private final ReadOnlyDoubleWrapper udlY = new ReadOnlyDoubleWrapper(this, "udlY", 0.0);
    private final ReadOnlyDoubleWrapper udlZ = new ReadOnlyDoubleWrapper(this, "udlZ", 0.0);
    
    private final ReadOnlyDoubleWrapper t = new ReadOnlyDoubleWrapper(this, "t", 0.0);
    
     protected final ReadOnlyDoubleWrapper f1 = new ReadOnlyDoubleWrapper(this, "f1", 0.157);
    protected final ReadOnlyDoubleWrapper f2 = new ReadOnlyDoubleWrapper(this, "f2", 0.157);
    

    private boolean hasNode1, hasNode2;

    public UIStrip(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;

        hasNode1 = true;
        hasNode2 = true;
    }
    
    

    public UIStrip() {
        this.node1 = null;
        this.node2 = null;

        hasNode1 = false;
        hasNode2 = false;
    }
    
    public static void clearNumbering()
    {
        stripSequence.set(0);
    }

    public boolean hasBothNodes() {
        return (hasNode1 && hasNode2);
    }

    public Node getNode1() {
        return node1;
    }

    public void setNode1(Node node1) {
        this.node1 = node1;
        hasNode1 = true;
    }

    public Node getNode2() {
        return node2;
        
    }

    public void setNode2(Node node2) {
        this.node2 = node2;
        hasNode2 = true;
    }

    public int getStripId() {
        return stripId.get();
    }

    public int getNode1Id() {
        if(hasBothNodes())
        {
        return node1.getNodeId();
        }
        return 0;
    }

    public int getNode2Id() {
        if(hasBothNodes())
        {
        return node2.getNodeId();
        }
        return 0;
    }

    public ObservableList<PointLoad> getPointLoadList() {
        return pointLoads;
    }

    public void setUdlZ(double magnitude) {
        udlZ.set(magnitude);
    }

    public double getUdlZ() {
        return udlZ.doubleValue();
    }

    public void setUdlX(double magnitude) {
        udlX.set(magnitude);
    }

    public double getUdlX() {
        return udlX.doubleValue();
    }

    public void setUdlY(double magnitude) {
        udlY.set(magnitude);
    }

    public double getUdlY() {
        return udlY.doubleValue();
    }

    public double getStripWidth() {
         if(hasBothNodes())
        {   
        return Math.sqrt(Math.pow((node1.getXCoord() - node2.getXCoord()), 2) + Math.pow((node1.getZCoord() - node2.getZCoord()), 2));
        }
         return 0;
    }
    
    public DoubleProperty thicknessProperty()
    {
        return t;
    }

    public double getStripThickness() {
        return t.doubleValue();
    }

    public void setStripThickness(double thickness) {
        t.set(thickness);
    }

    public double getStripAngle() {
        
        if(hasBothNodes())
        {                 
        return Math.atan((node2.getZCoord() - node1.getZCoord()) / (node2.getXCoord() - node1.getXCoord()));
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Strip " + stripId.getValue().toString();
    }
    
    public DoubleProperty f1Property()
     {
         return f1;
     }
     
     public DoubleProperty f2Property()
     {
         return f2;
     }
     
      public void setF1(double f1) {
        this.f1.setValue(f1);
        
    }
    
     public void setF2(double f2) {
        this.f2.setValue(f2);
        
    }
     
      public double getF1() {
        return f1.doubleValue();
        
    }
    
      public double getF2() {
        return f2.doubleValue();
        
    }
    
   
}
