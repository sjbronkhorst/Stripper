/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import linalg.Matrix;
import linalg.Vector;

/**
 *
 * @author SJ
 */
public class CoupledVector_1
{
    private Vector vector;
    private int nTerms;
    private int nDofPerNode = 4;
    
    public CoupledVector_1(int nNodes, int nTerms) {

        vector = Vector.getVector(nNodes * nTerms * nDofPerNode);
        
       
        this.nTerms = nTerms;
        this.nDofPerNode = nDofPerNode;
        vector.clear();

    }
    
    public void addQuarterMatrix(Vector halfVector , int nodeRow , int nodeColumn , int m)
    {
      
        int rowOffset = scaledIndex(nodeRow, 0, m);
        
        
        int [] rowIndex = {rowOffset , rowOffset +1 , rowOffset +2, rowOffset +3};
        
        
        vector.add(halfVector, rowIndex);
        
    }
    
    public void addForceVector(Vector force, Node firstNode , Node secondNode , int m)
    {
        int [] ind1 = {0,1,2,3};
        int [] ind2 = {4,5,6,7};
        
        
        Vector S11 = force.getSubVector(ind1);
        Vector S22 = force.getSubVector(ind2);
        
       
        
        
        addQuarterMatrix(S11, firstNode.getNodeId()-1, firstNode.getNodeId()-1, m);
        
        addQuarterMatrix(S22, secondNode.getNodeId()-1, secondNode.getNodeId()-1, m);
        
        
        
        
        
        
    }

    public int scaledIndex(int nodeRow, int row, int m) {
        return nodeRow * nTerms * nDofPerNode + (m - 1) * nDofPerNode + row;
    }

    public Vector getVector() {
        return vector;
    }
    
}
