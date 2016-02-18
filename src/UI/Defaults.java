/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import stripper.materials.Material;
import stripper.materials.Material_Steel;
import stripper.materials.Material_Z_Li;
import stripper.series.Series;

/**
 *
 * @author SJ
 */
public class Defaults
{
   
    private static Model baseModel = new Model();
    public static boolean ignoreCoupling = false;
    public static boolean bucklingAnalysis = false;
    
    public static int getNumberOfTerms()
    {
        return baseModel.getFourierTerms();
    }
    
    public static Model getBaseModel()
    {
        return baseModel;
    }
    
        
    
    
    
    
}
