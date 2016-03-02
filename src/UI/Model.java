/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import linalg.Matrix;
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
public class Model {

    private Material modelMaterial = new Material_Steel();
    private double modelLength = 100;
    private int fourierTerms = 1;
    private Series fourierSeries = Series.getSerieslList().get(0);

    private ObservableList<Strip> strips = FXCollections.<Strip>observableArrayList();
    private ObservableList<Node> nodes = FXCollections.<Node>observableArrayList();
    //private Map<Integer, Node> nodeMap = new HashMap();
    private BucklingDataPoint bucklePoint;

    public Model(Model modelToClone) {
        modelMaterial = modelToClone.getModelMaterial();
        modelLength = modelToClone.getModelLength();
        fourierTerms = modelToClone.getFourierTerms();
        fourierSeries = modelToClone.getFourierSeries();

    }

    public BucklingDataPoint getBucklePoint() {
        return bucklePoint;
    }

    public void setBucklePoint(BucklingDataPoint bucklePoint) {
        this.bucklePoint = bucklePoint;
    }

    public Model() {

    }

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

    /**
     * DO NOT ADD STRIPS HERE ! use the addStrip method to avoid errors caused
     * by reference calls
     *
     * @return
     */
    public ObservableList<Strip> getStripList() {
        return strips;
    }

    /**
     * DO NOT ADD NODES HERE ! use the addNode method to avoid errors caused by
     * reference calls
     *
     * @return
     */
    public ObservableList<Node> getNodeList() {
        return nodes;
    }

//    public Map<Integer, Node> getNodeMap() {
//        return nodeMap;
//    }
    /**
     * Strips should only be added to the model once all data is known e.g.
     * boundary conditions
     *
     * @param uistrip is a dummy strip that can be displayed and has no
     * mathematical significance. It helps with the separation of model and
     * viewer.
     */
//    public void addStrip(UIStrip uistrip) {
//        if (fourierSeries.isSimplySupported()) {
//
//            Strip s = new Strip_SS(uistrip, this);
//            s.setNode1(getNode(uistrip.getNode1Id()));
//            s.setNode2(getNode(uistrip.getNode2Id()));
//            strips.add(s);
//        } else {
//            Strip s = new Strip_General(uistrip, this);
//            s.setNode1(getNode(uistrip.getNode1Id()));
//            s.setNode2(getNode(uistrip.getNode2Id()));
//            strips.add(s);
//        }
//
//    }
    public void clearStrips() {
        int i = strips.size();

        for (int j = 0; j < i; j++) {
            strips.remove(0);

        }

    }

    public void removeStrip(Strip n) {
        strips.remove(n);

    }

    public void addStrip(Strip s) {

        Strip strip = this.fourierSeries.getStrip(this);

        if (s.hasBothNodes()) {
            strip.setNode1(getNode(s.getNode1Id()));
            strip.setNode2(getNode(s.getNode2Id()));
        }
        strip.setStripThickness(s.getStripThickness());
        strip.setEdgeTractionAtNode1(s.getEdgeTractionAtNode1());
        strip.setEdgeTractionAtNode2(s.getEdgeTractionAtNode2());
        strips.add(strip);
    }

    public void addStrip() {

        Strip strip = this.fourierSeries.getStrip(this);
        strips.add(strip);
    }

    public void addNode(Node n) {
        Node node = new Node(n.getXCoord(), n.getZCoord(), this);

        nodes.add(node);

    }

    public void addNode() {
        Node node = new Node(0, 0, this);

        nodes.add(node);

    }

    public Node getNode(int Id) {
        for (Node n : nodes) {

            if (n.getNodeId() == Id) {
                return n;
            }

        }

        System.out.println("No such node");
        return null;
    }

    public void removeNode(Node n) {
        nodes.remove(n);

    }

    public void clearNodes() {
        int i = nodes.size();

        for (int j = 0; j < i; j++) {
            // nodeMap.remove(nodes.get(0).getNodeId(), nodes.get(0));
            nodes.remove(0);

        }
    }

    public double getXBar() {
        double sumAx = 0;
        double sumA = 0;

        for (Strip strip : Defaults.getBaseModel().getStripList()) {
            sumAx += strip.getCrossSectionalArea() * strip.getXBar();
            sumA += strip.getCrossSectionalArea();

        }

        return sumAx / sumA;
    }

