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
public class StraightSegment extends Segment {

    public StraightSegment(Node start, Node end, int nodeAmount) {

        
        setStart(start);
        setEnd(end);
        
        
        
        double xStep = (end.getX() - start.getX()) / nodeAmount;
        double yStep = (end.getY() - start.getY()) / nodeAmount;

        Node n = start;
        
        for (int i = 1; i < nodeAmount ; i++) {
            
            n.setNext(new Node(start.getX()+xStep*i,start.getY()+yStep*i));
            n = n.next();
            

        }
        
        n.setNext(end);

    }
    
    public String toString()
    {
        String s = start.toString()+"\n";
        
        Node n = start;
        while(n.next() != null)
        {
            
            n = n.next();
            s+= n.toString()+"\n";
            
        }
        
        return s;
    }
    
    public static void main (String [] args)
    {
        Node start = new Node(0, 0);
        Node end = new Node(10,10);
        
        
        StraightSegment s = new StraightSegment(start, end, 10);
        
        System.out.println(s.toString());
    }

}
