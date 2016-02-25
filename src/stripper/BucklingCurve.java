/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stripper;

import UI.Model;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author SJ
 */
public class BucklingCurve 
{
   
    private List<Model> modelList = new ArrayList<>();
    private String name;
    private static int bcNum = 0;
   
            
    
    public BucklingCurve()
    {
       name = "Curve " + bcNum;
       bcNum ++;
    }
    
    public void addModel(Model model)
    {
       modelList.add(model);
    }
    
    public double [] getLoadFactors()
    {
        double [] data = new double [modelList.size()];
        
        int i =0;
        for (Model m : modelList)
        {
            data[i] = m.getBucklePoint().getMinLoadFactor();
            i++;
        }
        
        return data;
    }
    
     public double [] getPhysicalLengths()
    {
        double [] data = new double [modelList.size()];
        
        int i =0;
        for (Model m : modelList)
        {
            data[i] = m.getBucklePoint().getPhysicalLength();
            i++;
        }
        
        return data;
    }
     
     public BucklingDataPoint getPoint(double physicalLength)
     {
         for(Model m : modelList)
         {
             if(m.getBucklePoint().getPhysicalLength() == physicalLength)
             {
                 return m.getBucklePoint();
             }
         }
         
         System.out.println("No such point");
         return null;
     }
     
     
      public Model getModel(double physicalLength)
     {
         for(Model m : modelList)
         {
             if(m.getBucklePoint().getPhysicalLength() == physicalLength)
             {
                 return m;
             }
         }
         
         System.out.println("No such Model");
         return null;
     }

    public String getName() {
        return name;
    }
     
     
     
    
    
    
}
