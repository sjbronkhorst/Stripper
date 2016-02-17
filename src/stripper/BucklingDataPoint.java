/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.NodeTableUtil;
import java.util.ArrayList;
import java.util.List;
import linalg.Vector;

/**
 *
 * @author SJ
 */
public class BucklingDataPoint {

    double physicalLength;
    double[] systemLoadFactors;
    Vector[] freeParamVectors;
    double minLoadFactor;
    Vector minParamVector;
    int minIndex;

    private int nTerms;
    

    public BucklingDataPoint(double physicalLength, int nTerms) {
        this.physicalLength = physicalLength;
        this.nTerms = nTerms;

        systemLoadFactors = new double[nTerms];
        freeParamVectors = new Vector[nTerms];

    }

    public double getPhysicalLength() {
        return physicalLength;
    }

    public double getSystemLoadFactor(int m) {
        return systemLoadFactors[m];
    }

    public Vector getFreeParamVector(int m) {
        return freeParamVectors[m];
    }

    public double getMinLoadFactor() {
        return minLoadFactor;
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
        this.minLoadFactor = minLoadFactor;
    }

    public void setMinParamVector(Vector minParamVector) {
        this.minParamVector = minParamVector;
       
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
    
    public void uncoupledDecompose()
    {
        for (int i = 0; i < nTerms; i++)
        {
         if(i != minIndex)
         {
             freeParamVectors[i].clear();
         }
        }
    }
    
    public void coupledDecompose()
    {
        
        for (int m = 0; m < nTerms; m++)
            {
                freeParamVectors[m] = Vector.getVector(NodeTableUtil.getNodeList().size()*4);
            }
        
        
        
        for (int n = 0; n < NodeTableUtil.getNodeList().size(); n++)
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
