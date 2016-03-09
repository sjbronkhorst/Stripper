/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import fsm.Assembler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import linalg.Matrix;
import linalg.Vector;
import Utils.Cholesky;
import fsm.Converter;
import fsm.CoupledMatrix;
import fsm.CoupledVector;
import fsm.Node;
import fsm.PartitionedSystem;
import fsm.Strip;
import fsm.series.Series;

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
    private Model model;

    public Vector[] fourierTermParameterContributions;

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

    public SystemEquation(Model model) {
        this.strips = model.getStripList();
        this.nodes = model.getNodeList();
        this.model = model;

        if (model.getFourierSeries().isSimplySupported()) {

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

    public void computeParameterVector() {

        fourierTermParameterContributions = new Vector[model.getFourierTerms()];

        for (int j = 0; j < model.getFourierTerms(); j++) {
            fourierTermParameterContributions[j] = Vector.getVector(model.getNodeList().size() * 4);
            fourierTermParameterContributions[j].clear();

        }

        Cholesky c = new Cholesky();

        Series Y = model.getFourierSeries();

        Main.println("Series simply suppported : " + Y.isSimplySupported());

        if (Y.isSimplySupported()) {

            // assemble status vector
            boolean[] status = new boolean[nodes.size() * 4];

            for (Node n : nodes) {
                status[(n.getNodeId() - 1) * 4] = n.getStatus()[0];
                status[(n.getNodeId() - 1) * 4 + 1] = n.getStatus()[1];
                status[(n.getNodeId() - 1) * 4 + 2] = n.getStatus()[2];
                status[(n.getNodeId() - 1) * 4 + 3] = n.getStatus()[3];

            }

            Assembler a = new Assembler(strips, model.getNodeList().size() * 4, localToGlobalConfNumbering);

            for (int i = 1; i <= model.getFourierTerms(); i++) {

                PartitionedSystem s = new PartitionedSystem(a.getK(i), status);
                Vector Wf = s.getWf(a.getF(i));

                //In the solution Uf = (Kff^-1)*(-Kfp*Up + Wf) ; Up is zero so we only need Wf     
                Vector temp = c.getX(s.getKff(), Wf);

                Vector temp2 = Vector.getVector(model.getNodeList().size() * 4);
                temp2.clear();
                int count = 0;
                for (int k = 0; k < temp2.size(); k++) {
                    if (!status[k]) {
                        temp2.set(temp2.get(k) + temp.get(count), k);
                        count++;
                    }

                }

                fourierTermParameterContributions[i - 1].add(temp2);

                temp2.release();

                progress.set((double) (i + 1) / (double) model.getFourierTerms());

                temp.release();

            }

        } else {

            CoupledMatrix cK = new CoupledMatrix(nodes.size(), model.getFourierTerms());
            CoupledVector fK = new CoupledVector(nodes.size(), model.getFourierTerms());
            CoupledVector status = new CoupledVector(nodes.size(), model.getFourierTerms());

//       Vector fK = Vector.getVector(8*nTerms);
//       fK.clear();
            for (int i = 1; i < model.getFourierTerms() + 1; i++) {

                progress.set((double) (i + 1) / (double) model.getFourierTerms());

                for (int j = 1; j < model.getFourierTerms() + 1; j++) {

                    for (Strip myStrip : strips) {
                        cK.addStiffnessMatrix(myStrip.getRotatedStiffnessMatrix(i, j), myStrip.getNode1(), myStrip.getNode2(), i, j);
                    }

                }

                for (Strip myStrip : strips) {

                    fK.addForceVector(myStrip.getRotatedLoadVector(i), myStrip.getNode1(), myStrip.getNode2(), i);
                    status.addForceVector(myStrip.getStatusVector(), myStrip.getNode1(), myStrip.getNode2(), i);

                }

            }

            PartitionedSystem se = new PartitionedSystem(cK.getMatrix(), Converter.vecToBool(status.getVector()));

            Main.println("Assembly done, commencing cholesky");

            Cholesky chol = new Cholesky();

            //Vector u = chol.getX(cK.getMatrix(), fK.getVector());
            Vector Wf = se.getWf(fK.getVector());
            Vector Uf = chol.getX(se.getKff(), Wf);

            Vector Us = Vector.getVector(cK.getMatrix().rows());
            Us.clear();
            int count = 0;
            for (int k = 0; k < Us.size(); k++) {
                if (status.getVector().get(k) == 0) {
                    Us.set(Us.get(k) + Uf.get(count), k);
                    count++;
                }

            }

            Main.println("Displacements calculated");

            for (Node n : nodes) {

                for (int i = 0; i < model.getFourierTerms(); i++) {

                        // this needs to change because in plane y displacement is scaled with dY
                    // Uarr[j].add(u.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1)) * Y.getFunctionValue(model.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1));
                    // Uarr[j].add(u.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1) + 1) * Y.getFunctionValue(model.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1) + 1);
                    // Uarr[j].add(u.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1) + 2) * Y.getFunctionValue(model.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1) + 2);
                    // Uarr[j].add(u.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1) + 3) * Y.getFunctionValue(model.getModelLength() * (j / 100.0), i + 1), 4 * (n.getNodeId() - 1) + 3);
                    fourierTermParameterContributions[i].add(Us.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1)), 4 * (n.getNodeId() - 1));
                    fourierTermParameterContributions[i].add(Us.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1) + 1), 4 * (n.getNodeId() - 1) + 1);
                    fourierTermParameterContributions[i].add(Us.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1) + 2), 4 * (n.getNodeId() - 1) + 2);
                    fourierTermParameterContributions[i].add(Us.get((i * 4) + 4 * model.getFourierTerms() * (n.getNodeId() - 1) + 3), 4 * (n.getNodeId() - 1) + 3);

                }
            }

            Main.println("Done ...");

        }

           // xData[i] = strips.get(0).getStripLength() * (i / 100.0);
        // yData[i] = Uarr[i].get(2);
        int[] indices = {0, 1, 2, 3};
        for (Node n : nodes) {

            for (int m = 0; m < model.getFourierTerms(); m++) {

                n.setParameterVector(fourierTermParameterContributions[m].getSubVector(indices), m);
            }

            for (int y = 0; y < 4; y++) {
                indices[y] = indices[y] + 4;
            }

        }

        //return Uarr;
    }

    public void setDisplacedState(int distPercentage) {

//        for (int i = 0; i < model.getNodeList().size(); i++) {
//            
//            
//            
//            model.getNodeList().get(i).setDisplacedXCoord(Uarr[distPercentage].get((i * 4)));
//            model.getNodeList().get(i).setDisplacedZCoord(Uarr[distPercentage].get((i * 4 + 2)));
//        }
        for (Node n : model.getNodeList()) {
            // n.setDisplacedXCoord(n.getBendingDisplacementVectorAt(distPercentage).get(0));
            //n.setDisplacedZCoord(n.getDisplacementVectorAt(distPercentage).get(2));
        }

    }

}
