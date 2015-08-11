/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import linalg.Matrix;
import linalg.Vector;

import stripper.Node;
import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.series.Series;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class Strip {

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
    
    
    private Series Y;
        
    public static void clearNumbering()
    {
        stripSequence.set(0);
    }

    public Strip(Node node1, Node node2) {

        setNode1(node1);
        setNode2(node2);
        
        
                
    }

    

    public Strip() {
        
        hasNode1 = false;
        hasNode2 = false;
        this.node1Id.set(0);
        this.node2Id.set(0);
        
        
        
    }
    
    public ObservableList<PointLoad> getPointLoadList()
    {
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
    
    public void setUdlZ(double magnitude)
    {
        udlZ.set(magnitude);
    }
    
    public double getUdlZ()
    {
        return udlZ.doubleValue();
    }
    
    public void setUdlX(double magnitude)
    {
        udlX.set(magnitude);
    }
    
    public double getUdlX()
    {
        return udlX.doubleValue();
    }
    
    public void setUdlY(double magnitude)
    {
        udlY.set(magnitude);
    }
    
    public double getUdlY()
    {
        return udlY.doubleValue();
    }
    
    public boolean hasTwoNodes()
    {
       return hasNode1 && hasNode2;
    }

    public double getStripLength() {
        return a;
    }

    public double getStripWidth() {
        
        if(hasTwoNodes())
        {
        return Math.sqrt(Math.pow((node1.getXCoord() - node2.getXCoord()), 2) + Math.pow((node1.getZCoord() - node2.getZCoord()), 2));
        }
        return 0;
    }

    public double getStripThickness() {
        return t;
    }
    
    public double getStripAngle()
    {
        return Math.atan((node2.getZCoord() - node1.getZCoord()) / (node2.getXCoord() - node1.getXCoord()));
    }

    public Material getMaterial() {
        return mat;
    }
    
    public void setProperties(Material mat , double thickness , double length)
    {
        this.mat = mat;
        this.t = thickness;
        this.a = length;
        Y = new Series_SS(a);
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
    
    
    
    

    public Matrix getBendingStiffnessMatrix(int m, int n) {

        Matrix S = Matrix.getMatrix(4, 4);

        if (m == n) {

            double b = getStripWidth();
            double k = (m * Math.PI) / a;

            double Ex = mat.getEx();
            double Ey = mat.getEy();
            double vx = mat.getVx();
            double vy = mat.getVy();
            double G = mat.getG();

            double Dx = (Ex * t * t * t) / (12 * (1 - vx * vy));

            double Dy = (Ey * t * t * t) / (12 * (1 - vx * vy));

            double D1 = (vx * Ey * t * t * t) / (12 * (1 - vx * vy));

            double Dxy = G * t * t * t / 12;

            S.set((13 * a * b * k * k * k * k * Dy / 70) + ((12 * a * k * k * Dxy) / (5 * b)) + (6 * a * k * k * D1 / (5 * b)) + (6 * a * Dx / (b * b * b)), 0, 0);
            S.set(S.get(0, 0), 2, 2);

            S.set((3 * a * k * k * D1 / 5) + (a * k * k * Dxy / 5) + (3 * a * Dx / (b * b)) + (11 * a * b * b * k * k * k * k * Dy / (420)), 1, 0);
            S.set(S.get(1, 0), 0, 1);
            S.set(-S.get(1, 0), 3, 2);
            S.set(S.get(3, 2), 2, 3);

            S.set((a * b * b * b * k * k * k * k * Dy / 210) + (4 * a * b * k * k * Dxy / 15) + (2 * a * b * k * k * D1 / 15) + (2 * a * Dx / b), 1, 1);
            S.set(S.get(1, 1), 3, 3);

            S.set((9 * a * b * k * k * k * k * Dy / 140) - (12 * a * k * k * Dxy / (5 * b)) - (6 * a * k * k * D1 / (5 * b)) - (6 * a * Dx / (b * b * b)), 2, 0);
            S.set(S.get(2, 0), 0, 2);

            S.set(-(13 * a * b * b * k * k * k * k * Dy / 840) + (a * k * k * Dxy / 5) + (a * k * k * D1 / 10) + (3 * a * Dx / (b * b)), 3, 0);
            S.set(S.get(3, 0), 0, 3);

            S.set(-S.get(3, 0), 2, 1);
            S.set(S.get(2, 1), 1, 2);

            S.set(-(3 * a * b * b * b * k * k * k * k * Dy / 840) - (a * b * k * k * Dxy / 15) - (a * b * k * k * D1 / 30) + (a * Dx / b), 3, 1);
            S.set(S.get(3, 1), 1, 3);
        }

        if (m != n) {
            S.clear();
        }

        return S;
    }
    
    public Matrix getMembraneStiffnessMatrix(int m, int n) {
        
        Matrix M = Matrix.getMatrix(4, 4);

        double b = getStripWidth();

        double Ex = mat.getEx();
        double Ey = mat.getEy();
        double vx = mat.getVx();
        double vy = mat.getVy();
        double G = mat.getG();
        double k = (m * Math.PI) / a;

        

        
        double E1 = Ex/(1-vx*vy);
        double E2 = Ey/(1-vx*vy);
        
        
        

        double K11 = a*E1/(2.0*b)+(a*b*k*k*G)/6.0;

        double K12 = (a*k*vx*E2/4.0)-a*k*G/4.0;
        
        double K13 = -a*E1/(2*b)+a*b*k*k*G/12;
        
        double K14 = (a*k*vx*E2/4)+a*k*G/4;
        
        double K22 = (a*b*k*k*E2/6)+a*G/(2*b);
        
        double K23 = -(a*k*vx*E2/4)-a*k*G/4;
        
        double K24 = (a*b*k*k*E2/12)-a*G/(2*b);
        
        double K33 = K11;
        double K34 = -K12;
        
        double K44 = K22;
        
        double K21 = K12;
        double K31 = K13;
        double K32 = K23;
        double K41 = K14;
        double K42 = K24;
        double K43 = K34;
        
                

        M.set(K11, 0, 0);
        M.set(K12, 0, 1);
        M.set(K13, 0, 2);
        M.set(K14, 0, 3);

        M.set(K21, 1, 0);
        M.set(K22, 1, 1);
        M.set(K23, 1, 2);
        M.set(K24, 1, 3);

        M.set(K31, 2, 0);
        M.set(K32, 2, 1);
        M.set(K33, 2, 2);
        M.set(K34, 2, 3);

        M.set(K41, 3, 0);
        M.set(K42, 3, 1);
        M.set(K43, 3, 2);
        M.set(K44, 3, 3);
        
       M.scale(getStripThickness());

        return M;
    }
    
    public Matrix getStiffnessMatrix(int m , int n)
    {
        Matrix K = Matrix.getMatrix(8,8);
        
        K.clear();
        
        int [] bendingIndices = {2,3,6,7};
        
        K.addSubmatrix(getBendingStiffnessMatrix(m, n), bendingIndices);
        
        int [] membraneIndices = {0,1,4,5}; 
        
        K.addSubmatrix(getMembraneStiffnessMatrix(m, n), membraneIndices);
        
        
        return K;
    }
    
    public Matrix getRotatedStiffnessMatrix(int m , int n)
    {
        
        Matrix S = getStiffnessMatrix(m, n);
        Matrix R = getRotationMatrix();
        Matrix RT = R.transpose();
        
        Matrix RS = R.multiply(S);
        
        return RS.multiply(RT);
        
        
    }
    
    public static void main (String[]args)
    {
        Node n1 = new Node(0, 0);
        Node n2 = new Node(10, 0);
        
        Material mat = new Material_Steel();
        
        Strip s = new Strip(n1,n2);
        s.setProperties(mat , 1 , 200);
        
        
        s.getStiffnessMatrix(1, 1).printf("K ");
        
        s.getRotationMatrix().printf("R ");
        
        s.getRotatedLoadVector(1).printf("F ");
        
        System.out.println("Angle : " + s.getStripAngle());
        
    }
    
    public Matrix getRotationMatrix()
    {
        Matrix R = Matrix.getMatrix(8, 8);
        Matrix r = Matrix.getMatrix(4,4);
        
        beta = getStripAngle();
        
        double s = (node2.getZCoord() - node1.getZCoord())/getStripWidth();
        double c = (node2.getXCoord() - node1.getXCoord())/getStripWidth();
        
        r.clear();
        r.set(c, 0, 0);
        r.set(1 ,1,1);
        r.set(c,2,2);
        r.set(1,3,3);
        r.set(s,2,0);
        r.set(-s,0,2);
        
        int [] ind1 = {0,1,2,3};
        int [] ind2 = {4,5,6,7};
        
        R.clear();
        
        R.addSubmatrix(r, ind1);
        R.addSubmatrix(r, ind2);
        
        
        
        return R;
        
    }

    @Override
    public String toString() {
        return "Strip " + stripId.getValue().toString();
    }
    
    
           

}
