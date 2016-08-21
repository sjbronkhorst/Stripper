/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm;

import java.util.Arrays;
import java.util.List;
import linalg.Matrix;
import linalg.Vector;

/**
 *
 * @author SJ
 * 
 * 
 */
public class Assembler {

    int localDOF = 0;
    int globalDOF = 0;
    int nrOfElements = 0;
    List<Strip> strips;
    Matrix T;

    public Assembler(List<Strip> strips, int nrOfGlobalDOF, int[][] localToGlobalConfNumbering) {

        localDOF = localToGlobalConfNumbering.length;
        globalDOF = nrOfGlobalDOF;
        nrOfElements = strips.size();
        
        

        T = Matrix.getMatrix(localDOF, globalDOF);
        T.clear();
        for (int i = 0; i < localDOF; i++) {
            
           
            
            T.set(1, localToGlobalConfNumbering[i][0], localToGlobalConfNumbering[i][1]);
        }

        this.strips = strips;

    }

    public Matrix getK(int m) {
        Matrix Kt = Matrix.getMatrix(localDOF, localDOF);
        Kt.clear();

        for (int i = 0; i < nrOfElements; i++) {

            int[] indices = new int[strips.get(i).getStiffnessMatrix(m , m).cols()];
            for (int j = 0; j < indices.length; j++) {
                indices[j] = i * indices.length + j;
            }

            Kt.addSubmatrix(strips.get(i).getRotatedStiffnessMatrix(m, m), indices);
        }

        Matrix K = T.transpose().multiply(Kt).multiply(T);

        return K;
    }
    
     public Matrix getKg(int m) {
        Matrix Kt = Matrix.getMatrix(localDOF, localDOF);
        Kt.clear();

        for (int i = 0; i < nrOfElements; i++) {

            int[] indices = new int[strips.get(i).getGeometricMatrix(m, m).cols()];
            for (int j = 0; j < indices.length; j++) {
                indices[j] = i * indices.length + j;
            }

            Kt.addSubmatrix(strips.get(i).getRotatedGeometricMatrix(m, m), indices);
        }

        Matrix K = T.transpose().multiply(Kt).multiply(T);

        return K;
    }

    public Vector getF(int m) 
    {
        
        Vector Ft = Vector.getVector(localDOF);
        Ft.clear();
        
        for (int i = 0; i < nrOfElements; i++) 
        {
            for (int j = 0; j < strips.get(i).getLoadVector(m).size(); j++) 
            {
                Ft.set(strips.get(i).getRotatedLoadVector(m).get(j), j + i*strips.get(i).getLoadVector(m).size());
            }
            
            
     
        }
        Vector F = T.transpose().multiply(Ft);
        

        
        
        
        return F;
    }

}
