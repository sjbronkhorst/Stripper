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
public class NodeMatrix {

    
    
    TermMatrix [][] nodeMatrix;
    
    int nNodes , nTerms , nDOF;
    
    
    
    
    public NodeMatrix(int nNodes , int nTerms , int nDOF) 
    {
           nodeMatrix = new TermMatrix [nNodes][nNodes];
           
           this.nDOF = nDOF;
           this.nNodes  = nNodes;
           this.nTerms = nTerms;
           
           clear();
           
           
           
    }
    
    public Matrix getFlatMatrix()
    {
       Matrix flatMat = Matrix.getMatrix(nNodes*nDOF*nTerms, nNodes*nDOF*nTerms); 
       
        for (int i = 0; i < nNodes; i++)
        {
            for (int j = 0; j < nNodes; j++)
            {
            
                 int [] rows = new int[nDOF*nTerms];
                int [] cols = new int[nDOF*nTerms];
                
                for (int k = 0; k < nDOF*nTerms; k++)
                {
                    rows[k] = k+i*nDOF*nTerms;
                    //System.out.print(k+i*nDOF + " ");
                    cols[k] = k+j*nDOF*nTerms;
                                               
                    
                }
                
                
                flatMat.addSubmatrix(nodeMatrix[i][j].getFlatMatrix(),rows, cols);
                
                
                
            }
   
        }
       
       
       
       
       return flatMat;
    }
    
    public void addValue(int nodeRow, int nodeColumn , int m , int n , int DOFcolumn , int DOFrow , double value)
    {
        nodeMatrix[nodeRow][nodeColumn].addValue(m, n, DOFcolumn, DOFrow, value);
    }
    
    public double getValue(int nodeRow, int nodeColumn , int m , int n , int DOFcolumn , int DOFrow)
    {
        return nodeMatrix[nodeRow][nodeColumn].getValue(m, n, DOFcolumn, DOFrow);
    }
    
    public void clear()
    {
        for (int i = 0; i < nNodes; i++)
        {
            for (int j = 0; j < nNodes; j++)
            {
            nodeMatrix[i][j] = new TermMatrix(nTerms, nDOF);
            }
   
        }
    }
    
    public void addMatrix()
    {
        
    }
    
    
    
    public static void main (String [] args)
    {
        NodeMatrix n = new NodeMatrix(2, 2, 2);
        
        n.addValue(1,1, 1, 1, 1, 1, 5);
        n.addValue(1,1, 1, 1, 1, 1, 5);
        
        n.getFlatMatrix().printf("F");
        
    }

}
