/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Defaults;
import UI.Model;
import UI.NodeTableUtil;
import java.util.ArrayList;
import java.util.List;
import linalg.Matrix;
import linalg.Vector;
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
    private int bucklingIndex;
    private Matrix eigenMatrix, eigenVectors;
    private Model model;

    public BucklingEquation(Model model) {
        this.strips = model.getStripList();
        this.nodes = model.getNodeList();
        this.model = model;

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

    /**
     *
     * @param steps total sampling points along model length
     * @return an array containing raw data for buckling curves for buckling
     * modes 1 to total number of Fourier terms set in ModelProperties. The last
     * column of the array contains the signature curve.
     */
    public BucklingDataPoint getBucklingData() {

        BucklingDataPoint bucklePoint = new BucklingDataPoint(model.getModelLength(), model.getFourierTerms());

        if (model.getFourierSeries().isSimplySupported() || Defaults.ignoreCoupling) {

            boolean[] status = new boolean[nodes.size() * 4];

            for (Node n : nodes) {
                status[(n.getNodeId() - 1) * 4] = n.getStatus()[0];
                status[(n.getNodeId() - 1) * 4 + 1] = n.getStatus()[1];
                status[(n.getNodeId() - 1) * 4 + 2] = n.getStatus()[2];
                status[(n.getNodeId() - 1) * 4 + 3] = n.getStatus()[3];

            }

            for (int r = 1; r <= model.getFourierTerms(); r++) { // r = mode shape

                Assembler a = new Assembler(strips, NodeTableUtil.getNodeList().size() * 4, localToGlobalConfNumbering);

                PartitionedSystem se = new PartitionedSystem(a.getK(r), status);
                PartitionedSystem sg = new PartitionedSystem(a.getKg(r), status);

                Matrix Ke = se.getKff();
                Matrix Kg = sg.getKff();

                calcBucklingVectorValueAndIndex(Ke, Kg);
                bucklePoint.setSystemLoadFactor(r - 1, Math.abs(eigenMatrix.get(bucklingIndex, bucklingIndex)));
                bucklePoint.setFreeParamVector(r - 1, eigenVectors.col(bucklingIndex));

            }

            bucklePoint.calcMinParamAndLoad();

            for (int r = 0; r < model.getFourierTerms(); r++) {

                //Convert the free nodal param vector to a system nodal param vector
                Vector Uf = bucklePoint.getMinParamVector();

                Vector Us = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);

                Us.clear();
                int count = 0;
                for (int k = 0; k < Us.size(); k++) {
                    if (status[k] == false) {
                        Us.set(Us.get(k) + Uf.get(count), k);
                        count++;
                    }

                }

                bucklePoint.setFreeParamVector(r, Us);

            }

                //bucklingLoads[i].uncoupledDecompose();
            return bucklePoint;
        }

        // ELSE ----------------------------------------------------------------
        System.out.println("Series not SS , commencing full solution");

        CoupledMatrix_1 cKe = new CoupledMatrix_1(nodes.size(), model.getFourierTerms());
        CoupledMatrix_1 cKg = new CoupledMatrix_1(nodes.size(), model.getFourierTerms());
        CoupledVector_1 status = new CoupledVector_1(nodes.size(), model.getFourierTerms());

//       Vector fK = Vector.getVector(8*nTerms);
//       fK.clear();
        for (int i = 1; i < model.getFourierTerms() + 1; i++) {

            for (int j = 1; j < model.getFourierTerms() + 1; j++) {

                for (Strip myStrip : strips) {
                    cKe.addStiffnessMatrix(myStrip.getRotatedStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
                    cKg.addStiffnessMatrix(myStrip.getRotatedGeometricMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
                }

            }

            for (Strip myStrip : strips) {

                status.addForceVector(myStrip.getStatusVector(), myStrip.getNode1(), myStrip.getNode2(), i);

            }

        }

        System.out.println("Starting solver");

        PartitionedSystem se = new PartitionedSystem(cKe.getMatrix(), Converter.vecToBool(status.getVector()));
        PartitionedSystem sg = new PartitionedSystem(cKg.getMatrix(), Converter.vecToBool(status.getVector()));

        Matrix Ke = se.getKff();
        Matrix Kg = sg.getKff();

        calcBucklingVectorValueAndIndex(Ke, Kg);

        bucklePoint.setMinLoadFactor(Math.abs(eigenMatrix.get(bucklingIndex, bucklingIndex)));
        bucklePoint.setMinParamVector(eigenVectors.col(bucklingIndex));

        //Convert the free nodal param vector to a system nodal param vector
        Vector Uf = bucklePoint.getMinParamVector();

        Vector Us = Vector.getVector(cKe.getMatrix().rows());

        Us.clear();
        int count = 0;
        for (int k = 0; k < Us.size(); k++) {
            if (status.getVector().get(k) == 0) {
                Us.set(Us.get(k) + Uf.get(count), k);
                count++;
            }

        }

        bucklePoint.setMinParamVector(Us);
        bucklePoint.coupledDecompose();

        return bucklePoint;

    }

    public void calcBucklingVectorValueAndIndex(Matrix Keff, Matrix Kgff) {
        Matrix Ke = Keff;
        Matrix Kg = Kgff;
        int index = 0;

//        Ke.printf("Ke");
//        Kg.printf("Kg");
        Matrix K = Ke.multiply(Kg.inverse());

        eigenMatrix = K.eigenMatrix();
        eigenVectors = K.getEigenVectors();

        //eig.printf("eig values");
        //ev.printf("eig vectors");
        double alphaMin = eigenMatrix.get(0, 0);
        for (int i = 0; i < eigenMatrix.cols(); i++) {
            if (Math.abs(eigenMatrix.get(i, i)) <= Math.abs(alphaMin)) 
            {
                alphaMin = eigenMatrix.get(i, i);
                index = i;

            }
        }

        Ke.release();
        Kg.release();

        bucklingIndex = index;

    }

}
