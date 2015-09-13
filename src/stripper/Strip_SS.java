package stripper;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import UI.PointLoad;
import UI.UIStrip;
import linalg.Matrix;
import linalg.Vector;

import stripper.materials.Material;
import stripper.series.Series_SS;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public class Strip_SS extends Strip {

    public Strip_SS(Node node1, Node node2) {

        setNode1(node1);
        setNode2(node2);

    }

    public Strip_SS() {

        hasNode1 = false;
        hasNode2 = false;
        this.node1Id.set(0);
        this.node2Id.set(0);

    }

    public Strip_SS(UIStrip uiStrip) {
        if (!uiStrip.hasBothNodes()) {

            hasNode1 = false;
            hasNode2 = false;
            this.node1Id.set(0);
            this.node2Id.set(0);
        } else {
            setNode1(uiStrip.getNode1());
            setNode2(uiStrip.getNode2());
            
            setUdlX(uiStrip.getUdlX());
            setUdlY(uiStrip.getUdlY());
            setUdlZ(uiStrip.getUdlZ());
            
            this.stripId.set(uiStrip.getStripId());
            
            this.pointLoads = uiStrip.getPointLoadList();
        }
    }

    @Override
    public void setProperties(Material mat, double thickness, double length, Series Y) {
        super.mat = mat;
        super.t = thickness;
        super.a = length;
        super.Y = new Series_SS(a);
    }

    public Matrix getBendingStiffnessMatrix(int m, int n) {

        Matrix S = Matrix.getMatrix(4, 4);

        if (m == n) {

            double b = getStripWidth();
            double k = (m * Math.PI) / a;

            double Ex = mat.getEx();
            double Ey = mat.getEy();
            double vx = mat.getVx();
            double vy = mat.getVy();
            double G = mat.getG();

            double Dx = (Ex * t * t * t) / (12.0 * (1 - vx * vy));

            double Dy = (Ey * t * t * t) / (12.0 * (1 - vx * vy));

            double D1 = (vx * Ey * t * t * t) / (12.0 * (1 - vx * vy));

            double Dxy = G * t * t * t / 12.0;

            S.set((13 * a * b * k * k * k * k * Dy / 70) + ((12 * a * k * k * Dxy) / (5 * b)) + (6 * a * k * k * D1 / (5 * b)) + (6 * a * Dx / (b * b * b)), 0, 0);
            S.set(S.get(0, 0), 2, 2);

            S.set((3 * a * k * k * D1 / 5) + (a * k * k * Dxy / 5) + (3 * a * Dx / (b * b)) + (11 * a * b * b * k * k * k * k * Dy / (420)), 1, 0);
            S.set(S.get(1, 0), 0, 1);
            S.set(-S.get(1, 0), 3, 2);
            S.set(S.get(3, 2), 2, 3);

            S.set((a * b * b * b * k * k * k * k * Dy / 210) + (4 * a * b * k * k * Dxy / 15) + (2 * a * b * k * k * D1 / 15) + (2 * a * Dx / b), 1, 1);
            S.set(S.get(1, 1), 3, 3);

            S.set((9 * a * b * k * k * k * k * Dy / 140) - (12 * a * k * k * Dxy / (5 * b)) - (6 * a * k * k * D1 / (5 * b)) - (6 * a * Dx / (b * b * b)), 2, 0);
            S.set(S.get(2, 0), 0, 2);

            S.set(-(13 * a * b * b * k * k * k * k * Dy / 840) + (a * k * k * Dxy / 5) + (a * k * k * D1 / 10) + (3 * a * Dx / (b * b)), 3, 0);
            S.set(S.get(3, 0), 0, 3);

            S.set(-S.get(3, 0), 2, 1);
            S.set(S.get(2, 1), 1, 2);

            S.set(-(3 * a * b * b * b * k * k * k * k * Dy / 840) - (a * b * k * k * Dxy / 15) - (a * b * k * k * D1 / 30) + (a * Dx / b), 3, 1);
            S.set(S.get(3, 1), 1, 3);
        }

        if (m != n) {
            S.clear();
        }

        return S;
    }

    public Matrix getMembraneStiffnessMatrix(int m, int n) {

        Matrix M = Matrix.getMatrix(4, 4);

        if (m == n) {

            double b = getStripWidth();

            double Ex = mat.getEx();
            double Ey = mat.getEy();
            double vx = mat.getVx();
            double vy = mat.getVy();
            double G = mat.getG();
            double k = (m * Math.PI) / a;

            double E1 = Ex / (1 - vx * vy);
            double E2 = Ey / (1 - vx * vy);

            double K11 = a * E1 / (2.0 * b) + (a * b * k * k * G) / 6.0;

            double K12 = (a * k * vx * E2 / 4.0) - a * k * G / 4.0;

            double K13 = -a * E1 / (2.0 * b) + a * b * k * k * G / 12;

            double K14 = (a * k * vx * E2 / 4.0) + a * k * G / 4;

            double K22 = (a * b * k * k * E2 / 6.0) + (a * G / (2.0 * b));

            double K23 = -(a * k * vx * E2 / 4.0) - (a * k * G / 4.0);

            double K24 = (a * b * k * k * E2 / 12.0) - (a * G / (2.0 * b));

            double K33 = K11;
            double K34 = -K12;

            double K44 = K22;

            double K21 = K12;
            double K31 = K13;
            double K32 = K23;
            double K41 = K14;
            double K42 = K24;
            double K43 = K34;

            M.set(K11, 0, 0);
            M.set(K12, 0, 1);
            M.set(K13, 0, 2);
            M.set(K14, 0, 3);

            M.set(K21, 1, 0);
            M.set(K22, 1, 1);
            M.set(K23, 1, 2);
            M.set(K24, 1, 3);

            M.set(K31, 2, 0);
            M.set(K32, 2, 1);
            M.set(K33, 2, 2);
            M.set(K34, 2, 3);

            M.set(K41, 3, 0);
            M.set(K42, 3, 1);
            M.set(K43, 3, 2);
            M.set(K44, 3, 3);

            M.scale(getStripThickness());
        }

        if (m != n) {
            M.clear();
        }

        return M;
    }

    @Override
    public Matrix getStiffnessMatrix(int m, int n) {
        Matrix K = Matrix.getMatrix(8, 8);

        K.clear();

        int[] bendingIndices = {2, 3, 6, 7};

        K.addSubmatrix(getBendingStiffnessMatrix(m, n), bendingIndices);

        int[] membraneIndices = {0, 1, 4, 5};

        K.addSubmatrix(getMembraneStiffnessMatrix(m, n), membraneIndices);

        return K;
    }

}
