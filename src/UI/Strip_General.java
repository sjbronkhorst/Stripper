/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import java.util.concurrent.atomic.AtomicInteger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import linalg.Matrix;
import linalg.Vector;

import stripper.Node;
import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.series.Series;
import stripper.series.Series_CC;

/**
 *
 * @author SJ
 */
public class Strip_General {

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

    public Strip_General(Node node1, Node node2) {

        setNode1(node1);
        setNode2(node2);
        
        
                
    }

    

    public Strip_General() {
        
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
    
    public void setProperties(Material mat , double thickness , double length , Series Y)
    {
        this.mat = mat;
        this.t = thickness;
        this.a = length;
        this.Y = Y;
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
            
            
            /////////////////////////////////////////////////////////////////////////////////////////////////
            
double c=0;










//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
           // double c = magnitude * Y.getFunctionValue(y, m);
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
        
        Matrix S = Matrix.getMatrix(4,4);

       
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

        double[] I = new double[5];

            
            I = Y.getIntegralValues(m, n);
            
          
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
        
        for (int i = 0; i < 5; i++)
        {
            System.out.println("I"+ (i+1) + " = "+ I[i]);    
        }

        
        
        
        

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
    
    public Matrix getMembraneStiffnessMatrix(int m, int n) {
        
        Matrix M = Matrix.getMatrix(4, 4);

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

        double[] I = new double[5];

        
        
        I = Y.getIntegralValues(m, n);

        double C1 = Y.getMu_m(m)/a;
        double C2 = Y.getMu_m(n)/a;
        
        double K1 = Ex/(1-vx*vy);
        double K2 = vx*Ey/(1-vx*vy);
        double K3 = Ey/(1-vx*vy);
        double K4 = G;
        
        if(b == 0)
        {
            System.out.println("B is zero");
        }

        double K11 = K1*(1.0/b)*I[0]
                + K4*(b/3.0)*I[4];

        double K12 = K2*(-1.0/(2*C2))*I[2]
                    +K4*(-1.0/(2*C2))*I[4];
        
        double K13 = K1*(-1.0/(b))*I[0]
                    +K4*(b/6.0)*I[4];
        
        double K14 = K2*(-1.0/(2*C2))*I[2]
                    +K4*(1.0/(2*C2))*I[4];
        
        double K22 = K3*(b/(3*C1*C2))*I[3]
                    +K4*(1.0/(b*C1*C2))*I[4];
        
        double K23 = K2*(1.0/(2*C1))*I[1]
                    +K4*(-1.0/(2*C1))*I[4];
        
        double K24 = K3*(b/(6*C1*C2))*I[3]
                    +K4*(-1.0/(b*C1*C2))*I[4];
        
        double K33 = K11;
        double K34 = -K12;
        
        double K44 = K22;
        
        double K21 = K12;
        double K31 = K13;
        double K32 = -K14;
        double K41 = -K23;
        double K42 = K24;
        double K43 = -K21;
        
                

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
        Node n2 = new Node(100, 0);
        
        Material mat = new Material_Steel();
        Series_CC Y = new Series_CC(2000);
        
        Strip_General s = new Strip_General(n1,n2);
        s.setProperties(mat , 10 , 2000, Y);
        
        
        
       
        
        
        //s.getStiffnessMatrix(1, 1).printf("K ");
        
        
        
        
        
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
