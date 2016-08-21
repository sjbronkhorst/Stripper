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
public class Profile 
{
       Segment seg;
    
    public Profile(Segment seg)
    {
        this.seg = seg;
    }
    
    
    public void addSegment(Segment s)
    {
        
        
       seg.getEnd().setNext(s.getStart());
        
        
    }
    
    public String toString()
    {
        String s = seg.toString();
        int i = 1;
        
        Node n = seg.start;
        while(n.next() != null)
        {
            
            n = n.next();
            s+= "e,"+i+","+(i+1)+",3.0\n";
            i++;
            
        }
        
        
        return s;
    }
    
    
    public static void main (String [] args)
    {
        
        Node n1 = new Node(0, 0);
        Node n2 = new Node(0,14.1224);
        
        
        Node n3 = new Node(-4.7625,18.8849);
        Node n4 = new Node(-57.2389,18.8849);
        
        Node n5 = new Node(-62.0014,14.1224);
        Node n6 = new Node(-62.0014,-203.454);
        Node n7 = new Node(-57.2389,-208.2165);
        Node n8 = new Node(-4.7625,-208.2165);
        
        Node n9 = new Node(0,-203.454);
        
        Node n10 = new Node(0,-189.3316);
        
        
        
        
        
        
        Segment s;
        
        
        s = new StraightSegment(n1, n2, 5);
        new CircleSegment(n2, n3, 0, 90, 5);
        new StraightSegment(n3, n4, 5);
        new CircleSegment(n4, n5 ,90,180, 5);
        new StraightSegment(n5, n6, 5);
        new CircleSegment(n6, n7, 180, 270, 5);
        new StraightSegment(n7, n8, 5);
        new CircleSegment(n8, n9, 270, 360, 5);
        new StraightSegment(n9, n10, 5);
        
        
        Profile p = new Profile(s);
        
        System.out.println(p.toString());
        
        
    }
    
    
    
    
}
