/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fsm;

/**
 *
 * @author SJ
 */
public class PointLoad {
    
    private double x, y, magnitude;

    public PointLoad(double x, double y, double magnitude)
    {
        this.x = x;
        this.y = y;
        this.magnitude = magnitude;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMagnitude() {
        return magnitude;
    }
    
   
    
    
}
