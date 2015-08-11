/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;


import linalg.Vector;

/**
 *
 * @author SJ
 */
public class CoupledVector {
    
     private Vector vector;
    private int nTerms;
    private int nDofPerNode;

    public CoupledVector(int nNodes, int nDofPerNode, int nTerms) 
    {

        vector = Vector.getVector(nNodes * nTerms * nDofPerNode);
        this.nTerms = nTerms;
        this.nDofPerNode = nDofPerNode;
        vector.clear();

    }
    
    public void addValue(double value, int nodeRow, int row, int m)
    {
        vector.add(value, scaledIndex(nodeRow, row, m));

    }
    
    public void addVector(Vector V, int nNodes, int m)
    {
        for (int i = 0; i < nNodes; i++)
        {
            for (int j = 0; j < V.size()/nNodes; j++) 
            {
                addValue(V.get(i*nNodes + j), i, j, m);
            }
            
            
        }
        
        
    }
    
    public int scaledIndex(int nodeRow, int row, int m) {
        return nodeRow * nTerms * nDofPerNode + (m - 1) * nDofPerNode + row;
    }
    
     public Vector getVector() {
        return vector;
    }
     
     
     
    
}
