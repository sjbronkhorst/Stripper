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

    /**
     *
     * @param steps total sampling points along model length
     * @return an array containing raw data for buckling curves for buckling
     * modes 1 to total number of Fourier terms set in ModelProperties. The last
     * column of the array contains the signature curve.
     */
    public BucklingDataPoint[] getBucklingLoads(int steps) {

        if (ModelProperties.getFourierSeries().isSimplySupported() || ModelProperties.ignoreCoupling) {
            BucklingDataPoint[] bucklingLoads = new BucklingDataPoint[steps];

            boolean[] status = new boolean[nodes.size() * 4];

            for (Node n : nodes) {
                status[(n.getNodeId() - 1) * 4] = n.getStatus()[0];
                status[(n.getNodeId() - 1) * 4 + 1] = n.getStatus()[1];
                status[(n.getNodeId() - 1) * 4 + 2] = n.getStatus()[2];
                status[(n.getNodeId() - 1) * 4 + 3] = n.getStatus()[3];

            }
            double Lmax = ModelProperties.getModelLength();
            double Lincrement = Lmax / ((double) steps);

            int index = 0;
            double hwl = Lincrement; // Half-Wave Length

            while (hwl <= Lmax) {

                double[] loads = new double[ModelProperties.getFourierTerms()];
                Vector[] eigVecs = new Vector[ModelProperties.getFourierTerms()];

                bucklingLoads[index] = new BucklingDataPoint(hwl, ModelProperties.getFourierTerms());

                for (int r = 1; r <= ModelProperties.getFourierTerms(); r++) { // r = mode shape

                    //ModelProperties.setModelLength(hwl);
                    Series Y = ModelProperties.getFourierSeries();
                    Y.setLength(hwl);

                    for (Strip s : strips) {
                        s.setProperties(s.getMaterial(), hwl, Y);

                        // s.getStiffnessMatrix(1, 1).printf("Ke direct from strip");
                        // s.getGeometricMatrix(1, 1).printf("Kg direct from strip");
                    }

                    Assembler a = new Assembler(strips, NodeTableUtil.getNodeList().size() * 4, localToGlobalConfNumbering);

                    PartitionedSystem se = new PartitionedSystem(a.getK(r), status);
                    PartitionedSystem sg = new PartitionedSystem(a.getKg(r), status);

                    Matrix Ke = se.getKff();
                    Matrix Kg = sg.getKff();

                    calcBucklingVectorValueAndIndex(Ke, Kg);
                    bucklingLoads[index].setSystemLoadFactor(r - 1, eigenMatrix.get(bucklingIndex, bucklingIndex));
                    bucklingLoads[index].setFreeParamVector(r - 1, eigenVectors.col(bucklingIndex));

                    

                }

                hwl += Lincrement;
                index++;

            }

            for (int i = 0; i < steps; i++) {
                bucklingLoads[i].calcMinParamAndLoad();
                
                 for (int r = 0; r < ModelProperties.getFourierTerms(); r++) 
                 {
                
                //Convert the free nodal param vector to a system nodal param vector
                    Vector Uf = bucklingLoads[i].getMinParamVector();

                    Vector Us = Vector.getVector(NodeTableUtil.getNodeList().size()*4);

                    Us.clear();
                    int count = 0;
                    for (int k = 0; k < Us.size(); k++) {
                        if (status[k] == false) {
                            Us.set(Us.get(k) + Uf.get(count), k);
                            count++;
                        }

                    }

                    bucklingLoads[i].setFreeParamVector(r,Us);
                
                 }
                
                bucklingLoads[i].uncoupledDecompose();

            }

            return bucklingLoads;
        }

        // ELSE ----------------------------------------------------------------
        System.out.println("Series not SS , commencing full solution");
        BucklingDataPoint[] bucklingLoads = new BucklingDataPoint[steps];

        int index = 0;
        double Lmax = ModelProperties.getModelLength();
        double Lincrement = Lmax / ((double) steps);
        double hwl = Lincrement; // Half-Wave Length
        System.out.println("Lmax =" + Lmax);

        while (hwl <= Lmax) {

            System.out.println("hwl =" + hwl);

            Series Y = ModelProperties.getFourierSeries();
            Y.setLength(hwl);

            for (Strip s : strips) {
                s.setProperties(s.getMaterial(), hwl, Y);

                // s.getStiffnessMatrix(1, 1).printf("Ke direct from strip");
                // s.getGeometricMatrix(1, 1).printf("Kg direct from strip");
            }

            CoupledMatrix_1 cKe = new CoupledMatrix_1(nodes.size(), ModelProperties.getFourierTerms());
            CoupledMatrix_1 cKg = new CoupledMatrix_1(nodes.size(), ModelProperties.getFourierTerms());
            CoupledVector_1 status = new CoupledVector_1(nodes.size(), ModelProperties.getFourierTerms());

//       Vector fK = Vector.getVector(8*nTerms);
//       fK.clear();
            for (int i = 1; i < ModelProperties.getFourierTerms() + 1; i++) {

                for (int j = 1; j < ModelProperties.getFourierTerms() + 1; j++) {

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

            bucklingLoads[index] = new BucklingDataPoint((index + 1) * Lincrement, ModelProperties.getFourierTerms());
            bucklingLoads[index].setMinLoadFactor(eigenMatrix.get(bucklingIndex, bucklingIndex));
            bucklingLoads[index].setMinParamVector(eigenVectors.col(bucklingIndex));

            //Convert the free nodal param vector to a system nodal param vector
            Vector Uf = bucklingLoads[index].getMinParamVector();

            Vector Us = Vector.getVector(cKe.getMatrix().rows());

            Us.clear();
            int count = 0;
            for (int k = 0; k < Us.size(); k++) {
                if (status.getVector().get(k) == 0) {
                    Us.set(Us.get(k) + Uf.get(count), k);
                    count++;
                }

            }

            bucklingLoads[index].setMinParamVector(Us);
            bucklingLoads[index].coupledDecompose();

            hwl += Lincrement;
            index++;

        }

        return bucklingLoads;

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
            if ((eigenMatrix.get(i, i) <= alphaMin) && (eigenMatrix.get(i, i) >= 0)) {
                alphaMin = eigenMatrix.get(i, i);
                index = i;

            }
        }

        Ke.release();
        Kg.release();

        bucklingIndex = index;

    }

    public void setDisplacedState(BucklingDataPoint point) {

       int[] indices = {0, 1, 2, 3};
            for (Node n : nodes) {

                for (int m = 0; m < ModelProperties.getFourierTerms(); m++) {

                    n.setParameterVector(point.freeParamVectors[m].getSubVector(indices), m);
                }

                for (int y = 0; y < 4; y++) {
                    indices[y] = indices[y] + 4;
                }

            }

    }

}
