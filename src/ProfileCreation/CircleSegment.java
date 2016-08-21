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
public class CircleSegment extends Segment 
{
    public CircleSegment(Node start , Node end , double startDeg , double endDeg, int nodeAmount)
    {
       
        
        Line l1 = new Line(start, Math.tan(Math.toRadians(startDeg)));
        if(startDeg == 90)
        {
           l1 = new Line(start, Double.POSITIVE_INFINITY); 
        }
        
        
        
        Line l2 = new Line(end, Math.tan(Math.toRadians(endDeg)));
        
        this.start = start;
        this.end = end;
        
        double degreeIncrement = (endDeg - startDeg)/(double)nodeAmount; 
        
        Node centre = l1.getIntersection(l2);
        
        System.out.println("Centre : " + centre);
        
        double radius = start.distanceTo(centre);
        
        System.out.println("radius = " + radius);
        
        Node n = start;
        
        for (int i = 1; i < nodeAmount ; i++) {
            
            n.setNext(new Node(centre.getX() + radius*Math.cos(Math.toRadians(startDeg) + Math.toRadians(degreeIncrement)*i), centre.getY() + radius*Math.sin(Math.toRadians(startDeg) + Math.toRadians(degreeIncrement)*i)));
            n = n.next();
            

        }
        
        n.setNext(end);
        
        
        
        
        
        
    }
    
    public static void main (String[]args)
    {
        
        Node start = new Node(0, 0);
        Node end = new Node(0, 1);
        
        
        
        
        CircleSegment seg = new CircleSegment(start , end, -45, 45 , 10);
        
        System.out.println(seg.toString());
    }
    
}
