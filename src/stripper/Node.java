/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.ModelProperties;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import linalg.Vector;

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
    private boolean [] dofPrescribedStatus = {false,false,false,false}; // u v w theta
    
    private Vector [][] displacementVectors = new Vector[ModelProperties.getFourierTerms()][101];

    public Node(double xCoord ,double zCoord) 
    {
        
        this.xCoord.set(xCoord);
        this.zCoord.set(zCoord);
        
        //displacementVectors = new Vector[ModelProperties.getFourierTerms()][101];
    }
    
    /**
     * 
     * @param u displacement vector @ i% of length
     * @param i longitudinal distance %
     * 
     */
    public void setParameterVector(Vector u, int m , int yPercentage) 
    {
        displacementVectors[m][yPercentage] = u;
    }
    
    public boolean [] getStatus()
    {
        return dofPrescribedStatus;
    }
    
    public void setStatus(boolean [] status)
    {
        this.dofPrescribedStatus = status;
    }
    
    public Vector getParameterContributionVectorAt(int m , int yPercentage) 
    {
        return displacementVectors[m][yPercentage];
    }
    
//    public Vector getDisplacementVectorAt(int yPercentage)  // needs scaling
//    {
//        Vector u = Vector.getVector(4);
//        u.clear();
//        
//        for (int m = 0; m < ModelProperties.getFourierTerms(); m++) 
//        {
//            Vector scale = Vector.getVector(4);
//            scale.set(getParameterContributionVectorAt(m , yPercentage).get(0)*ModelProperties.getFourierSeries().getFunctionValue((yPercentage/100.0)*ModelProperties.getModelLength(), m), 0);
//            scale.set(getParameterContributionVectorAt(m , yPercentage).get(1)*ModelProperties.getFourierSeries().getFunctionValue((yPercentage/100.0)*ModelProperties.getModelLength(), m), 1);
//            scale.set(getParameterContributionVectorAt(m , yPercentage).get(2)*ModelProperties.getFourierSeries().getFunctionValue((yPercentage/100.0)*ModelProperties.getModelLength(), m), 2);
//            scale.set(getParameterContributionVectorAt(m , yPercentage).get(3)*ModelProperties.getFourierSeries().getVScalingValue((yPercentage/100.0)*ModelProperties.getModelLength(), m), 3);
//            
//            
//            
//            u.add(scale);
//            
//            scale.release();
//        }
//        
//        
//        return u;
//    }

    public Vector[][] getDisplacementVectors() 
    {
        
        return displacementVectors;
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
    
    @Override
    public String toString()
    {
        return "Node " + getNodeId();
    }
    
    
    
    

}
