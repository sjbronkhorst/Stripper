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
        Node n2 = new Node(0,10);
        
        Node n3 = new Node(100,10);
        Node n4 = new Node(100,-90);
        
        Node n5 = new Node(0,-90);
        Node n6 = new Node(0,-80);
        Segment s;
        
        
        s = new StraightSegment(n1, n2, 10);
        new StraightSegment(n2, n3, 10);
        new StraightSegment(n3, n4, 10);
        new StraightSegment(n4, n5, 10);
        new StraightSegment(n5, n6, 10);
        
        Profile p = new Profile(s);
        
        System.out.println(p.toString());
        
        
    }
    
    
    
    
}
