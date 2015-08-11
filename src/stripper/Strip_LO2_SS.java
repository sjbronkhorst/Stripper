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
import stripper.materials.*;
import stripper.series.Series_SS;

/**
 *
 * @author SJ
 */
public class Strip_LO2_SS extends Strip {

    private ArrayList<PointLoad> pointLoads = new ArrayList<>();
    private ArrayList<PointLoad> distLoadsX = new ArrayList<>();

    public Strip_LO2_SS(Node firstNode, Node lastNode, double length, double thickness, Material material) {
        this.t = thickness;
        this.a = length;
        this.firstNode = firstNode;
        this.lastNode = lastNode;
        this.mat = material;

        beta = Math.atan((lastNode.getZ() - firstNode.getZ()) / (lastNode.getX() - firstNode.getX()));

        S = Matrix.getMatrix(4, 4);
        R = Matrix.getMatrix(4, 4);

        Ym = new Series_SS(a);
        calcRotationMatrix();
    }

    
    @Override
    public Vector getLoadVector(int m) {
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
    
     public void addDistLoadX(double magnitude) {

        distLoadsX.add(new PointLoad(0, 0, magnitude));
    }
    
    public Vector getDistLoadVectorLocal(int m) { // Ek weet nie of dit reg is nie
        Vector F = Vector.getVector(4);
        F.clear();

        for (PointLoad p : distLoadsX) {
            
            
            IterativeLegendreGaussIntegrator in = new IterativeLegendreGaussIntegrator(6, 0.95, 10);
            
            double I = in.integrate(2000, Ym.getFunction(m), 0, a);
            
            double magnitude = p.getMagnitude();

            double c = magnitude;
            
            Vector f = Vector.getVector(4);

            f.set(I*getStripWidth()/2.0, 0);
            f.set(I*getStripWidth()*getStripWidth()/12.0, 1);
            f.set(I*getStripWidth()/2.0, 2);
            f.set(I*getStripWidth()*getStripWidth()/12.0, 3);
            f.scale(c);

            F.add(f);
        }

        return F;
    }

    @Override
    public void addPointLoad(double x, double y, double magnitude) {

        pointLoads.add(new PointLoad(x, y, magnitude));

    }

    private void calcRotationMatrix() {
        double cosb = Math.cos(beta);
        double sinb = Math.sin(beta);

        R.clear();

        R.set(cosb, 0, 0);
        R.set(1, 1, 1);
        R.set(cosb, 2, 2);
        R.set(1, 3, 3);

        R.set(-sinb, 0, 2);
        R.set(sinb, 2, 0);

    }

    private void calcStiffnessMatrix(int m) {

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

        S.add((13 * a * b * k * k * k * k * Dy / 70) + ((12 * a * k * k * Dxy) / (5 * b)) + (6 * a * k * k * D1 / (5 * b)) + (6 * a * Dx / (b * b * b)), 0, 0);
        S.add(S.get(0, 0), 2, 2);

        S.add((3 * a * k * k * D1 / 5) + (a * k * k * Dxy / 5) + (3 * a * Dx / (b * b)) + (11 * a * b * b * k * k * k * k * Dy / (420)), 1, 0);
        S.add(S.get(1, 0), 0, 1);
        S.add(-S.get(1, 0), 3, 2);
        S.add(S.get(3, 2), 2, 3);

        S.add((a * b * b * b * k * k * k * k * Dy / 210) + (4 * a * b * k * k * Dxy / 15) + (2 * a * b * k * k * D1 / 15) + (2 * a * Dx / b), 1, 1);
        S.add(S.get(1, 1), 3, 3);

        S.add((9 * a * b * k * k * k * k * Dy / 140) - (12 * a * k * k * Dxy / (5 * b)) - (6 * a * k * k * D1 / (5 * b)) - (6 * a * Dx / (b * b * b)), 2, 0);
        S.add(S.get(2, 0), 0, 2);

        S.add(-(13 * a * b * b * k * k * k * k * Dy / 840) + (a * k * k * Dxy / 5) + (a * k * k * D1 / 10) + (3 * a * Dx / (b * b)), 3, 0);
        S.add(S.get(3, 0), 0, 3);

        S.add(-S.get(3, 0), 2, 1);
        S.add(S.get(2, 1), 1, 2);

        S.add(-(3 * a * b * b * b * k * k * k * k * Dy / 840) - (a * b * k * k * Dxy / 15) - (a * b * k * k * D1 / 30) + (a * Dx / b), 3, 1);
        S.add(S.get(3, 1), 1, 3);

        stiffnessCalculated = true;
    }

    @Override
    public Matrix getStiffnessMatrixInLocalCoordinates(int m) {

        calcStiffnessMatrix(m);

        return S;
    }

    @Override
    public Matrix getRotationMatrix() {
        return R;
    }

}
