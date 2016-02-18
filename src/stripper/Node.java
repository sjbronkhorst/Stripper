/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Model;
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
    Model model;
    
    private Vector [] parameterVectors;

    public Node(double xCoord ,double zCoord, Model model) 
    {
        parameterVectors = new Vector[model.getFourierTerms()];
        this.xCoord.set(xCoord);
        this.zCoord.set(zCoord);
        
        this.model = model;
        
        //displacementVectors = new Vector[ModelProperties.getFourierTerms()][101];
    }
    
//    public Node(double xCoord ,double zCoord) 
//    {
//       // parameterVectors = new Vector[model.getFourierTerms()];
//        this.xCoord.set(xCoord);
//        this.zCoord.set(zCoord);
//       // model.getNodeList().add(this);
//               
//        //displacementVectors = new Vector[ModelProperties.getFourierTerms()][101];
//    }
    
    /**
     * 
     * @param Pm parameter vector associated with term m
     * @param m fourier term number associated with Pm
     * 
     */
    public void setParameterVector(Vector P, int m ) 
    {
        
        parameterVectors[m] = P;
    }
    
    public boolean [] getStatus()
    {
        return dofPrescribedStatus;
    }
    
    public void setStatus(boolean [] status)
    {
        this.dofPrescribedStatus = status;
    }
    
    
    /**
     * 
     * @param m fourier term number
     * @return the parameter vector associated with m
     */
    
    public Vector getParameterContributionVector(int m) 
    {
        return parameterVectors[m];
    }
    


    /**
     * 
     * @return an array containing parameter vectors associated with all m's 
     */
    
    public Vector[] getParameterVectors() 
    {
        
        return parameterVectors;
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
