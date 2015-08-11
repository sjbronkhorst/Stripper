/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;


import linalg.Matrix;

/**
 *
 * @author SJ
 */
public class CoupledMatrix_1 {

    private Matrix matrix;
    private int nTerms;
    private int nDofPerNode = 4;

    public CoupledMatrix_1(int nNodes, int nTerms) {

        matrix = Matrix.getMatrix(nNodes * nTerms * nDofPerNode, nNodes * nTerms * nDofPerNode);
        
       
        this.nTerms = nTerms;
        this.nDofPerNode = nDofPerNode;
        matrix.clear();

    }

    /**
     *
     * @param value
     * @param row
     * @param column
     * @param m starts at 1
     */
    public void addValue(double value, int nodeRow, int nodeColumn, int row, int column, int m , int n ) {
        matrix.add(value, scaledIndex(nodeRow, row, m), scaledIndex(nodeColumn, column, n));

    }

    public void addMatrix(Matrix K, int nNodes, int m, int n) {
        int[] rowIndices = new int[K.rows() / nNodes];
        int[] colIndices = new int[K.cols() / nNodes];
        
         int[] refRowIndices = new int[K.rows() / nNodes];
        int[] refColIndices = new int[K.cols() / nNodes];
        
        for (int i = 0; i < rowIndices.length; i++) 
            {
                refRowIndices[i] = i ;
                refColIndices[i] = i ;
            }
        
        
        

        Matrix[][] Ksplit = new Matrix[nNodes][nNodes];

        for (int r = 0; r < nNodes; r++) {
            
            for (int i = 0; i < rowIndices.length; i++) 
            {
                rowIndices[i] = i + r * nNodes;
            }

            for (int c = 0; c < nNodes; c++) 
            {

                for (int j = 0; j < rowIndices.length; j++) {
                    colIndices[j] = j + c * nNodes;
                }

                Ksplit[r][c] = Matrix.getMatrix(K.rows() / nNodes, K.cols() / nNodes);

                Ksplit[r][c].addSubmatrix(K.getSubMatrix(rowIndices, colIndices),refRowIndices , refColIndices);
                
                for (int i = 0; i < K.rows() / nNodes ; i++)
                {
                    for (int j = 0; j < K.cols() / nNodes; j++)
                    {
                        addValue(Ksplit[r][c].get(i, j), r, c, i, j, m , n);
                    }
   
                }

            }
        }
        
        

    }
    
    public void addQuarterMatrix(Matrix quarterMatrix , int nodeRow , int nodeColumn , int m , int n)
    {
      
        int rowOffset = scaledIndex(nodeRow, 0, m);
        int colOffset = scaledIndex(nodeColumn, 0, n);
        
        int [] rowIndex = {rowOffset , rowOffset +1 , rowOffset +2, rowOffset +3};
        int [] colIndex = {colOffset , colOffset +1 , colOffset +2, colOffset +3};
        
        matrix.addSubmatrix(quarterMatrix, rowIndex, colIndex);
        
    }
    
    public void addStiffnessMatrix(Matrix stiffness, Node firstNode , Node secondNode , int m , int n)
    {
        int [] ind1 = {0,1,2,3};
        int [] ind2 = {4,5,6,7};
        
        Matrix S11 = stiffness.getSubMatrix(ind1, ind1);
        Matrix S22 = stiffness.getSubMatrix(ind2,ind2);
        
        Matrix S12 = stiffness.getSubMatrix(ind1, ind2);
        
        Matrix S21 = stiffness.getSubMatrix(ind2,ind1);
        
        
        addQuarterMatrix(S11, firstNode.getNodeId()-1, firstNode.getNodeId()-1, m, n);
        
        addQuarterMatrix(S22, secondNode.getNodeId()-1, secondNode.getNodeId()-1, m, n);
        
        addQuarterMatrix(S12, firstNode.getNodeId()-1, secondNode.getNodeId()-1, m, n);
        
        addQuarterMatrix(S21, secondNode.getNodeId()-1, firstNode.getNodeId()-1, m, n);
        
        
        
        
    }

    public int scaledIndex(int nodeRow, int row, int m) {
        return nodeRow * nTerms * nDofPerNode + (m - 1) * nDofPerNode + row;
    }

    public Matrix getMatrix() {
        return matrix;
    }
    
    
    
    
    
    

}
