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
public class Model{

    private Material modelMaterial = new Material_Steel();
    private double modelLength = 100;
    private int fourierTerms = 1;
    private Series fourierSeries = Series.getSerieslList().get(0);
    public BucklingCurve bucklingCurve;

    private ObservableList<Strip> strips = FXCollections.<Strip>observableArrayList();
    private ObservableList<Node> nodes = FXCollections.<Node>observableArrayList();

    public Material getModelMaterial() {
        return modelMaterial;
    }

    public void setModelMaterial(Material modelMaterial) {
        this.modelMaterial = modelMaterial;
    }

    public double getModelLength() {
        return modelLength;
    }

    public void setModelLength(double modelLength) {
        this.modelLength = modelLength;
        fourierSeries.setLength(modelLength);
    }

    public int getFourierTerms() {

        return fourierTerms;
    }

    public void setFourierTerms(int fourierTerms) {
        this.fourierTerms = fourierTerms;
        TableViewEdit.println("Fourier terms set to " + fourierTerms);
    }

    public Series getFourierSeries() {
        return fourierSeries;
    }

    public void setFourierSeries(Series fourierSeries) {
        this.fourierSeries = fourierSeries;
        this.fourierSeries.setLength(modelLength);
        getFourierSeries().computeAllIntegrals(getFourierTerms());
    }

    public ObservableList<Strip> getStripList() {
        return strips;
    }
    
     public ObservableList<Node> getNodeList() {
        return nodes;
    }

    /**
     * Strips should only be added to the model once all data is known e.g.
     * boundary conditions
     *
     * @param uistrip is a dummy strip that can be displayed and has no
     * mathematical significance. It helps with the separation of model and
     * viewer.
     */
    public void addStrip(UIStrip uistrip) {
        if (fourierSeries.isSimplySupported()) {
            strips.add(new Strip_SS(uistrip, this));
        } else {
            strips.add(new Strip_General(uistrip,this));
        }
    }

    public double getXBar() {
        double sumAx = 0;
        double sumA = 0;

        for (UIStrip strip : StripTableUtil.getStripList()) {
            sumAx += strip.getCrossSectionalArea() * strip.getXBar();
            sumA += strip.getCrossSectionalArea();

        }

        return sumAx / sumA;
    }

    public double getZBar() {
        double sumAz = 0;
        double sumA = 0;

        for (UIStrip strip : StripTableUtil.getStripList()) {
            sumAz += strip.getCrossSectionalArea() * strip.getZBar();
            sumA += strip.getCrossSectionalArea();

        }

        return sumAz / sumA;
    }

    public void setDisplacedState(BucklingDataPoint point) {

        double scale = 20.0;
        int[] indices = {0, 1, 2, 3};

        for (Node n : NodeTableUtil.getNodeList()) {

            for (int m = 0; m < getFourierTerms(); m++) {

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
