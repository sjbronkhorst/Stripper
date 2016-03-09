/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import fsm.Strip;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;



/**
 *
 * @author SJ
 */
public class PointLoad {

    

    private static AtomicInteger loadSequence = new AtomicInteger(0);
    private final ReadOnlyIntegerWrapper pointLoadId = new ReadOnlyIntegerWrapper(this, "pointLoadId", loadSequence.incrementAndGet());
    private final ReadOnlyStringWrapper dirString = new ReadOnlyStringWrapper(this , "disString" , null);
    
    private final ReadOnlyDoubleWrapper xCoord = new ReadOnlyDoubleWrapper(this, "xCoord", 0.0);
    private final ReadOnlyDoubleWrapper yCoord = new ReadOnlyDoubleWrapper(this, "yCoord", 0.0);
    private final ReadOnlyDoubleWrapper magnitude = new ReadOnlyDoubleWrapper(this, "magnitude", 0.0);
    
    
    
    public PointLoad() {
        
               
    }

    public double getX() {
        return xCoord.doubleValue();
    }

    public double getY() {
        return yCoord.doubleValue();
    }

    public double getMagnitude() {
        return magnitude.doubleValue();
    }
    
    public int getID()
    {
        return pointLoadId.get();
    }
    
    public IntegerProperty pointLoadIdProperty()
    {
        return pointLoadId;
    }
    
    
    
    public DoubleProperty xCoordProperty()
    {
        return xCoord;
    }
    
    public DoubleProperty yCoordProperty()
    {
        return yCoord;
    }
    
    public DoubleProperty magnitudeProperty()
    {
        return magnitude;
    }
    
   
    
    public void setMagnitude(double magnitude)
    {
        magnitudeProperty().set(magnitude);
    }
    
   public void setXCoord(Double xCoord)
    {
        xCoordProperty().set(xCoord);
    }
    
    public void setYCoord(Double yCoord)
    {
        yCoordProperty().set(yCoord);
    }
    
    
    
    
    
    
    
    
    

}
