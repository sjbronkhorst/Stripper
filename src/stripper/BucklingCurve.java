/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author SJ
 */
public class BucklingCurve 
{
    public Map<Double,BucklingDataPoint> dataPoints = new HashMap<>();
    
    
    
    
    public void addDataPoint(BucklingDataPoint point)
    {
        dataPoints.put(point.physicalLength,point);
    }
    
    public double [] getLoadFactors()
    {
        double [] data = new double [dataPoints.size()];
        
        int i =0;
        for (BucklingDataPoint p : dataPoints.values())
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
        for (BucklingDataPoint p : dataPoints.values())
        {
            data[i] = p.getPhysicalLength();
            i++;
        }
        
        return data;
    }
    
    
    
}
