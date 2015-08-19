/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import linalg.*;
import stripper.materials.Material;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public abstract class Strip {

    protected double a, t, beta; // length , thickness , angle
    protected Material mat;
    // protected Set<Node> nodes; 

    protected Vector forceVector;
    protected Series Y;

    protected static AtomicInteger stripSequence = new AtomicInteger(0);

    protected ObservableList<UI.PointLoad> pointLoads = FXCollections.<UI.PointLoad>observableArrayList();

    protected final ReadOnlyIntegerWrapper stripId = new ReadOnlyIntegerWrapper(this, "stripId", stripSequence.incrementAndGet());

    protected final ReadOnlyIntegerWrapper node1Id = new ReadOnlyIntegerWrapper(this, "node1Id", 0);
    protected final ReadOnlyIntegerWrapper node2Id = new ReadOnlyIntegerWrapper(this, "node2Id", 0);

    protected final ReadOnlyDoubleWrapper udlX = new ReadOnlyDoubleWrapper(this, "udlX", 0.0);
    protected final ReadOnlyDoubleWrapper udlY = new ReadOnlyDoubleWrapper(this, "udlY", 0.0);
    protected final ReadOnlyDoubleWrapper udlZ = new ReadOnlyDoubleWrapper(this, "udlZ", 0.0);

    protected Node node1;
    protected Node node2;

    protected boolean hasNode1, hasNode2;

    // private double distLoadZMagnitude = 0.00001;
    public static void clearNumbering() {
        stripSequence.set(0);
    }

    public ObservableList<UI.PointLoad> getPointLoadList() {
        return pointLoads;
    }

    public int getStripId() {
        return stripId.get();
    }

    public int getNode1Id() {
        return node1Id.get();
    }

    public int getNode2Id() {
        return node2Id.get();

    }

    public IntegerProperty node1IdProperty() {
        return node1Id;
    }

    public IntegerProperty node2IdProperty() {
        return node2Id;
    }

    public Node getNode1() {
        return node1;
    }

    public Node getNode2() {
        return node2;
    }

    public void setNode1(Node n) {
        node1 = n;
        node1IdProperty().set(n.getNodeId());
        hasNode1 = true;

    }

    public void setNode2(Node n) {
        node2 = n;
        node2IdProperty().set(n.getNodeId());
        hasNode2 = true;
    }

    public void setUdlZ(double magnitude) {
        udlZ.set(magnitude);
    }

    public double getUdlZ() {
        return udlZ.doubleValue();
    }

    public void setUdlX(double magnitude) {
        udlX.set(magnitude);
    }

    public double getUdlX() {
        return udlX.doubleValue();
    }

    public void setUdlY(double magnitude) {
        udlY.set(magnitude);
    }

    public double getUdlY() {
        return udlY.doubleValue();
    }

    public boolean hasTwoNodes() {
        return hasNode1 && hasNode2;
    }

    public abstract Matrix getRotationMatrix();

    public double getStripLength() {
        return a;
    }

    public double getStripWidth() {
        return Math.sqrt(Math.pow((node1.getXCoord() - node2.getXCoord()), 2) + Math.pow((node1.getZCoord() - node2.getZCoord()), 2));
    }

    public double getStripThickness() {
        return t;
    }

    public double getStripAngle() {
        return Math.atan((node2.getZCoord() - node1.getZCoord()) / (node2.getXCoord() - node1.getXCoord()));
    }

    public Material getMaterial() {
        return mat;
    }

    public Matrix getRotatedStiffnessMatrix(int m, int n) {

        Matrix S = getStiffnessMatrix(m, n);
        Matrix R = getRotationMatrix();
        Matrix RT = R.transpose();

        Matrix RS = R.multiply(S);

        return RS.multiply(RT);

    }

    public Vector getRotatedLoadVector(int m) {
        Matrix R = getRotationMatrix();
        Vector F = getLoadVector(m);

        Vector RF = R.multiply(F);

        return RF;
    }

    public abstract Vector getLoadVector(int m);

    public abstract void addPointLoad(double x, double y, double magnitude);

    public abstract Matrix getStiffnessMatrix(int n, int m);

    public abstract void setProperties(Material mat, double thickness, double a, Series Y);

}
