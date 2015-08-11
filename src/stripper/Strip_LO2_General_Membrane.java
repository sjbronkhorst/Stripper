/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

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
public class Strip_LO2_General_Membrane {

    private ArrayList<PointLoad> distLoadsX = new ArrayList<>();
    private ArrayList<PointLoad> pointLoadsY = new ArrayList<>();

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

    public Strip_LO2_General_Membrane(Node firstNode, Node lastNode, double length, double thickness, Material material, Series boundaryConditions) {

        this.t = thickness;
        this.a = length;
        this.firstNode = firstNode;
        this.lastNode = lastNode;
        this.mat = material;
        

        beta = Math.atan((lastNode.getZ() - firstNode.getZ()) / (lastNode.getX() - firstNode.getX()));

        S = Matrix.getMatrix(4, 4);
        R = Matrix.getMatrix(4, 4);

        Ym = boundaryConditions;

    }

    public double getStripLength() {
        return a;
    }

    public double getStripWidth() {
        return Math.sqrt(Math.pow((firstNode.getX() - lastNode.getX()), 2) + Math.pow((firstNode.getZ() - lastNode.getZ()), 2));
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

        for (int i = 0; i < 5; i++) {
            IterativeLegendreGaussIntegrator in = new IterativeLegendreGaussIntegrator(6, 0.95, 10);
            I[i] = in.integrate(2000, Ym.getIntegralFunctions(m, n)[i], 0, a);
            //  System.out.println("I" + i + " = " + in.integrate(2000, Ym.getIntegralFunctions(m, n)[i], 0, a));
        }

        double C1 = Ym.getMu_m(m)/a;
        double C2 = Ym.getMu_m(n)/a;
        
        double K1 = Ex/(1-vx*vy);
        double K2 = vx*Ey/(1-vx*vy);
        double K3 = Ey/(1-vx*vy);
        double K4 = G;
        

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
        
       S.scale(getStripThickness());

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

    public Vector getLoadVectorLocal(int m) { // Ek weet nie of dit reg is nie
        Vector F = Vector.getVector(4);
        F.clear();

        for (PointLoad p : distLoadsX) {
            
            
            IterativeLegendreGaussIntegrator in = new IterativeLegendreGaussIntegrator(6, 0.95, 10);
            
            double I = in.integrate(2000, Ym.getFunction(m), 0, a);
            
            double intgrl = (-a/(m*Math.PI))*(Math.cos(m*Math.PI)-1);
            
            double magnitude = p.getMagnitude();

            double c = magnitude * getStripWidth()/2.0;
            
            Vector f = Vector.getVector(4);

            f.set(I, 0);
            f.set(0, 1);
            f.set(I, 2);
            f.set(0, 3);
            f.scale(c);

            F.add(f);
        }

        return F;
    }
    
    public Vector getLoadVectorGlobal(int m) 
    {
        return getRotationMatrix().transpose().multiply(getLoadVectorLocal(m));
    }

    public void addDistLoadX(double magnitude) {

        distLoadsX.add(new PointLoad(0, 0, magnitude));
    }
     
    public void addPointLoadY(double x, double y, double magnitude) {

        pointLoadsY.add(new PointLoad(x, y, magnitude));
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
