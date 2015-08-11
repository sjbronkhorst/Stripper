/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;
import linalg.*;
import stripper.materials.Material;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public abstract class Strip {
    
    protected Matrix S; // Stifness Matrix
    protected Matrix R; // Rotation Matrix
    protected Matrix RSRT; // Rotated stiffness matrix
    protected double a ,t , beta; // length , thickness , angle
    protected Material mat;
   // protected Set<Node> nodes; 
    protected Node firstNode , lastNode;
    
    protected Vector forceVector;
    protected Series Ym;
   
    

public abstract Matrix getStiffnessMatrixInLocalCoordinates(int m);
public abstract Matrix getRotationMatrix();

public Matrix getStiffnessMatrixInGlobalCoordinates(int m)
{
  
       Matrix Sl = getStiffnessMatrixInLocalCoordinates(m);
    Matrix Rot = getRotationMatrix();
    
    Matrix RotT = Rot.transpose();
    
    Matrix RS = Rot.multiply(Sl);
    RSRT = RS.multiply(RotT);
   
    return RSRT;
}



    public double getStripLength() {
        return a;
    }

    public double getStripWidth() {
        return Math.sqrt(Math.pow((firstNode.getXCoord()- lastNode.getXCoord()),2) + Math.pow((firstNode.getZCoord()- lastNode.getZCoord()),2));
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
    
    public abstract Vector getLoadVector(int m);
    
    public abstract void addPointLoad(double x, double y, double magnitude);




}