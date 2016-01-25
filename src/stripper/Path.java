/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;


import javafx.geometry.Point2D;

import linalg.Vector;

/**
 * A 2D path along a strip for probing values. 
 *
 * xData is the set of local x coordinates yData is the set of local y
 * coordinates queryData is the set of query data
 *
 * @author SJ
 */
public class Path {

    private Point2D start, end;
    private Strip strip;
    private double[] xData, yData;
    private Vector [][] queryData;
    private int numOfDataPoints;

    
    /**
     * 
     * @param start start of the path
     * @param end end of the path
     * @param numOfDataPoints number of total data point along the path (including start and end)
     */
    
    public Path(Point2D start, Point2D end, int numOfDataPoints, Strip strip) {

        this.start = start;
        this.end = end;
        this.strip = strip;
        this.numOfDataPoints = numOfDataPoints;
        

        double dx = start.getX() - end.getX();
        double dy = start.getY() - end.getY();

       // length = Math.sqrt(dy * dy + dx * dx);

        double idx = 0;
        double idy = 0;
        
        if(numOfDataPoints > 1)
        {
        idx = Math.abs(dx)/(numOfDataPoints-1);
        idy = Math.abs(dy)/(numOfDataPoints-1);
        }
       

       // incrementLength = Math.sqrt(idy * idy + idx * idx);
        
        
        
        

        xData = new double[numOfDataPoints];
        yData = new double[numOfDataPoints];
        queryData = new Vector[numOfDataPoints][4];
        
//        TableViewEdit.println("######################################");
//        TableViewEdit.println("Path start = " + start.toString());
//        TableViewEdit.println("Path end = " + end.toString());
//        TableViewEdit.println("######################################");
        
        for (int i = 0; i < numOfDataPoints; i++)
        {
            xData[i] = i*idx + start.getX();
            yData[i] = i*idy + start.getY();
            queryData[i][0] = strip.getBendingStressVectorAt(xData[i], 100*(int)(yData[i])/(int)(strip.a));
            queryData[i][1] = strip.getPlaneStressVectorAt(xData[i], 100*(int)(yData[i])/(int)(strip.a));
            queryData[i][2] = strip.getBendingDisplacementVectorAt(xData[i], 100*(int)(yData[i])/(int)(strip.a));
            queryData[i][3] = strip.getPlaneDisplacementVectorAt(xData[i], 100*(int)(yData[i])/(int)(strip.a));
            
            
//            TableViewEdit.println("-----------------------------------");
//            TableViewEdit.println("X = " + xData[i] + " Y = " + yData[i]);
//            TableViewEdit.println("-----------------------------------");
//            queryData[i][0].printf("Bending stress vector");
//            queryData[i][1].printf("Plane stress vector");
            
            
        }

    }
    
    public Vector [][] getQueryData()
    {
        return queryData;
    }

    public double[] getxData() {
        return xData;
    }

    public double[] getyData() {
        return yData;
    }

    public int getNumOfDataPoints() {
        return numOfDataPoints;
    }
    
    
    
    

}
