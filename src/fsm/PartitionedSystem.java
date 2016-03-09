/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm;

import linalg.Matrix;
import linalg.Vector;

/**
 *
 * @author SJ
 */
public class PartitionedSystem {

    Matrix Kpp, Kff, Kpf, Kfp;
    private int fDOF, pDOF;
    private boolean[] status;

    /**
     * Will set Kpp, Kff, Kfp, Kpf to corresponding part of Ks
     *
     * @param Ks system stiffness matrix
     * @param status true for prescribed value, false for unknown/free
     *
     */
    public PartitionedSystem(Matrix Ks, boolean[] status) {
        this.status = status;
        fDOF = 0;

        for (int i = 0; i < status.length; i++) {
            if (status[i] == false) {
                fDOF++;
            }
        }

        pDOF = status.length - fDOF;

        Kff = Matrix.getMatrix(fDOF, fDOF);
        Kpp = Matrix.getMatrix(pDOF, pDOF);
        Kpf = Matrix.getMatrix(pDOF, fDOF);
        Kfp = Matrix.getMatrix(fDOF, pDOF);

        int nextKppCol = 0;
        int nextKppRow = 0;

        int nextKpfCol = 0;
        int nextKpfRow = 0;

        int nextKfpCol = 0;
        int nextKfpRow = 0;

        int nextKffCol = 0;
        int nextKffRow = 0;

        for (int i = 0; i < Ks.rows(); i++) {
            for (int j = 0; j < Ks.cols(); j++) {

                if (status[i] == true) {
                    if (status[j] == true) {
                        Kpp.set(Ks.get(i, j), nextKppRow, nextKppCol);
                        nextKppCol++;

                        if (nextKppCol == Kpp.cols()) {
                            nextKppCol = 0;
                            nextKppRow++;
                        }

                    }

                    if (status[j] == false) {

                        Kpf.set(Ks.get(i, j), nextKpfRow, nextKpfCol);
                        nextKpfCol++;

                        if (nextKpfCol == Kpf.cols()) {
                            nextKpfCol = 0;
                            nextKpfRow++;
                        }

                    }

                }

                if (status[i] == false) {

                    if (status[j] == false) {
                        Kff.set(Ks.get(i, j), nextKffRow, nextKffCol);

                        nextKffCol++;

                        if (nextKffCol == Kff.cols()) {
                            nextKffCol = 0;
                            nextKffRow++;
                        }
                    }

                    if (status[j] == true) {
                        Kfp.set(Ks.get(i, j), nextKfpRow, nextKfpCol);
                        nextKfpCol++;

                        if (nextKfpCol == Kfp.cols()) {
                            nextKfpCol = 0;
                            nextKfpRow++;
                        }
                    }

                }

            }

        }

    }

    public Matrix getKpp() {
        return Kpp;
    }

    public Matrix getKff() {
        return Kff;
    }

    public Matrix getKpf() {
        return Kpf;
    }

    public Matrix getKfp() {
        return Kfp;
    }

    /**
     * Returns Wf (free nodal force parameters) if Ws (system nodal force parameters) are given
     * @param Ws
     * @return 
     */
    
   
    
    public Vector getWf(Vector Ws) {
        Vector Wf = Vector.getVector(fDOF);
        int count = 0;
        for (int i = 0; i < Ws.size(); i++) {
            if (!status[i]) {
                Wf.set(Ws.get(i), count);
                count++;
            }

        }

        return Wf;
    }

}
