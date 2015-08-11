/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import stripper.Direction;

/**
 *
 * @author SJ
 */
public class DistributedLoad 
{
    private double magnitude;
    private Direction direction;
    
    
    

    public DistributedLoad(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }
    
    public void setDirection(Direction direction)
    {
        this.direction = direction;
    }
    
    public Direction getDirection()
    {
        return direction;
    }
    
   
    
    
    
    
}


