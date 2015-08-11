/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import UI.PointLoad;
import java.util.ArrayList;
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
public class Strip_LO2_General_Bending {

    private ArrayList<PointLoad> pointLoads = new ArrayList<>();
    private ArrayList<PointLoad> distLoadsX = new ArrayList<>();

    protected Matrix S; // Stifness Matrix
    protected Matrix R; // Rotation Matrix
    protected Matrix RSRT; // Rotated stiffness matrix
    protected double a, t, beta; // length , thickness , angle
    protected Material mat;
    // protected Set<Node> nodes; 
    protected Node firstNode, lastNode;
    protected boolean stiffnessCalculated = false;
    protected boolean stiffnessRotated = false;
    protected Vector forceVector;
    protected Series Ym;

    public Strip_LO2_General_Bending(Node firstNode, Node lastNode, double length, double thickness, Material material, Series boundaryConditions) {

        this.t = thickness;
        this.a = length;
        this.firstNode = firstNode;
        this.lastNode = lastNode;
        this.mat = material;
        

        beta = Math.atan((lastNode.getZCoord()- firstNode.getZCoord()) / (lastNode.getXCoord()- firstNode.getXCoord()));

        S = Matrix.getMatrix(4, 4);
        R = Matrix.getMatrix(4, 4);

        Ym = boundaryConditions;

    }
     public void addDistLoadX(double magnitude) {

        distLoadsX.add(new PointLoad(0, 0, magnitude));
    }
    
    public Vector getDistLoadVectorLocal(int m) { // Ek weet nie of dit reg is nie
        Vector F = Vector.getVector(4);
        F.clear();

        for (PointLoad p : distLoadsX) {
            
            
//           IterativeLegendreGaussIntegrator in = new IterativeLegendreGaussIntegrator(6, 0.95, 10);
//            
//           double I2 = in.integrate(2000, Ym.getFunction(m), 0, a);
//           
//            System.out.println("ILG = " + I2);
//            System.out.println("m = " + m);
//            System.out.println("a = " + a);
            
           // Integrator in = new Integrator();
            
           // double I = in.integrate(Ym.getFunction(m), 0, a);
            
            double magnitude = p.getMagnitude();

            
            double I = Ym.getYmIntegral(m, a);
            
//            System.out.println("I an = " + I);
//            System.out.println("m = " + m);
//            System.out.println("a = " + a);
            
            double c = magnitude;
            
            Vector f = Vector.getVector(4);

            f.set(I*getStripWidth()/2.0, 0);
            f.set(I*getStripWidth()*getStripWidth()/12.0, 1);
            f.set(I*getStripWidth()/2.0, 2);
            f.set(-I*getStripWidth()*getStripWidth()/12.0, 3);
            
            f.scale(c);

            F.add(f);
        }

        return F;
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

        double[] I = new double[5];

        //for (int i = 0; i < 5; i++) {
           // IterativeLegendreGaussIntegrator in = new IterativeLegendreGaussIntegrator(6, 0.95, 10);
          //  Integrator in = new Integrator();
            
            
            I = Ym.getIntegralValues(m, n);
            
            //try {
             //   I[i] = in.integrate(2000, Ym.getIntegralFunctions(m, n)[i], 0, a);
                
            //} catch (Exception e)
            //{
                //System.out.println("No convergence for m = " + m + " n = " + n + " i = " + i);
               // I[i] = 0;
            //}
            
//            if(I[i] < 0.00001)
//            {
//                I[i] = 0;
//            }
            
            
            
            //  System.out.println("I" + i + " = " + in.integrate(2000, Ym.getIntegralFunctions(m, n)[i], 0, a));
        //}

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

    public Vector getLoadVectorLocal(int m) {
        Vector F = Vector.getVector(4);

        for (PointLoad p : pointLoads) {
            double x = p.getX();
            double y = p.getY();
            double magnitude = p.getMagnitude();

            double c = magnitude * Ym.getFunctionValue(y, m);
            double xb = x / getStripWidth();

            Vector f = Vector.getVector(4);

            f.set(1 - 3 * xb * xb + 2 * xb * xb * xb, 0);
            f.set(x * (1 - 2 * xb + xb * xb), 1);
            f.set(3 * xb * xb - 2 * xb * xb * xb, 2);
            f.set(x * (xb * xb - xb), 3);
            f.scale(c);

            F.add(f);
        }

        return F;
    }
    
    public Vector getLoadVectorGlobal(int m) 
    {
        return getRotationMatrix().transpose().multiply(getLoadVectorLocal(m));
    }

    public void addPointLoad(double x, double y, double magnitude) {

        pointLoads.add(new PointLoad(x, y, magnitude));
    }

    public static void main(String[] args) {
        int modelLength = 300;
        Material usrMat = new Material_Steel();
        Node n1 = new Node(0, 0);
        Node n2 = new Node(10, 0);
        Node n3 = new Node(20, 0);
        Node n4 = new Node(30, 0);

        Strip_LO2_General_Bending s = new Strip_LO2_General_Bending(n1, n2, modelLength, 1, usrMat, new Series_SS(modelLength));

        s.getStiffnessMatrixInLocalCoordinates(1, 1);

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
