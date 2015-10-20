/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.ModelProperties;
import UI.NodeTableUtil;
import java.util.ArrayList;
import java.util.List;
import linalg.Matrix;
import stripper.series.Series;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class BucklingEquation {

    List<Strip> strips = new ArrayList<>();
    List<Node> nodes = new ArrayList<>();
    int[][] localToGlobalConfNumbering;

    public BucklingEquation(List<Strip> strips, List<Node> nodes) {
        this.strips = strips;
        this.nodes = nodes;

        localToGlobalConfNumbering = new int[strips.size() * 8][2];

        for (int i = 0; i < strips.size() * 8; i++) {
            localToGlobalConfNumbering[i][0] = i;
        }
        int count = 0;

        for (Strip s : strips) {

            localToGlobalConfNumbering[count * 8][1] = (s.getNode1Id() - 1) * 4;
            localToGlobalConfNumbering[count * 8 + 1][1] = (s.getNode1Id() - 1) * 4 + 1;
            localToGlobalConfNumbering[count * 8 + 2][1] = (s.getNode1Id() - 1) * 4 + 2;
            localToGlobalConfNumbering[count * 8 + 3][1] = (s.getNode1Id() - 1) * 4 + 3;

            localToGlobalConfNumbering[count * 8 + 4][1] = (s.getNode2Id() - 1) * 4;
            localToGlobalConfNumbering[count * 8 + 5][1] = (s.getNode2Id() - 1) * 4 + 1;
            localToGlobalConfNumbering[count * 8 + 6][1] = (s.getNode2Id() - 1) * 4 + 2;
            localToGlobalConfNumbering[count * 8 + 7][1] = (s.getNode2Id() - 1) * 4 + 3;

            count++;

        }

    }

    public double[] getBucklingCurve(int steps) {
        double Lmax = ModelProperties.getModelLength();

        double Lincrement = Lmax / ((double) steps);

        double hwl = Lincrement; // Half-Wave Length

        double[] bucklingLoads = new double[steps];

        boolean[] status = new boolean[nodes.size() * 4]; // r is always = 1 for buckling
        
        for (Node n : nodes)
        {
         status[(n.getNodeId() - 1)*4] = n.getStatus()[0]; 
         status[(n.getNodeId() - 1)*4 +1] = n.getStatus()[1]; 
         status[(n.getNodeId() - 1)*4 +2] = n.getStatus()[2]; 
         status[(n.getNodeId() - 1)*4 +3] = n.getStatus()[3]; 
        }
        

        int index = 0;

        while (hwl <= Lmax) {
            //ModelProperties.setModelLength(hwl);

            Series Y = ModelProperties.getFourierSeries();
            Y.setLength(hwl);

            for (Strip s : strips) {
                s.setProperties(s.getMaterial(), s.getStripThickness(), hwl, Y);

                // s.getStiffnessMatrix(1, 1).printf("Ke direct from strip");
                // s.getGeometricMatrix(1, 1).printf("Kg direct from strip");
            }

            Assembler a = new Assembler(strips, NodeTableUtil.getNodeList().size() * 4, localToGlobalConfNumbering);

            SystemSolver se = new SystemSolver(a.getK(1), status);
            SystemSolver sg = new SystemSolver(a.getKg(1), status);

            Matrix Ke = se.getKff();
            Matrix Kg = sg.getKff();

            bucklingLoads[index] = getBucklingLoad(Ke, Kg);

            hwl += Lincrement;
            index++;
        }

        return bucklingLoads;
    }

    public double getBucklingLoad(Matrix Keff, Matrix Kgff) {
        Matrix Ke = Keff;
        Matrix Kg = Kgff;

        Matrix K = Ke.multiply(Kg.inverse());
        Matrix eig = K.eigenMatrix();

        //eig.printf("eig values");
        //ev.printf("eig vectors");
        double alphaMin = eig.get(0, 0);
        for (int i = 0; i < eig.cols(); i++) {
            if (eig.get(i, i) <= alphaMin) {
                alphaMin = eig.get(i, i);

            }
        }

        //System.out.println(alphaMin);
        return alphaMin;

    }

}
