/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Model;
import UI.TableViewEdit;
import java.awt.geom.Point2D;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.DoubleProperty;
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

    protected double beta; // thickness , angle

   // protected Series Y;
    protected final ReadOnlyDoubleWrapper t = new ReadOnlyDoubleWrapper(this, "t", 0.0);

    protected static AtomicInteger stripSequence = new AtomicInteger(0);

    protected ObservableList<UI.PointLoad> pointLoads = FXCollections.<UI.PointLoad>observableArrayList();

    protected ReadOnlyIntegerWrapper stripId = new ReadOnlyIntegerWrapper(this, "stripId", stripSequence.incrementAndGet());

    protected final ReadOnlyIntegerWrapper node1Id = new ReadOnlyIntegerWrapper(this, "node1Id", 0);
    protected final ReadOnlyIntegerWrapper node2Id = new ReadOnlyIntegerWrapper(this, "node2Id", 0);

    protected final ReadOnlyDoubleWrapper udlX = new ReadOnlyDoubleWrapper(this, "udlX", 0.0);
    protected final ReadOnlyDoubleWrapper udlY = new ReadOnlyDoubleWrapper(this, "udlY", 0.0);
    protected final ReadOnlyDoubleWrapper udlZ = new ReadOnlyDoubleWrapper(this, "udlZ", 0.0);

    protected Node node1;
    protected Node node2;
    // Edge load size at node.

    protected final ReadOnlyDoubleWrapper f1 = new ReadOnlyDoubleWrapper(this, "f1", 0.157);
    protected final ReadOnlyDoubleWrapper f2 = new ReadOnlyDoubleWrapper(this, "f2", 0.157);

    protected Model model;

    protected boolean hasNode1, hasNode2;

    public static void clearNumbering() {
        stripSequence.set(0);
    }

    public Vector getStatusVector() {
        Vector status = Vector.getVector(8);

        for (int i = 0; i < 4; i++) {
            status.set(Converter.boolToVec(node1.getStatus()).get(i), i);
            status.set(Converter.boolToVec(node2.getStatus()).get(i), i + 4);
        }

        return status;
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

    public boolean hasBothNodes() {
        return (hasNode1 && hasNode2);
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
        return model.getModelLength();
    }

    public double getStripWidth() {
        if(hasBothNodes())
        {
        return Math.sqrt(Math.pow((node1.getXCoord() - node2.getXCoord()), 2) + Math.pow((node1.getZCoord() - node2.getZCoord()), 2));
        }
        return 0;
    }

    public double getStripThickness() {
        return t.doubleValue();
    }

    public double getStripAngle() {
        if(hasBothNodes())
        {
        return Math.atan((node2.getZCoord() - node1.getZCoord()) / (node2.getXCoord() - node1.getXCoord()));
        }
        return 0;
    }

    public Material getMaterial() {
        return model.getModelMaterial();
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
        Series Y = model.getFourierSeries();
        double a = model.getModelLength();

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

        double b = getStripWidth();

        fp.set((b / 2.0) * udlX.doubleValue() * Y.getYmIntegral(m, a), 0);
        fp.set(udlY.doubleValue() * (a / Y.getMu_m(m) * Y.getFirstDerivativeIntegral(m)), 1);
        fp.set((b / 2.0) * udlX.doubleValue() * Y.getYmIntegral(m, a), 4);
        fp.set(udlY.doubleValue() * (a / Y.getMu_m(m) * Y.getFirstDerivativeIntegral(m)), 5);

        F.add(fp);

        //No information in Cheung on pointloads on LO2 strip in other directions
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

    public Matrix getBendingStrainMatrix(double x, double y, int m) {
        Matrix B = Matrix.getMatrix(3, 4);
        B.clear();
        Series Y = model.getFourierSeries();

        double b = getStripWidth();

        double s = Y.getFunctionValue(y, m);
        double s1 = Y.getFirstDerivativeValue(y, m);
        double s2 = Y.getSecondDerivativeValue(y, m);
        double xb = x / b;

        B.set((6.0 / (b * b)) * (1 - 2 * xb) * s, 0, 0);
        B.set((2.0 / b) * (2 - 3 * xb) * s, 0, 1);
        B.set((6.0 / (b * b)) * (-1 + 2 * xb) * s, 0, 2);
        B.set((2.0 / b) * (-3 * xb + 1) * s, 0, 3);

        B.set(-(1 - 3 * xb * xb + 2 * xb * xb * xb) * s2, 1, 0);
        B.set(-x * (1 - 2 * xb + xb * xb) * s2, 1, 1);
        B.set(-(3 * xb * xb - 2 * xb * xb * xb) * s2, 1, 2);
        B.set(-x * (xb * xb - xb) * s2, 1, 3);

        B.set((2.0 / b) * (-6.0 * xb + 6.0 * xb * xb) * s1, 2, 0);
        B.set(2.0 * (1 - 4 * xb + 3 * xb * xb) * s1, 2, 1);
        B.set((2.0 / b) * (6.0 * xb - 6.0 * xb * xb) * s1, 2, 2);
        B.set(2.0 * (3.0 * xb * xb - 2.0 * xb) * s1, 2, 3);

        return B;
    }

    public Matrix getPlaneStrainMatrix(double x, double y, int m) {
        Matrix B = Matrix.getMatrix(3, 4);
        B.clear();
        Series Y = model.getFourierSeries();
        double a = model.getModelLength();

        double b = getStripWidth();

        double s = Y.getFunctionValue(y, m);
        double s1 = Y.getFirstDerivativeValue(y, m);
        double s2 = Y.getSecondDerivativeValue(y, m);
        x = x / b;

        B.set((-1.0 / b) * s, 0, 0);
        B.set((1.0 / b) * s, 0, 2);
        B.set((1 - x) * (a / Y.getMu_m(m)) * s2, 1, 1);
        B.set(x * (a / Y.getMu_m(m)) * s2, 1, 3);
        B.set((1 - x) * s2, 2, 0);
        B.set((-1.0 / b) * (a / Y.getMu_m(m)) * s1, 2, 1);
        B.set(x * s1, 2, 2);
        B.set((1.0 / b) * (a / Y.getMu_m(m)) * s1, 2, 3);

        return B;
    }

    public Matrix getPlanePropertyMatrix() {

        Matrix D = Matrix.getMatrix(3, 3);
        D.clear();
        Material mat = model.getModelMaterial();

        double Ex = mat.getEx();
        double Ey = mat.getEy();
        double vx = mat.getVx();
        double vy = mat.getVy();
        double G = mat.getG();

        double E1 = Ex / (1 - vx * vy);
        double E2 = Ey / (1 - vx * vy);

        D.set(E1, 0, 0);
        D.set(vx * E2, 0, 1);
        D.set(vx * E2, 1, 0);
        D.set(E2, 1, 1);
        D.set(G, 2, 2);

        return D;

    }

    public Matrix getBendingPropertyMatrix() {
        Matrix D = Matrix.getMatrix(3, 3);
        D.clear();
        Material mat = model.getModelMaterial();
        double t = this.t.doubleValue();

        double Ex = mat.getEx();
        double Ey = mat.getEy();
        double vx = mat.getVx();
        double vy = mat.getVy();
        double G = mat.getG();

        double Dx = (Ex * t * t * t) / (12.0 * (1 - vx * vy));
        double Dy = (Ey * t * t * t) / (12.0 * (1 - vx * vy));
        double D1 = (vx * Ey * t * t * t) / (12.0 * (1 - vx * vy));
        double Dxy = G * t * t * t / 12.0;

        D.set(Dx, 0, 0);
        D.set(D1, 0, 1);

        D.set(D1, 1, 0);
        D.set(Dy, 1, 1);

        D.set(Dxy, 2, 2);

        return D;
    }

    public Matrix getPlaneDisplacementShapeFunctionMatrix(double x, double y, int m) {
        Matrix N = Matrix.getMatrix(2, 4);
        Series Y = model.getFourierSeries();
        double b = getStripWidth();
        double s = Y.getFunctionValue(y, m);
        double s1 = Y.getVScalingValue(y, m);
        double a = model.getModelLength();

        x = x / b;

        N.set((1 - x) * s, 0, 0);
        N.set(0, 0, 1);
        N.set(x * s, 0, 2);
        N.set(0, 0, 3);

        N.set(0, 1, 0);
        N.set((1 - x) * a / Y.getMu_m(m) * s1, 1, 1);
        N.set(0, 1, 2);
        N.set(x * a / Y.getMu_m(m) * s1, 1, 3);

        return N;
    }

    public Matrix getBendingDisplacementShapeFunctionMatrix(double x, double y, int m) {
        Matrix N = Matrix.getMatrix(2, 4);
        Series Y = model.getFourierSeries();
        double b = getStripWidth();
        double s = Y.getFunctionValue(y, m);
        double a = model.getModelLength();

        x = x / b;

        N.set((1 - x) * s, 0, 0);
        N.set(0, 0, 1);
        N.set(x * s, 0, 2);
        N.set(0, 0, 3);

        N.set(0, 1, 0);
        N.set((1 - x) * a / Y.getMu_m(m) * s, 1, 1);
        N.set(0, 1, 2);
        N.set(x * a / Y.getMu_m(m) * s, 1, 3);

        return N;
    }

    /**
     *
     * @param m fourier term number
     * @return the parameter vector associated with m
     */
    public Vector getParameterContributionVector(int m) {
        Vector U = Vector.getVector(8);

        int[] ind1 = {0, 1, 2, 3};
        int[] ind2 = {4, 5, 6, 7};

        U.add(node1.getParameterContributionVector(m), ind1);
        U.add(node2.getParameterContributionVector(m), ind2);

        return U;
    }

    /**
     *
     * @param localXCoordinate local x coordinate on the strip
     * @param localYCoordinate local y coordinate on the strip
     * @return the bending stress vector, in local coordinates at a given point
     * in the strip.
     */
    public Vector getBendingStressVector(double localXCoordinate, double localYCoordinate) {
        Vector ub = Vector.getVector(4);
        double a = model.getModelLength();
        Vector strain = Vector.getVector(3);
        strain.clear();

        for (int m = 0; m < model.getFourierTerms(); m++) {
            ub.clear();

            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(2), 0);
            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(3), 1);
            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(6), 2);
            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(7), 3);

            Matrix B = getBendingStrainMatrix(localXCoordinate, localYCoordinate * a, m + 1);

            strain.add(B.multiply(ub));

            B.release();
        }

        ub.release();
        return getBendingPropertyMatrix().multiply(strain);
    }

    public Vector getPlaneDisplacementVector(double localXCoordinate, double localYCoordinate) {
        Vector f = Vector.getVector(2);
        Vector param = Vector.getVector(4);
        double a = model.getModelLength();
        Matrix Nplane = Matrix.getMatrix(2, 4);

        for (int m = 0; m < model.getFourierTerms(); m++) {

            param.clear();

            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(0), 0);
            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(1), 1);
            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(4), 2);
            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(5), 3);

            Nplane = getPlaneDisplacementShapeFunctionMatrix(localXCoordinate, localYCoordinate * a, m + 1);
            f.add(Nplane.multiply(param));

        }

        param.release();
        Nplane.release();

        return f;
    }

    public Vector getGlobalPlaneDisplacementVector(double localXCoordinate, double localYCoordinate) {
        Vector f = Vector.getVector(2);
        Vector param = Vector.getVector(4);
        double a = model.getModelLength();
        Matrix Nplane = Matrix.getMatrix(2, 4);

        for (int m = 0; m < model.getFourierTerms(); m++) {

            param.clear();

            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(0), 0);
            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(1), 1);
            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(4), 2);
            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(5), 3);

            Nplane = getPlaneDisplacementShapeFunctionMatrix(localXCoordinate, localYCoordinate * a, m + 1);
            f.add(Nplane.multiply(param));

        }

        param.release();
        Nplane.release();

        return f;
    }

    public Vector getBendingDisplacementVector(double localXCoordinate, double localYCoordinate) {
        Vector w = Vector.getVector(2);
        Vector param = Vector.getVector(4);
        double a = model.getModelLength();
        Matrix Nbend = Matrix.getMatrix(2, 4);

        for (int m = 0; m < model.getFourierTerms(); m++) {

            param.clear();

            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(2), 0);
            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(3), 1);
            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(6), 2);
            param.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(7), 3);

            Nbend = getPlaneDisplacementShapeFunctionMatrix(localXCoordinate, localYCoordinate * a, m + 1);
            w.add(Nbend.multiply(param));

        }

        param.release();
        Nbend.release();

        return w;
    }

    public Vector getGlobalBendingDisplacementVector(double localXCoordinate, double localYCoordinate) {
        Vector w = Vector.getVector(2);
        Vector param = Vector.getVector(4);
        double a = model.getModelLength();
        Matrix Nbend = Matrix.getMatrix(2, 4);

        for (int m = 0; m < model.getFourierTerms(); m++) {

            param.clear();

            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(2), 0);
            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(3), 1);
            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(6), 2);
            param.add(/*getRotationMatrix().transpose().multiply*/(getParameterContributionVector(m)).get(7), 3);

            Nbend = getPlaneDisplacementShapeFunctionMatrix(localXCoordinate, localYCoordinate * a, m + 1);
            w.add(Nbend.multiply(param));

        }

        param.release();
        Nbend.release();

        return w;
    }

    /**
     *
     * @param localXCoordinate
     * @param yPercentage
     * @return the plane stress vector, in local coordinates at a given point in
     * the strip.
     */
    public Vector getPlaneStressVector(double localXCoordinate, double localYCoordinate) {
        Vector ub = Vector.getVector(4);
        double a = model.getModelLength();
        Vector strain = Vector.getVector(3);
        strain.clear();

        for (int m = 0; m < model.getFourierTerms(); m++) {
            ub.clear();

            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(0), 0);
            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(1), 1);
            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(4), 2);
            ub.add(getRotationMatrix().transpose().multiply(getParameterContributionVector(m)).get(5), 3);

            Matrix B = getPlaneStrainMatrix(localXCoordinate, localYCoordinate * a, m + 1);

            strain.add(B.multiply(ub));

            B.release();
        }

        ub.release();
        return getPlanePropertyMatrix().multiply(strain);
    }

    public Matrix getMembraneGeometricStiffnessMatrix(int m, int n) {
        double T1 = f1.doubleValue() * getStripThickness();
        double T2 = f2.doubleValue() * getStripThickness();
        double b = getStripWidth();
        Series Y = model.getFourierSeries();
        double a = model.getModelLength();
        double[] I = Y.getIntegralValues(m, n);
        double I4 = I[3];
        double I5 = I[4];

        double um = Y.getMu_m(m);
        double un = Y.getMu_m(n);

        Matrix Kgm = Matrix.getMatrix(4, 4);

        Kgm.set((3 * T1 + T2) * b * I5 / 12.0, 0, 0);
        Kgm.set(0, 0, 1);
        Kgm.set((T1 + T2) * b * I5 / 12.0, 0, 2);
        Kgm.set(0, 0, 3);

        Kgm.set(((3 * T1 + T2) * b * a * a * I4) / (12.0 * um * un), 1, 1);
        Kgm.set(0, 1, 2);
        Kgm.set(((T1 + T2) * b * a * a * I4) / (12.0 * um * un), 1, 3);

        Kgm.set((T1 + 3 * T2) * b * I5 / 12.0, 2, 2);
        Kgm.set(0, 2, 3);

        Kgm.set(((T1 + 3 * T2) * b * a * a * I4) / (12.0 * um * un), 3, 3);

        Kgm.set(Kgm.get(0, 1), 1, 0);

        Kgm.set(Kgm.get(0, 2), 2, 0);
        Kgm.set(Kgm.get(1, 2), 2, 1);

        Kgm.set(Kgm.get(0, 3), 3, 0);
        Kgm.set(Kgm.get(1, 3), 3, 1);
        Kgm.set(Kgm.get(2, 3), 3, 2);

        return Kgm;

    }

    public Matrix getBendingGeometricStiffnessMatrix(int m, int n) {

        double T1 = f1.doubleValue() * getStripThickness();
        double T2 = f2.doubleValue() * getStripThickness();
        Series Y = model.getFourierSeries();

        double b = getStripWidth();

        double[] I = Y.getIntegralValues(m, n);
        double I5 = I[4];

        Matrix Kgb = Matrix.getMatrix(4, 4);

        Kgb.set((10 * T1 + 3 * T2) * b * I5 / 35.0, 0, 0);
        Kgb.set((15 * T1 + 7 * T2) * b * b * I5 / 420.0, 0, 1);
        Kgb.set(9 * (T1 + T2) * b * I5 / 140.0, 0, 2);
        Kgb.set(-(7 * T1 + 6 * T2) * b * b * I5 / 420.0, 0, 3);

        Kgb.set((5 * T1 + 3 * T2) * b * b * b * I5 / 840.0, 1, 1);
        Kgb.set((6 * T1 + 7 * T2) * b * b * I5 / 420.0, 1, 2);
        Kgb.set(-(T1 + T2) * b * b * b * I5 / 280.0, 1, 3);

        Kgb.set((3 * T1 + 10 * T2) * b * I5 / 35.0, 2, 2);
        Kgb.set(-(7 * T1 + 15 * T2) * b * b * I5 / 420.0, 2, 3);

        Kgb.set((3 * T1 + 5 * T2) * b * b * b * I5 / 840.0, 3, 3);

        Kgb.set(Kgb.get(0, 1), 1, 0);

        Kgb.set(Kgb.get(0, 2), 2, 0);
        Kgb.set(Kgb.get(1, 2), 2, 1);

        Kgb.set(Kgb.get(0, 3), 3, 0);
        Kgb.set(Kgb.get(1, 3), 3, 1);
        Kgb.set(Kgb.get(2, 3), 3, 2);

        return Kgb;

    }

    public Matrix getGeometricMatrix(int m, int n) {
        Matrix K = Matrix.getMatrix(8, 8);

        K.clear();

        int[] bendingIndices = {2, 3, 6, 7};

        K.addSubmatrix(getBendingGeometricStiffnessMatrix(m, n), bendingIndices);

        int[] membraneIndices = {0, 1, 4, 5};

        K.addSubmatrix(getMembraneGeometricStiffnessMatrix(m, n), membraneIndices);

        return K;
    }

    public Matrix getRotatedGeometricMatrix(int m, int n) {
        Matrix S = getGeometricMatrix(m, n);
        Matrix R = getRotationMatrix();
        Matrix RT = R.transpose();

        Matrix RS = R.multiply(S);

        return RS.multiply(RT);
    }

    public abstract Matrix getStiffnessMatrix(int n, int m);

    public void setEdgeTractionAtNode1(double f1) {
        this.f1.set(f1);

    }

    public void setEdgeTractionAtNode2(double f2) {
        this.f2.set(f2);

    }

    public double getEdgeTractionAtNode1() {
        return f1.doubleValue();

    }

    public double getEdgeTractionAtNode2() {
        return f2.doubleValue();

    }

    public void setStripThickness(double thickness) {
        this.t.set(thickness);
    }

    public DoubleProperty thicknessProperty() {
        return t;
    }
    
    public DoubleProperty f1Property()
    {
        return f1;
    }
    
    public DoubleProperty f2Property()
    {
        return f2;
    }

    public double getCrossSectionalArea() {
        return getStripThickness() * getStripWidth();
    }

    public Point2D.Double getCrossSectionalCentroid() {
        return new Point2D.Double((node1.getXCoord() + node2.getXCoord()) / 2.0, (node1.getZCoord() + node2.getZCoord()) / 2.0);
    }

    public double getXBar() {
        return getCrossSectionalCentroid().getX();
    }

    public double getZBar() {
        return getCrossSectionalCentroid().getY();
    }
    
    public void clone(Strip strip)
    {
          if (!strip.hasBothNodes()) {

            hasNode1 = false;
            hasNode2 = false;
            this.node1Id.set(0);
            this.node2Id.set(0);
        } else {
            setNode1(strip.getNode1());
            setNode2(strip.getNode2());

            setUdlX(strip.getUdlX());
            setUdlY(strip.getUdlY());
            setUdlZ(strip.getUdlZ());
            
            setEdgeTractionAtNode1(strip.getEdgeTractionAtNode1());
            setEdgeTractionAtNode2(strip.getEdgeTractionAtNode2());

            this.stripId.set(strip.getStripId());

            this.pointLoads = strip.getPointLoadList();
            this.t.set(strip.getStripThickness());
            
        }
    }

}
