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
public class CoupledMatrix {

    private Matrix matrix;
    private int nTerms;
    private int nDofPerNode;

    public CoupledMatrix(int nNodes, int nDofPerNode, int nTerms) {

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
    public void addValue(double value, int nodeRow, int nodeColumn, int row, int column, int m ) {
        matrix.add(value, scaledIndex(nodeRow, row, m), scaledIndex(nodeColumn, column, m));

    }

    public void addMatrix(Matrix K, int nNodes, int m) {
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
                        addValue(Ksplit[r][c].get(i, j), r, c, i, j, m);
                    }
   
                }

            }
        }
        
        

    }

    public int scaledIndex(int nodeRow, int row, int m) {
        return nodeRow * nTerms * nDofPerNode + (m - 1) * nDofPerNode + row;
    }

    public Matrix getMatrix() {
        return matrix;
    }

}
