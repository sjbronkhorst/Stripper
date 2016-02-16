/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author SJ
 */
public class BucklingCurve 
{
    private List<BucklingDataPoint> dataPoints = new ArrayList<>();
    
    
    
    
    public void addDataPoint(BucklingDataPoint point)
    {
        dataPoints.add(point);
    }
    
    public double [] getLoadFactors()
    {
        double [] data = new double [dataPoints.size()];
        
        int i =0;
        for (BucklingDataPoint p : dataPoints)
        {
            data[i] = p.getMinLoadFactor();
            i++;
        }
        
        return data;
    }
    
     public double [] getPhysicalLengths()
    {
        double [] data = new double [dataPoints.size()];
        
        int i =0;
        for (BucklingDataPoint p : dataPoints)
        {
            data[i] = p.getPhysicalLength();
            i++;
        }
        
        return data;
    }
    
    
    
}
