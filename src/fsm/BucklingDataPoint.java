/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm;

import UI.Defaults;
import Utils.NodeTableUtil;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import linalg.Vector;

/**
 *
 * @author SJ
 */
public class BucklingDataPoint {

    private ReadOnlyDoubleWrapper physicalLength = new ReadOnlyDoubleWrapper(this, "physicalLength", 0.0);
    double[] systemLoadFactors;
    Vector[] freeParamVectors;
    private ReadOnlyDoubleWrapper minLoadFactor = new ReadOnlyDoubleWrapper(this, "minLoadFactor", 0.0);
    Vector minParamVector;
    int minIndex;

    private int nTerms;
    

    public BucklingDataPoint(double physicalLength, int nTerms) {
        this.physicalLength.set(physicalLength); 
        this.nTerms = nTerms;

        systemLoadFactors = new double[nTerms];
        freeParamVectors = new Vector[nTerms];

    }

    public double getPhysicalLength() {
        return physicalLength.get();
    }

    public double getSystemLoadFactor(int m) {
        return systemLoadFactors[m];
    }

    public Vector getFreeParamVector(int m) {
        return freeParamVectors[m];
    }

    public double getMinLoadFactor() {
        return minLoadFactor.get();
    }

    public Vector getMinParamVector() {
        return minParamVector;
    }

    public void setSystemLoadFactor(int m, double load) {
        systemLoadFactors[m] = load;
    }

    public void setFreeParamVector(int m, Vector eigVec) {
        freeParamVectors[m] = eigVec;
       
    }

    public void setMinLoadFactor(double minLoadFactor) {
        this.minLoadFactor.set(minLoadFactor); 
    }

    public void setMinParamVector(Vector minParamVector) {
        this.minParamVector = minParamVector;
       
    }
    
    public DoubleProperty physicalLengthProperty()
    {
        return physicalLength;
    }
    
    public DoubleProperty minLoadFactorProperty()
    {
        return minLoadFactor;
    }
    
   

    /**
     * Used for S-S special case strips
     */
    public void calcMinParamAndLoad() {

        double minLoad = getSystemLoadFactor(0);
        minIndex = 0;

        for (int i = 0; i < nTerms; i++) {
            if (getSystemLoadFactor(i) < minLoad) {
                minLoad = getSystemLoadFactor(i);
                minIndex = i;
            }
        }

        setMinLoadFactor(minLoad);
        setMinParamVector(getFreeParamVector(minIndex));

    }
    
    public int getMinIndex()
    {
        return minIndex;
    }
    
//    public void uncoupledDecompose()
//    {
//        for (int i = 0; i < nTerms; i++)
//        {
//         if(i != minIndex)
//         {
//             freeParamVectors[i].clear();
//         }
//        }
//    }
    
    public void coupledDecompose()
    {
        
        for (int m = 0; m < nTerms; m++)
            {
                freeParamVectors[m] = Vector.getVector(Defaults.getBaseModel().getNodeList().size()*4);
            }
        
        
        
        for (int n = 0; n < Defaults.getBaseModel().getNodeList().size(); n++)
        {
            int startIndex = n*4*nTerms;
            
            for (int m = 0; m < nTerms; m++)
            {
             int [] ind = {startIndex+0+4*m,startIndex+1+4*m,startIndex+2+4*m,startIndex+3+4*m};
                
                freeParamVectors[m].set(getMinParamVector().getSubVector(ind).get(0), n*4);
                freeParamVectors[m].set(getMinParamVector().getSubVector(ind).get(1), n*4+1);
                freeParamVectors[m].set(getMinParamVector().getSubVector(ind).get(2), n*4+2);
                freeParamVectors[m].set(getMinParamVector().getSubVector(ind).get(3), n*4+3);
                
                
                
            }
            
            
            
        }
        
        
        
        
        
    }

}
