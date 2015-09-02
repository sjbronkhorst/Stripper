/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import stripper.Assembler;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import linalg.Vector;
import stripper.Cholesky;
import stripper.CoupledMatrix_1;
import stripper.CoupledVector_1;
import stripper.Node;
import stripper.Strip;
import stripper.series.Series;

/**
 *
 * @author SJ
 *
 *
 *
 */
public class SystemEquation {

    List<Strip> strips = new ArrayList<>();
    List<Node> nodes = new ArrayList<>();
    int[][] localToGlobalConfNumbering;
    private Vector[] Uarr = new Vector[101];
    private double[] xData = new double[101];
    private double[] yData = new double[101];

    public double[] getxData() {
        return xData;
    }

    public double[] getyData() {
        return yData;
    }

    private ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(0);

    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }

    public SystemEquation(List<Strip> strips, List<Node> nodes) {
        this.strips = strips;
        this.nodes = nodes;

        if (ModelProperties.getFourierSeries().isSimplySupported()) {

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

    }

    public Vector[] getDisplacementVector() {

        for (int i = 0; i < 101; i++) {
            Uarr[i] = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);
            Uarr[i].clear();
        }

        Cholesky c = new Cholesky();

        Series Y = ModelProperties.getFourierSeries();

        System.out.println("Series simply suppported : " + Y.isSimplySupported());

        if (Y.isSimplySupported()) {

            Assembler a = new Assembler(strips, NodeTableUtil.getNodeList().size() * 4, localToGlobalConfNumbering);

            for (int i = 1; i < ModelProperties.getFourierTerms(); i++) {

                Vector temp = c.getX(a.getK(i), a.getF(i));

               
//                a.getK(i).printf("Global K " + i);
//                a.getF(i).printf("Global P " + i);

                for (int j = 0; j < 101; j++) {
                    Vector temp2 = Vector.getVector(NodeTableUtil.getNodeList().size() * 4);
                    temp2.add(temp);
                    
                    temp2.scale(Y.getFunctionValue(ModelProperties.getModelLength() * (j / 100.0), i));
                   
                    Uarr[j].add(temp2);
                    temp2.release();

                }

                progress.set((double) (i + 1) / (double) ModelProperties.getFourierTerms());
                //System.out.println(i+1/ModelProperties.getFourierTerms());
                temp.release();

            }

        }
        else {

            CoupledMatrix_1 cK = new CoupledMatrix_1(nodes.size(), ModelProperties.getFourierTerms());
            CoupledVector_1 fK = new CoupledVector_1(nodes.size(), ModelProperties.getFourierTerms());

//       Vector fK = Vector.getVector(8*nTerms);
//       fK.clear();
            for (int i = 1; i < ModelProperties.getFourierTerms() + 1; i++) {

                progress.set((double) (i + 1) / (double) ModelProperties.getFourierTerms());

                for (int j = 1; j < ModelProperties.getFourierTerms() + 1; j++) {

                    for (Strip myStrip : strips) {
                        cK.addStiffnessMatrix(myStrip.getRotatedStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
                    }

                }

                for (Strip myStrip : strips) {

                    fK.addForceVector(myStrip.getRotatedLoadVector(i), myStrip.getNode1(), myStrip.getNode2(), i);

                }

            }
            System.out.println("Assembly done, commencing cholesky");

            Cholesky chol = new Cholesky();
            Vector u = chol.getX(cK.getMatrix(), fK.getVector());

            System.out.println("Displacements calculated");

            for (Node n : nodes) {

                for (int i = 0; i < ModelProperties.getFourierTerms(); i++) {

                    for (int j = 0; j < 101; j++) {

                        Uarr[j].add(u.get((i * 4) + 4 * ModelProperties.getFourierTerms() * (n.getNodeId() - 1)) * Y.getFunctionValue(ModelProperties.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1));
                        Uarr[j].add(u.get((i * 4) + 4 * ModelProperties.getFourierTerms() * (n.getNodeId() - 1) + 1) * Y.getFunctionValue(ModelProperties.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1) + 1);
                        Uarr[j].add(u.get((i * 4) + 4 * ModelProperties.getFourierTerms() * (n.getNodeId() - 1) + 2) * Y.getFunctionValue(ModelProperties.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1) + 2);
                        Uarr[j].add(u.get((i * 4) + 4 * ModelProperties.getFourierTerms() * (n.getNodeId() - 1) + 3) * Y.getFunctionValue(ModelProperties.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1) + 3);
                    }
                }
            }

            System.out.println("Done ...");

        }

        for (int i = 0; i < 101; i++) {
            xData[i] = strips.get(0).getStripLength() * (i / 100.0);
            yData[i] = Uarr[i].get(2);
        }

        return Uarr;
    }

    public void setDisplacedState(int distPercentage) {

        for (int i = 0; i < NodeTableUtil.getNodeList().size(); i++) {
            NodeTableUtil.getNodeList().get(i).setDisplacedXCoord(Uarr[distPercentage].get((i * 4)));
            NodeTableUtil.getNodeList().get(i).setDisplacedZCoord(Uarr[distPercentage].get((i * 4 + 2)));
        }

    }

}