    public double getZBar() {
        double sumAz = 0;
        double sumA = 0;

        for (Strip strip : Defaults.getBaseModel().getStripList()) {
            sumAz += strip.getCrossSectionalArea() * strip.getZBar();
            sumA += strip.getCrossSectionalArea();

        }

        return sumAz / sumA;
    }

    public double getIxx() {

        //I = sum(Ixx +Ad^2)
        double I = 0;
        double xbar = getXBar();

        for (Strip s : strips) {
            double d = xbar - s.getXBar();

            I = I + s.getIxx() + s.getCrossSectionalArea() * d * d;
        }

        return I;
    }

    public double getIzz() {

        //I = sum(Ixx +Ad^2)
        double I = 0;
        double zbar = getZBar();

        for (Strip s : strips) {
            double d = zbar - s.getZBar();

            I = I + s.getIzz() + s.getCrossSectionalArea() * d * d;
        }

        return I;
    }

    public double getIxz() // product of inertia
    {

        // I = Ixz + Adxdy
        double I = 0;
        double zbar = getZBar();
        double xbar = getXBar();

        for (Strip s : strips) {
            double dz = zbar - s.getZBar();
            double dx = xbar - s.getXBar();

            I = I + s.getIxz() + s.getCrossSectionalArea() * dx * dz;
        }

        return I;

    }

    public double getPrincipalAxisAngle() {
        double Ixz = getIxz();
        double Ixx = getIxx();
        double Izz = getIzz();

        return Math.atan((-Ixz * 2.0) / (Ixx - Izz)) / 2.0;
    }

    public double getIxxPrincipal() {
        double Ixz = getIxz();
        double Ixx = getIxx();
        double Izz = getIzz();
        double theta = getPrincipalAxisAngle();

        return ((Ixx + Izz) / 2.0) + ((Ixx - Izz) / 2.0)*Math.cos(2*theta) - Ixz * Math.sin(2 * theta);
        
    }

    public double getIzzPrincipal() {
         double Ixz = getIxz();
        double Ixx = getIxx();
        double Izz = getIzz();
        double theta = getPrincipalAxisAngle();

        return ((Ixx + Izz) / 2.0) - ((Ixx - Izz) / 2.0)*Math.cos(2*theta) + Ixz * Math.sin(2 * theta);
    }

    public double getZx() {
        return getIxx() / getXBar();
    }

    public double getZz() {
        return getIzz() / getZBar();
    }

    public void setDisplacedState(BucklingDataPoint point) {

        double scale = 40.0;
        int[] indices = {0, 1, 2, 3};

        for (Node n : nodes) {

            for (int m = 0; m < getFourierTerms(); m++) {

                n.setParameterVector(point.getFreeParamVector(m).getSubVector(indices), m);
            }

            for (int y = 0; y < 4; y++) {
                indices[y] = indices[y] + 4;
            }

        }

        Vector zVec = Vector.getVector(nodes.size());
        Vector xVec = Vector.getVector(nodes.size());

        for (Strip s : strips) {

            zVec.set(s.getGlobalBendingDisplacementVector(0, modelLength / (2 * (point.getMinIndex() + 1))).get(0), s.getNode1Id() - 1);
            xVec.set(s.getGlobalPlaneDisplacementVector(0, modelLength / (2 * (point.getMinIndex() + 1))).get(0), s.getNode1Id() - 1);

            zVec.set(s.getGlobalBendingDisplacementVector(s.getStripWidth(), modelLength / (2 * (point.getMinIndex() + 1))).get(0), s.getNode2Id() - 1);
            xVec.set(s.getGlobalPlaneDisplacementVector(s.getStripWidth(), modelLength / (2 * (point.getMinIndex() + 1))).get(0), s.getNode2Id() - 1);

        }

        zVec.normalize();
        xVec.normalize();

        zVec.scale(scale);
        xVec.scale(scale);

        if (zVec.get(0) > 0) // Arbitrary rule to draw all vectors in same direction
        {
            zVec.scale(-1);
            xVec.scale(-1);
        }

        for (Node n : nodes) {

            n.setDisplacedZCoord(zVec.get(n.getNodeId() - 1));
            n.setDisplacedXCoord(xVec.get(n.getNodeId() - 1));

        }

    }

}
