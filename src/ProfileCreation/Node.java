/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProfileCreation;

/**
 *
 * @author 16536096
 */
public class Node {
    
    private double x,y;
    private Node next = null;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    public void setNext(Node next)
    {
        this.next = next;
    }
    
    public Node next()
    {
        return next;
    }
    
    public String toString()
    {
        return "n," + getX() + "," + getY();
    }
    
    public double distanceTo(Node n)
    {
        double dx = x-n.getX();
        double dy = y-n.getY();
        
        return Math.sqrt(dx*dx + dy*dy);
        
    }
            
    
    
    
    
    
    
    
}
