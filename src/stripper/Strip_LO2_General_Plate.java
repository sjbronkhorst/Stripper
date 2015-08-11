/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.PointLoad;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import linalg.Matrix;
import linalg.Vector;
import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.series.Series;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class Strip_LO2_General_Plate {

    private static AtomicInteger stripSequence = new AtomicInteger(0);
    
    private ObservableList<PointLoad> pointLoads = FXCollections.<PointLoad>observableArrayList();

    private final ReadOnlyIntegerWrapper stripId = new ReadOnlyIntegerWrapper(this, "stripId", stripSequence.incrementAndGet());

    private final ReadOnlyIntegerWrapper node1Id = new ReadOnlyIntegerWrapper(this, "node1Id", 0);
    private final ReadOnlyIntegerWrapper node2Id = new ReadOnlyIntegerWrapper(this, "node2Id", 0);
    
    private final ReadOnlyDoubleWrapper udlX = new ReadOnlyDoubleWrapper(this, "udlX", 0.0);
    private final ReadOnlyDoubleWrapper udlY = new ReadOnlyDoubleWrapper(this, "udlY", 0.0);
    private final ReadOnlyDoubleWrapper udlZ = new ReadOnlyDoubleWrapper(this, "udlZ", 0.0);

    private Node node1;
    private Node node2;
    
    
    
    private boolean hasNode1, hasNode2;
    
    
    private double a, t, beta;
    private Material mat;
    
   // private double distLoadZMagnitude = 0.00001;
    

    public static void clearNumbering()
    {
        stripSequence.set(0);
    }

    public Strip_LO2_General_Plate(Node node1, Node node2) {

        setNode1(node1);
        setNode2(node2);
        
        
                
    }

    

    public Strip_LO2_General_Plate() {
        
        hasNode1 = false;
        hasNode2 = false;
        this.node1Id.set(0);
        this.node2Id.set(0);
        
        
        
    }

    public double getStripLength() {
        return a;
    }

    public double getStripWidth() {
        return Math.sqrt(Math.pow((firstNode.getXCoord()- lastNode.getXCoord()), 2) + Math.pow((firstNode.getZCoord()- lastNode.getZCoord()), 2));
    }

    public double getStripThickness() {
        return t;
    }

    public Material getMaterial() {
        return mat;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public Node getLastNode() {
        return lastNode;
    }

    public Matrix getStiffnessMatrixInLocalCoordinates(int m, int n) {

        double b = getStripWidth();

        double Ex = mat.getEx();
        double Ey = mat.getEy();
        double vx = mat.getVx();
        double vy = mat.getVy();
        double G = mat.getG();

        double Dx = (Ex * t * t * t) / (12 * (1 - vx * vy));

        double Dy = (Ey * t * t * t) / (12 * (1 - vx * vy));

        double D1 = (vx * Ey * t * t * t) / (12 * (1 - vx * vy));

        double Dxy = G * t * t * t / 12;

        double[] I = Ym.getIntegralValues(m, n);

        
        
        
        
// u v w th
        double c = 1.0 / (420 * b * b * b);

        double K11 = c * (5040 * Dx * I[0]
                - 504 * b * b * D1 * I[1]
                - 504 * b * b * D1 * I[2]
                + 156 * b * b * b * b * Dy * I[3]
                + 2016 * b * b * Dxy * I[4]);

        double K12 = c * (2520 * b * Dx * I[0]
                - 462 * b * b * b * D1 * I[1]
                - 42 * b * b * b * D1 * I[2]
                + 22 * b * b * b * b * b * Dy * I[3]
                + 168 * b * b * b * Dxy * I[4]);

        double K13 = c * (-5040 * Dx * I[0]
                + 504 * b * b * D1 * I[1]
                + 504 * b * b * D1 * I[2]
                + 54 * b * b * b * b * Dy * I[3]
                - 2016 * b * b * Dxy * I[4]);

        double K14 = c * (2520 * b * Dx * I[0]
                - 42 * b * b * b * D1 * I[1]
                - 42 * b * b * b * D1 * I[2]
                - 13 * b * b * b * b * b * Dy * I[3]
                + 168 * b * b * b * Dxy * I[4]);

        double K21 = K12;

        double K22 = c * (1680 * b * b * Dx * I[0]
                - 56 * b * b * b * b * D1 * I[1]
                - 56 * b * b * b * b * D1 * I[2]
                + 4 * b * b * b * b * b * b * Dy * I[3]
                + 224 * b * b * b * b * Dxy * I[4]);

        double K23 = -K14;

        double K24 = c * (840 * b * b * Dx * I[0]
                + 14 * b * b * b * b * D1 * I[1]
                + 14 * b * b * b * b * D1 * I[2]
                - 3 * b * b * b * b * b * b * Dy * I[3]
                - 56 * b * b * b * b * Dxy * I[4]);

        double K31 = K13;
        double K32 = K23;
        double K33 = K11;
        double K34 = -K21;

        double K41 = K14;
        double K42 = K24;
        double K43 = K34;
        double K44 = K22;

        S.set(K11, 0, 0);
        S.set(K12, 0, 1);
        S.set(K13, 0, 2);
        S.set(K14, 0, 3);

        S.set(K21, 1, 0);
        S.set(K22, 1, 1);
        S.set(K23, 1, 2);
        S.set(K24, 1, 3);

        S.set(K31, 2, 0);
        S.set(K32, 2, 1);
        S.set(K33, 2, 2);
        S.set(K34, 2, 3);

        S.set(K41, 3, 0);
        S.set(K42, 3, 1);
        S.set(K43, 3, 2);
        S.set(K44, 3, 3);
        
       

        return S;
    }

    public Matrix getRotationMatrix() {
        double cosb = Math.cos(beta);
        double sinb = Math.sin(beta);
        
       
                
                
        R.clear();

        R.set(1, 0, 0);
        R.set(1, 1, 1);
        R.set(1, 2, 2);
        R.set(1, 3, 3);

        R.set(0, 0, 2);
        R.set(0, 2, 0);
        return R;
    }

      public Vector getLoadVector(int m)
    {
        
        // Bending udl
        Vector F = Vector.getVector(8);
        
        F.clear();
        
        
        F.set(getStripWidth()/2.0, 2);
        F.set(getStripWidth()*getStripWidth()/12.0,3);
        
        F.set(getStripWidth()/2.0, 6);
        F.set(-getStripWidth()*getStripWidth()/12.0,7);
        
        F.scale(Y.getYmIntegral(m, a)*udlZ.doubleValue());
        
        // In plane udl
        
        Vector fp = Vector.getVector(8);
        
        fp.set(udlX.doubleValue()*Y.getYmIntegral(m, a),0);
        fp.set(udlY.doubleValue()*(a/Y.getMu_m(m)*Y.getFirstDerivativeIntegral(m)),1);
        fp.set(udlX.doubleValue()*Y.getYmIntegral(m, a),4);
        fp.set(udlY.doubleValue()*(a/Y.getMu_m(m)*Y.getFirstDerivativeIntegral(m)),5);
        
        fp.scale(getStripWidth()/2.0);
        
        F.add(fp);
        
        
        
        
        //Pointloads
        
        for (PointLoad p : pointLoads) {
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
    
    public Vector getRotatedLoadVector(int m)
    {
        Matrix R = getRotationMatrix();
        Vector F = getLoadVector(m);
        
        Vector RF = R.multiply(F);
        
//        if(RF.get(2) < 0)
//        {
//            RF.set(-RF.get(3), 3);
//            RF.set(-RF.get(7), 7);
//        }
//        
        
        return RF;
    }

    public Matrix getStiffnessMatrixInGlobalCoordinates(int m) {

        Matrix Sl = getStiffnessMatrixInLocalCoordinates(m, m);
        Matrix Rot = getRotationMatrix();

        Matrix RotT = Rot.transpose();

        Matrix RS = Rot.multiply(Sl);
        RSRT = RS.multiply(RotT);

        return RSRT;
    }

}
