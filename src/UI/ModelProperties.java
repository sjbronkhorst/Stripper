/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import linalg.Vector;
import stripper.BucklingCurve;
import stripper.BucklingDataPoint;
import stripper.Node;
import stripper.Strip;
import stripper.Strip_General;
import stripper.Strip_SS;
import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.materials.Material_Z_Li;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public class ModelProperties {

    private static Material modelMaterial = new Material_Z_Li();
    private static double modelLength = 100;
    private static int fourierTerms = 2;
    private static Series fourierSeries = Series.getSerieslList().get(0);
    public static boolean ignoreCoupling = false;
    public static boolean bucklingAnalysis = false;
    public static BucklingCurve bucklingCurve;

    private static ObservableList<Strip> strips = FXCollections.<Strip>observableArrayList();

    public static Material getModelMaterial() {
        return modelMaterial;
    }

    public static void setModelMaterial(Material modelMaterial) {
        ModelProperties.modelMaterial = modelMaterial;
    }

    public static double getModelLength() {
        return modelLength;
    }

    public static void setModelLength(double modelLength) {
        ModelProperties.modelLength = modelLength;
        fourierSeries.setLength(modelLength);
    }

    public static int getFourierTerms() {

        return fourierTerms;
    }

    public static void setFourierTerms(int fourierTerms) {
        ModelProperties.fourierTerms = fourierTerms;
        TableViewEdit.println("Fourier terms set to " + fourierTerms);
    }

    public static Series getFourierSeries() {
        return fourierSeries;
    }

    public static void setFourierSeries(Series fourierSeries) {
        ModelProperties.fourierSeries = fourierSeries;
        ModelProperties.fourierSeries.setLength(modelLength);
        ModelProperties.getFourierSeries().computeAllIntegrals(ModelProperties.getFourierTerms());
    }

    public static ObservableList<Strip> getStripList() {
        return strips;
    }

    /**
     * Strips should only be added to the model once all data is known e.g.
     * boundary conditions
     *
     * @param uistrip is a dummy strip that can be displayed and has no
     * mathematical significance. It helps with the separation of model and
     * viewer.
     */
    public static void addStrip(UIStrip uistrip) {
        if (ModelProperties.fourierSeries.isSimplySupported()) {
            strips.add(new Strip_SS(uistrip));
        } else {
            strips.add(new Strip_General(uistrip));
        }
    }

    public static double getXBar() {
        double sumAx = 0;
        double sumA = 0;

        for (UIStrip strip : StripTableUtil.getStripList()) {
            sumAx += strip.getCrossSectionalArea() * strip.getXBar();
            sumA += strip.getCrossSectionalArea();

        }

        return sumAx / sumA;
    }

    public static double getZBar() {
        double sumAz = 0;
        double sumA = 0;

        for (UIStrip strip : StripTableUtil.getStripList()) {
            sumAz += strip.getCrossSectionalArea() * strip.getZBar();
            sumA += strip.getCrossSectionalArea();

        }

        return sumAz / sumA;
    }

    public static void setDisplacedState(BucklingDataPoint point) {

        double scale = 20.0;
        int[] indices = {0, 1, 2, 3};

        for (Node n : NodeTableUtil.getNodeList()) {

            for (int m = 0; m < ModelProperties.getFourierTerms(); m++) {

                n.setParameterVector(point.getFreeParamVector(m).getSubVector(indices), m);
            }

            for (int y = 0; y < 4; y++) {
                indices[y] = indices[y] + 4;
            }

        }

        Vector zVec = Vector.getVector(NodeTableUtil.getNodeList().size());
        Vector xVec = Vector.getVector(NodeTableUtil.getNodeList().size());

        for (Strip s : strips) {

            zVec.set(s.getGlobalBendingDisplacementVector(0, modelLength / (2*(point.getMinIndex()+1))).get(0), s.getNode1Id() - 1);
            xVec.set(s.getGlobalPlaneDisplacementVector(0, modelLength / (2*(point.getMinIndex()+1))).get(0), s.getNode1Id() - 1);

            zVec.set(s.getGlobalBendingDisplacementVector(s.getStripWidth(), modelLength / (2*(point.getMinIndex()+1))).get(0), s.getNode2Id() - 1);
            xVec.set(s.getGlobalPlaneDisplacementVector(s.getStripWidth(), modelLength / (2*(point.getMinIndex()+1))).get(0), s.getNode2Id() - 1);

        }

        zVec.normalize();
        xVec.normalize();

        zVec.scale(scale);
        xVec.scale(scale);

        for (Node n : NodeTableUtil.getNodeList()) {

            n.setDisplacedZCoord(zVec.get(n.getNodeId() - 1));
            n.setDisplacedXCoord(xVec.get(n.getNodeId() - 1));

        }

    }

}
