/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;

/**
 *
 * @author SJ
 */
public class Node {

    private static AtomicInteger nodeSequence = new AtomicInteger(0);

    private final ReadOnlyIntegerWrapper nodeId = new ReadOnlyIntegerWrapper(this, "nodeId", nodeSequence.incrementAndGet());
    
    private final ReadOnlyDoubleWrapper xCoord = new ReadOnlyDoubleWrapper(this, "xCoord", 0);
    
    private final ReadOnlyDoubleWrapper zCoord = new ReadOnlyDoubleWrapper(this, "zCoord", 0);
    
    private double displacedXCoord = 0 ;
    private double displacedZCoord = 0 ;

    public Node(double xCoord ,double zCoord) 
    {
        
        this.xCoord.set(xCoord);
        this.zCoord.set(zCoord);
        
    }

    public double getDisplacedXCoord() {
        return displacedXCoord;
    }

    public void setDisplacedXCoord(double displacedXCoord) {
        this.displacedXCoord = displacedXCoord;
    }

    public double getDisplacedZCoord() {
        return displacedZCoord;
    }

    public void setDisplacedZCoord(double displacedZCoord) {
        this.displacedZCoord = displacedZCoord;
    }
    
    public static void clearNumbering()
    {
        nodeSequence.set(0);
    }
    
    public int getNodeId()
    {
        return nodeId.get();
    }
    
    public double getXCoord()
    {
        return xCoord.get();
    }
    
    public double getZCoord()
    {
        return zCoord.get();
                       
    }
    
    
    public DoubleProperty xCoordProperty()
    {
        return xCoord;
    }
    
    public DoubleProperty zCoordProperty()
    {
        return zCoord;
    }
    
    public void setXCoord(Double xCoord)
    {
        xCoordProperty().set(xCoord);
    }
    
    public void setZCoord(Double zCoord)
    {
        zCoordProperty().set(zCoord);
    }
    
    
    
    

}
