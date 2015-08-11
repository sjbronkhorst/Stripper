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
public class TermMatrix {

    private int nTerms , nDOF;

    private Matrix[][] tm;

    public TermMatrix(int nTerms , int nDOF) {

        this.nTerms = nTerms;
        this.nDOF = nDOF;

        tm = new Matrix[nTerms][nTerms];
        clear();

    }

    public void clear() {
        for (int i = 0; i < nTerms; i++) {
            for (int j = 0; j < nTerms; j++) {
                tm[i][j] = Matrix.getMatrix(nDOF, nDOF);
                tm[i][j].clear();
            }

        }
    }
    
    public Matrix[][] getDeepMatrix()
    {
        return tm;
    }
    
    public void addValue(int m , int n , int DOFcolumn , int DOFrow , double value)
    {
        double v = getValue(m,n,DOFcolumn, DOFrow);
        
        tm[n][m].set(value + v, DOFrow, DOFcolumn);
    }
    
    public double getValue(int m , int n , int DOFcolumn , int DOFrow)
    {
        return tm[n][m].get(DOFrow, DOFcolumn);
    }
    
    public Matrix getFlatMatrix()
    {
        Matrix flatMat = Matrix.getMatrix(nDOF*nTerms, nDOF*nTerms);
        
        for (int i = 0; i < nTerms; i++)
        {
            for (int j = 0; j < nTerms; j++) 
            {
                
                int [] rows = new int[nDOF];
                int [] cols = new int[nDOF];
                
                for (int k = 0; k < nDOF; k++)
                {
                    rows[k] = k+i*nDOF;
                    //System.out.print(k+i*nDOF + " ");
                    cols[k] = k+j*nDOF;
                    
                    
                    
                    
                }
                
                //System.out.println("");
                
                
            flatMat.addSubmatrix(tm[i][j],rows, cols);
            }
   
        }
        
        return flatMat;
    }

    
    
    
    public static void main (String [] args)
    {
        TermMatrix t = new TermMatrix(2, 4);
        
        
        
        t.addValue(0, 0, 0, 0, 5);
        t.addValue(0, 0, 1, 1, 7);
        
        
        t.getDeepMatrix()[0][0].printf("Deep");
        //t.getDeepMatrix()[1][1].printf("D");
        
        t.getFlatMatrix().printf("Flat");
        
        
        
        
    }
}


