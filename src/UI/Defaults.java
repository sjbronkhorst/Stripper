/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fsm.BucklingCurve;
import fsm.material.Material;
import fsm.material.Material_Steel;
import fsm.material.Material_Z_Li;
import fsm.series.Series;

/**
 *
 * @author SJ
 */
public class Defaults
{
   
    private static Model baseModel = new Model();
    public static boolean ignoreCoupling = false;
    public static boolean bucklingAnalysis = false;
    public static boolean dsmAnalysis = false;
    public static List<BucklingCurve> bucklingCurveList = new ArrayList<BucklingCurve>();
   
   
    
    public static int getNumberOfTerms()
    {
        return baseModel.getFourierTerms();
    }
    
    public static Model getBaseModel()
    {
        return baseModel;
    }
    public static BucklingCurve getBucklinCurve(String name)
    {
        for (BucklingCurve bc : bucklingCurveList)
        {
         if(bc.getName().equals(name))
         {
             return bc;
         }
            
            
            
        }
        
        System.out.println("No such BucklingCurve");
        return null;
        
        
    }
    
        
    
    
    
    
}
