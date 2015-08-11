/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper.materials;

/**
 *
 * @author SJ
 */
public abstract class Material {

    protected String name;
    protected double Ex, Ey, vx, vy, G;

    

    public Material() {
        Ex = 1;
        Ey = 1;
        vx = 1;
        vy = 1;
        G = 1;
    }

    public String getName() {
        return name;
    }

    

    public double getEx() {
        return Ex;
    }

   

    public double getEy() {
        return Ey;
    }

   

    public double getVx() {
        return vx;
    }

    

    public double getVy() {
        return vy;
    }
    

    public double getG() {
        return G;
    }

   

}
