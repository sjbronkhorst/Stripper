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

    @Override
    public String toString() {
        return "Strip " + stripId.getValue().toString();
    }

    public Matrix getRotationMatrix() {
        Matrix R = Matrix.getMatrix(8, 8);
        Matrix r = Matrix.getMatrix(4, 4);

        double s = (node2.getZCoord() - node1.getZCoord()) / getStripWidth();
        double c = (node2.getXCoord() - node1.getXCoord()) / getStripWidth();

        r.clear();
        r.set(c, 0, 0);
        r.set(1, 1, 1);
        r.set(c, 2, 2);
        r.set(1, 3, 3);
        r.set(s, 2, 0);
        r.set(-s, 0, 2);

        int[] ind1 = {0, 1, 2, 3};
        int[] ind2 = {4, 5, 6, 7};

        R.clear();

        R.addSubmatrix(r, ind1);
        R.addSubmatrix(r, ind2);

        return R;

    }

    public Vector getLoadVector(int m) {

        // Bending udl
        Vector F = Vector.getVector(8);

        F.clear();

        if (udlZ.doubleValue() != 0.0) {

            F.set(getStripWidth() / 2.0, 2);
            F.set(getStripWidth() * getStripWidth() / 12.0, 3);

            F.set(getStripWidth() / 2.0, 6);
            F.set(-getStripWidth() * getStripWidth() / 12.0, 7);

            F.scale(Y.getYmIntegral(m, a) * udlZ.doubleValue());
        }

        // In plane udl
        Vector fp = Vector.getVector(8);

        fp.set(udlX.doubleValue() * Y.getYmIntegral(m, a), 0);
        fp.set(udlY.doubleValue() * (a / Y.getMu_m(m) * Y.getFirstDerivativeIntegral(m)), 1);
        fp.set(udlX.doubleValue() * Y.getYmIntegral(m, a), 4);
        fp.set(udlY.doubleValue() * (a / Y.getMu_m(m) * Y.getFirstDerivativeIntegral(m)), 5);

        fp.scale(getStripWidth() / 2.0);

        F.add(fp);

        //Pointloads
        for (UI.PointLoad p : pointLoads) {
            double x = p.getX();
            double y = p.getY();
            double magnitude = p.getMagnitude();

            double c = magnitude * Y.getFunctionValue(y, m);
            double xb = x / getStripWidth();

            Vector f = Vector.getVector(8);

            f.set(1 - 3 * xb * xb + 2 * xb * xb * xb, 2);
            f.set(x * (1 - 2 * xb + xb * xb), 3);
            f.set(3 * xb * xb - 2 * xb * xb * xb, 6);
            f.set(x * (xb * xb - xb), 7);
            f.scale(c);

            F.add(f);
        }

        return F;
    }

    

    public abstract Matrix getStiffnessMatrix(int n, int m);

    public abstract void setProperties(Material mat, double thickness, double a, Series Y);

}
