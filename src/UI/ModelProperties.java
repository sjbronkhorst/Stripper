/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import stripper.materials.Material;
import stripper.materials.Material_Steel;

/**
 *
 * @author SJ
 */
public class ModelProperties 
{
    
  private static Material modelMaterial = new Material_Steel();
  private static double modelLength = 2000;
  private static double fourierTerms = 200;

    public static Material getModelMaterial() {
        return modelMaterial;
    }

    public static void setModelMaterial(Material modelMaterial) {
        ModelProperties.modelMaterial = modelMaterial;
    }

    public static double getModelLength() {
        return modelLength;
    }

    public static void setModelLength(double modelLength) {
        ModelProperties.modelLength = modelLength;
    }

    public static double getFourierTerms() {
        
        return fourierTerms;
    }

    public static void setFourierTerms(int fourierTerms) {
        ModelProperties.fourierTerms = fourierTerms;
    }
    
    
  
  
  
  
  
    
}
