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
public class Line {
    
    
    // y = mx + c
    //or
    // x = a
            
           
    double m,c,a;
    
    public Line(Node point , double slope)
    {
        m=slope;
        
        
        
        
        if(Double.isInfinite(m))
        {
            a = point.getX();
            c=Double.NaN;
        }
        else
        {
            
            c = point.getY() - m*point.getX();
            
        }
        
        
        
        
    }

    public double getM() {
        return m;
    }

    public double getC() {
        return c;
    }

    public double getA() {
        return a;
    }
    
    public double getY(double x)
    {
        return m*x+c;
    }
    
    
    public Node getIntersection(Line l)
    {
        double xCoord, yCoord;
        Node n = new Node(0,0);
        
        if(Double.isInfinite(m))
        {
            
            xCoord = getA();
            
            yCoord = l.getY(xCoord);
            
            
        }
        
        else if(Double.isInfinite(l.getM()))
        {
            
            xCoord = l.getA();
          
            yCoord = getY(xCoord);
        }      
        else
        {
            
            
            xCoord= (l.getC() - getC())/(getM()-l.getM());
            
            yCoord = getY(xCoord);
        }
        
        
        
        n.setX(xCoord);
        n.setY(yCoord);
        
        
        
        return n;
    }
    
    
    
    public static void main (String[]args)
    {
        Line l1 = new Line(new Node(0,1), 0.5);
        Line l2 = new Line(new Node(0,0), 0);
        
        
        System.out.println(l1.getIntersection(l2));
        
        
    }
    
    
}
